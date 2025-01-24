package com.nttbank.microservices.creditservice.service;

import com.nttbank.microservices.creditservice.model.entity.Credit;
import com.nttbank.microservices.creditservice.model.entity.CreditTransactions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service interface for credit operations.
 */
public interface CreditService {

  Flux<Credit> findAll();

  Mono<CreditTransactions> save(Credit credit);

  Mono<Credit> preview(Credit credit);

  Mono<CreditTransactions> payInstallment(String creditId, String installmentNumber);

  Mono<Credit> findById(String creditId);
}
