package com.example.paymentprovider.service;

import com.example.paymentprovider.model.Transaction;
import com.example.paymentprovider.model.TransactionStatus;
import com.example.paymentprovider.repository.WebhookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class NotificationService {

    WebhookRepository webhookRepository;
    public Mono<Void> sendTransactionNotification(Transaction transaction, String message, TransactionStatus status){

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


        return null;
    }
}
