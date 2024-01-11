package com.example.paymentprovider.controller;

import com.example.paymentprovider.dto.TransactionDTO;
import com.example.paymentprovider.dto.TransactionResponse;
import com.example.paymentprovider.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("transaction")
    public Mono<TransactionResponse> createTransaction(ServerWebExchange serverWebExchange, @RequestBody TransactionDTO transactionDTO){
        return transactionService.saveTransaction(transactionDTO, serverWebExchange);
//                .onErrorReturn(TransactionResponse.builder().status("FAILED").message("PAYMENT_METHOD_NOT_ALLOWED").build());
    }
}
