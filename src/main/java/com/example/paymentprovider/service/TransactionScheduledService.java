package com.example.paymentprovider.service;

import com.example.paymentprovider.model.Transaction;
import com.example.paymentprovider.model.TransactionStatus;
import com.example.paymentprovider.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class TransactionScheduledService {

    private final TransactionRepository transactionRepository;
    private final CardRepository cardRepository;
    private final MerchantWalletRepository merchantWalletRepository;
    private final MerchantRepository merchantRepository;
    private final WebhookRepository webhookRepository;

    @Scheduled(fixedDelay = 10 * 1000)
    private void doPayment() {
        transactionRepository.findAllByStatus(TransactionStatus.IN_PROCESS)
                //сначала approewd потом меняю баланс
                .flatMap(transaction -> {

                    cardRepository.findById(transaction.getCardId())
                            .flatMap(card -> {
                                card.setAmount(card.getAmount() - transaction.getAmount());
                                return null;
                            })
                            .flatMap(nothing -> {
                                merchantRepository.findById(transaction.getMerchantId())
                                        .flatMap(merchant -> merchantWalletRepository.findById(merchant.getWallet_id()))
                                        .flatMap(merchantWallet -> {
                                            merchantWallet.setAmount(merchantWallet.getAmount() + transaction.getAmount());
                                            merchantWalletRepository.save(merchantWallet);
                                            return null;
                                        }).subscribe();
                                return null;
                            }).flatMap(noData -> sendNotification(transaction))
                            .subscribe();

                    return null;


                }).subscribe();

    }

    private Mono<Void> sendNotification(Transaction transaction) {
        webhookRepository.findById(transaction.getWebhookId())
                .flatMap(webhook -> {
                    WebClient client = WebClient.builder()
                            .baseUrl(webhook.getNotificationUrl())
                            .build();
                    return client
                            .get()
                            .retrieve()
                            .toBodilessEntity()
                            .flatMap(response -> {
                                webhook.setResponseCode(response.getStatusCode().value());
                                return webhookRepository.save(webhook);
                            });


                }).subscribe();

        return Mono.empty();
    }

}
