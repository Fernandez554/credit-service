package com.nttbank.microservices.creditservice.service;

import com.nttbank.microservices.creditservice.model.entity.Credit;
import reactor.core.publisher.Mono;

/** Service interface for credit operations. */
public interface CreditService {

  Mono<Credit> save(Credit credit);

  Mono<Credit> preview(Credit credit);
}
