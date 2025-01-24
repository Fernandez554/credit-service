package com.nttbank.microservices.creditservice.repo;

import com.nttbank.microservices.creditservice.model.entity.CreditTransactions;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface ICreditTransactionRepo extends
    ReactiveMongoRepository<CreditTransactions, String> {
  Flux<CreditTransactions> findAllByCreditId(String creditId);

}
