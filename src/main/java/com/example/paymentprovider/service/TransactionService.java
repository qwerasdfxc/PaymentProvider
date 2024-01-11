package com.example.paymentprovider.service;

import com.example.paymentprovider.dto.TransactionDTO;
import com.example.paymentprovider.dto.TransactionResponse;
import com.example.paymentprovider.mapper.TransactionMapper;
import com.example.paymentprovider.model.Transaction;
import com.example.paymentprovider.model.TransactionStatus;
import com.example.paymentprovider.model.Webhook;
import com.example.paymentprovider.repository.CardRepository;
import com.example.paymentprovider.repository.MerchantRepository;
import com.example.paymentprovider.repository.TransactionRepository;
import com.example.paymentprovider.repository.WebhookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    private final TransactionMapper transactionMapper;

    private final CardRepository cardRepository;

    private final MerchantRepository merchantRepository;

    private final WebhookRepository webhookRepository;


    public Mono<TransactionResponse> saveTransaction(TransactionDTO transactionDTO, ServerWebExchange serverWebExchange) {
        transactionDTO.setStatus(TransactionStatus.IN_PROCESS);
        return cardRepository.findFirstByCardNumber(transactionDTO.getCardDTO().getCardNumber())
                .flatMap(card -> {
                            transactionDTO.setCardId(card.getId());
//                            return Mono.just(transactionDTO);
                            return merchantRepository.findFirstBySecret(serverWebExchange.getRequest().getHeaders().getFirst("Authorization"))
                                    .flatMap(merchant -> {
                                        transactionDTO.setMerchantId(merchant.getId());
                                        return webhookRepository.save(Webhook.builder().notificationUrl(transactionDTO.getNotificationURL()).attempt(0).build())
                                                .flatMap(webhook -> {
                                                    transactionDTO.setWebhookId(webhook.getId());
                                                    return transactionRepository.save(transactionMapper.convert(transactionDTO))
                                                            .map(transaction -> TransactionResponse.builder().externalTransactionId(transaction.getId()).status("APPROVED").message("OK").build());
                                                });

                                    });
                        }
                );
//                .flatMap(transactionDTO2 ->
//                        merchantRepository.findFirstBySecret(serverWebExchange.getRequest().getHeaders().getFirst("Authorization"))
//                                .flatMap(merchant -> Mono.just(merchant.getId()))
//                                .flatMap(merchantId -> {
//                                    transactionDTO.setMerchantId(merchantId);
//                                    return webhookRepository.save(Webhook.builder().notificationUrl(transactionDTO.getNotificationURL()).attempt(0).build())
//                                            .flatMap(webhook -> {
//                                                transactionDTO.setWebhookId(webhook.getId());
//                                                return transactionRepository.save(transactionMapper.convert(transactionDTO))
//                                                        .map(transaction -> TransactionResponse.builder().externalTransactionId(transaction.getId()).status("APPROVED").message("OK").build());
//                                            });
//
//                                })
//                );
    }
    //получил транзакцию -  in progress
    //потом проверки и присваивает fail или approved
//    public Mono<TransactionResponse> checkTransaction(TransactionDTO transactionDTO) {
//        return cardRepository.existsByCardNumber(transactionDTO.getCardDTO().getCardNumber()).flatMap(exists -> {
//            if (!exists)
//                return Mono.just(TransactionResponse.builder().status("FAILED").message("PAYMENT_METHOD_NOT_ALLOWED").build());
//            //спросить что делать если не найдена карта, как обработать ошибку
//            return cardRepository.findFirstByCardNumber(transactionDTO.getCardDTO().getCardNumber())
//                    .switchIfEmpty(Mono.error(new AuthenticationException()))
//                    .flatMap(cardFromDB -> {
//                        if ((int) (Math.random() * 5) == 1)
//                            return Mono.just(TransactionResponse.builder().status("FAILED").message("PAYMENT_METHOD_NOT_ALLOWED").build());
//
//                        if (cardFromDB.getAmount() < transactionDTO.getAmount())
//                            return Mono.just(TransactionResponse.builder().status("FAILED").message("PAYMENT_METHOD_NOT_ALLOWED").build());
//
//                        Transaction transaction = transactionMapper.convert(transactionDTO);
//                        transaction.setCardId(cardFromDB.getId());
//                        transaction.setStatus(TransactionStatus.IN_PROCESS);
////                                transaction.setMerchantId();
//                        return transactionRepository.save(transaction).flatMap(transaction1 -> Mono.just(TransactionResponse.builder().status("APPROVED").message("OK").externalTransactionId(transaction1.getId()).build()));
////                            return Mono.just(TransactionResponse.builder().status("APPROVED").message("OK").externalTransactionId(123L).build());
//                    });
//        })
//
//
////                .flatMap(card -> {
////                    if(card.getAmount() < transactionDTO.getAmount())
////                        return Mono.just(TransactionResponse.builder().status("FAILED").message("PAYMENT_METHOD_NOT_ALLOWED").build());
////                    else {
////                        return Mono.just(TransactionResponse.builder().status("APPROVED").message("OK").externalTransactionId(123L).build());
////                    }
////                })
////                .onErrorComplete(InvalidDataException.class)
//                ;
//    }
}
