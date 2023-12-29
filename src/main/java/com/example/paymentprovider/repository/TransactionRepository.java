package com.example.paymentprovider.repository;

import com.example.paymentprovider.model.Transaction;
import com.example.paymentprovider.model.TransactionStatus;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

public interface TransactionRepository extends R2dbcRepository<Transaction, Long> {

    Flux<Transaction> findAllByStatus(TransactionStatus status);
}
