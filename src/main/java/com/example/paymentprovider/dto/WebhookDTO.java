package com.example.paymentprovider.dto;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
public class WebhookDTO {

    private Long id;

    private String notificationUrl;

    private int attempt;

    private Long lastAttempt;

    private Long responseCode;

}
