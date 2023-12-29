package com.example.paymentprovider.dto;

import com.example.paymentprovider.model.Merchant;
import com.example.paymentprovider.model.TransactionStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
public class TransactionDTO {

    private Long id;

    @JsonProperty("payment_method")
    private String paymentMethod;

    private String currency;

    private Double amount;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String language;

    @JsonProperty("notification_url")
    private String notificationURL;

    private TransactionStatus status;

    private MerchantDTO merchantDTO;

    @JsonProperty("customer")
    private CustomerDTO customerDTO;

    private WebhookDTO webhookDTO;

    @JsonProperty("card_data")
    private CardDTO cardDTO;

    private Long merchantId;

    private Long clientId;

    private Long webhookId;

    private Long cardId;

}
