package com.example.paymentprovider.service;

import com.example.paymentprovider.dto.TransactionDTO;
import com.example.paymentprovider.dto.TransactionResponse;
import com.example.paymentprovider.exception.InvalidDataException;
import com.example.paymentprovider.mapper.TransactionMapper;
import com.example.paymentprovider.model.Transaction;
import com.example.paymentprovider.model.TransactionStatus;
import com.example.paymentprovider.repository.CardRepository;
import com.example.paymentprovider.repository.MerchantRepository;
import com.example.paymentprovider.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.naming.AuthenticationException;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    private final TransactionMapper transactionMapper;

    private final CardRepository cardRepository;

    private final MerchantRepository merchantRepository;

    //получил транзакцию -  in progress
    //потом проверки и присваивает fail или approved
    public Mono<TransactionResponse> checkTransaction(TransactionDTO transactionDTO) {
        return cardRepository.existsByCardNumber(transactionDTO.getCardDTO().getCardNumber()).flatMap(exists -> {
            if (!exists)
                return Mono.just(TransactionResponse.builder().status("FAILED").message("PAYMENT_METHOD_NOT_ALLOWED").build());
            //спросить что делать если не найдена карта, как обработать ошибку
            return cardRepository.findFirstByCardNumber(transactionDTO.getCardDTO().getCardNumber())
                    .switchIfEmpty(Mono.error(new AuthenticationException()))
                    .flatMap(cardFromDB -> {
                        if ((int) (Math.random() * 5) == 1)
                            return Mono.just(TransactionResponse.builder().status("FAILED").message("PAYMENT_METHOD_NOT_ALLOWED").build());

                        if (cardFromDB.getAmount() < transactionDTO.getAmount())
                            return Mono.just(TransactionResponse.builder().status("FAILED").message("PAYMENT_METHOD_NOT_ALLOWED").build());

                        Transaction transaction = transactionMapper.convert(transactionDTO);
                        transaction.setCardId(cardFromDB.getId());
                        transaction.setStatus(TransactionStatus.IN_PROCESS);
//                                transaction.setMerchantId();
                        return transactionRepository.save(transaction).flatMap(transaction1 -> Mono.just(TransactionResponse.builder().status("APPROVED").message("OK").externalTransactionId(transaction1.getId()).build()));
//                            return Mono.just(TransactionResponse.builder().status("APPROVED").message("OK").externalTransactionId(123L).build());
                    });
        })


//                .flatMap(card -> {
//                    if(card.getAmount() < transactionDTO.getAmount())
//                        return Mono.just(TransactionResponse.builder().status("FAILED").message("PAYMENT_METHOD_NOT_ALLOWED").build());
//                    else {
//                        return Mono.just(TransactionResponse.builder().status("APPROVED").message("OK").externalTransactionId(123L).build());
//                    }
//                })
//                .onErrorComplete(InvalidDataException.class)
                ;
    }
}
