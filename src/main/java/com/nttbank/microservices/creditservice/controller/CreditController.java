package com.nttbank.microservices.creditservice.controller;

import com.nttbank.microservices.creditservice.model.entity.Credit;
import com.nttbank.microservices.creditservice.service.CreditService;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/** Controller class to handle HTTP requests for credit operations. */
@RestController
@RequestMapping("/credits")
@RequiredArgsConstructor
@Validated
public class CreditController {

  private final CreditService creditService;

  @PostMapping("/preview")
  public Mono<ResponseEntity<Credit>> preview(@Valid @RequestBody Credit credit,
      final ServerHttpRequest req) {
    return creditService.preview(credit).map(c -> ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_JSON).body(c))
        .defaultIfEmpty(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
  }

  @PostMapping
  public Mono<ResponseEntity<Credit>> save(@Valid @RequestBody Credit credit,
      final ServerHttpRequest req) {
    return creditService.save(credit).map(c -> ResponseEntity.created(
                URI.create(req.getURI().toString().concat("/").concat(c.getId())))
            .contentType(MediaType.APPLICATION_JSON).body(c))
        .defaultIfEmpty(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
  }


}
