package com.nttbank.microservices.creditservice.service;


import com.nttbank.microservices.creditservice.model.response.MovementResponse;
import com.nttbank.microservices.creditservice.proxy.openfeign.CloudGatewayFeign;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Service class responsible for handling movement-related operations.
 */
@Service
@RequiredArgsConstructor
public class MovementService {

  private final CloudGatewayFeign feignMovement;

  public Mono<MovementResponse> saveMovement(MovementResponse movement) {
    return feignMovement.saveMovement(movement);
  }
}
