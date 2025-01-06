package com.nttbank.microservices.creditservice.proxy.openfeign;

import com.nttbank.microservices.creditservice.model.response.CustomerResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Mono;

/**
 * Feign client interface for interacting with the external microservices endpoints. This client
 * allows making reactive HTTP requests.
 */
@ReactiveFeignClient(name = "cloud-gateway")
public interface CloudGatewayFeign {

  @GetMapping("/api/customer-service/customers/{customer_id}")
  Mono<CustomerResponse> findCustomerById(@PathVariable("customer_id") String customerId);

}
