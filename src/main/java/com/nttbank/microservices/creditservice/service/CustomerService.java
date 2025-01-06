package com.nttbank.microservices.creditservice.service;

import com.nttbank.microservices.creditservice.model.response.CustomerResponse;
import com.nttbank.microservices.creditservice.proxy.openfeign.CloudGatewayFeign;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Service class responsible for handling customer-related operations.
 * This service interacts with a Feign client to retrieve customer data.
 */
@Service
@RequiredArgsConstructor
public class CustomerService {

  private final CloudGatewayFeign feignCustomer;

  public Mono<CustomerResponse> findCustomerById(String customerId) {
    return feignCustomer.findCustomerById(customerId);
  }

}
