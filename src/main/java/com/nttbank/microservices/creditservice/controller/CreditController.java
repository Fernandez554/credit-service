package com.nttbank.microservices.creditservice.controller;

import com.nttbank.microservices.creditservice.dto.CreditDTO;
import com.nttbank.microservices.creditservice.mapper.CreditMapper;
import com.nttbank.microservices.creditservice.model.entity.Credit;
import com.nttbank.microservices.creditservice.model.entity.CreditTransactions;
import com.nttbank.microservices.creditservice.service.CreditService;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Controller class to handle HTTP requests for credit operations.
 */
@RestController
@RequestMapping("/credits")
@RequiredArgsConstructor
@Validated
public class CreditController {

  private final CreditService creditService;
  private final CreditMapper mapper;

  @GetMapping
  public Mono<ResponseEntity<Flux<Credit>>> findAll() {
    Flux<Credit> accountList = creditService.findAll();

    return Mono.just(ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(accountList))
        .defaultIfEmpty(ResponseEntity.noContent().build());
  }

  @GetMapping("/{credit_id}")
  public Mono<ResponseEntity<Credit>> findById(@Valid @PathVariable("credit_id") String id) {
    return creditService.findById(id)
        .map(c -> ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(c))
        .defaultIfEmpty(ResponseEntity.notFound().build());
  }

  @PostMapping("/preview")
  public Mono<ResponseEntity<Credit>> preview(@Valid @RequestBody CreditDTO creditDTO) {
    return creditService.preview(
            mapper.creditDTOToCredit(creditDTO)).map(c -> ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_JSON).body(c))
        .defaultIfEmpty(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
  }

  @PostMapping
  public Mono<ResponseEntity<CreditTransactions>> save(@Valid @RequestBody CreditDTO creditDTO,
      final ServerHttpRequest req) {
    return creditService.save(
            mapper.creditDTOToCredit(creditDTO)).map(c -> ResponseEntity.created(
                URI.create(req.getURI().toString().concat("/").concat(c.getId())))
            .contentType(MediaType.APPLICATION_JSON).body(c))
        .defaultIfEmpty(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
  }

  @PostMapping("/{credit_id}/installments/{installment_number}/pay")
  public Mono<ResponseEntity<CreditTransactions>> payInstallment(@PathVariable("credit_id") String creditId,
      @PathVariable("installment_number") String installmentNumber) {
    return creditService.payInstallment(creditId, installmentNumber)
        .map(c -> ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(c))
        .defaultIfEmpty(ResponseEntity.notFound().build());
  }

}
