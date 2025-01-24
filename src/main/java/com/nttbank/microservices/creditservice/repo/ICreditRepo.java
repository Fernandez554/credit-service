package com.nttbank.microservices.creditservice.repo;

import com.nttbank.microservices.creditservice.model.entity.Credit;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface ICreditRepo extends ReactiveMongoRepository<Credit, String> {
  Mono<Long> countByCustomerIdAndStatus(String customerId, String status);
}
