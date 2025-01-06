package com.nttbank.microservices.creditservice.service.impl;

import com.nttbank.microservices.creditservice.model.entity.Credit;
import com.nttbank.microservices.creditservice.model.entity.Installment;
import com.nttbank.microservices.creditservice.model.response.CustomerResponse;
import com.nttbank.microservices.creditservice.repo.ICreditRepo;
import com.nttbank.microservices.creditservice.service.CreditService;
import com.nttbank.microservices.creditservice.service.CustomerService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Credit service implementation.
 */
@Service
@RequiredArgsConstructor
public class CreditServiceImpl implements CreditService {

  private final CustomerService customerService;
  private final ICreditRepo repo;

  @Override
  public Mono<Credit> save(Credit credit) {
    return customerService.findCustomerById(credit.getCustomerId())
        .flatMap(this::hasLeastOneCredit)
        .flatMap(customer -> {
          return calculateInstallments(credit)
              .flatMap(installments -> Mono.just(Credit.builder()
                  .customerId(credit.getCustomerId())
                  .creditAmount(credit.getCreditAmount())
                  .interestRate(credit.getInterestRate())
                  .installments(credit.getInstallments())
                  .lstInstallments(installments)
                  .startDate(installments.get(0).getDueDate())
                  .endDate(installments.get(installments.size() - 1).getDueDate())
                  .createdAt(LocalDateTime.now())
                  .updatedAt(LocalDateTime.now())
                  .creditStatus("active")
                  .build()))
              .flatMap(repo::save);
        }).onErrorResume(
            e -> Mono.error(
                new IllegalArgumentException("Error saving credit: " + e.getMessage())));
  }

  @Override
  public Mono<Credit> preview(Credit credit) {
    return calculateInstallments(credit)
        .flatMap(installments -> Mono.just(Credit.builder()
            .id(credit.getId())
            .customerId(credit.getCustomerId())
            .creditAmount(credit.getCreditAmount())
            .interestRate(credit.getInterestRate())
            .installments(credit.getInstallments())
            .lstInstallments(installments)
            .startDate(installments.get(0).getDueDate())
            .endDate(installments.get(installments.size() - 1).getDueDate())
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build()));
  }

  private Mono<CustomerResponse> hasLeastOneCredit(CustomerResponse customer) {
    if ("business".equals(customer.getType())) {
      return Mono.just(customer);
    }
    return repo.countByCustomerIdAndCreditStatus(customer.getId(), "active")
        .flatMap(totalCredits -> {
          if (totalCredits > 0) {
            return Mono.error(
                new IllegalArgumentException("Customer cannot have more than one active credit"));
          }
          return Mono.just(customer);
        });
  }

  private Mono<List<Installment>> calculateInstallments(Credit credit) {
    return Mono.fromCallable(() -> {
      BigDecimal monthlyInterestRate = credit.getInterestRate()
          .divide(BigDecimal.valueOf(100), 10,
              RoundingMode.HALF_UP)
          .divide(BigDecimal.valueOf(12), 10,
              RoundingMode.HALF_UP);

      BigDecimal monthlyInstallment = credit.getCreditAmount()
          .multiply(monthlyInterestRate.add(BigDecimal.ONE)
              .pow(credit.getInstallments()))
          .multiply(monthlyInterestRate)
          .divide(monthlyInterestRate.add(BigDecimal.ONE)
              .pow(credit.getInstallments()).subtract(BigDecimal.ONE), RoundingMode.HALF_UP);

      return IntStream.rangeClosed(1, credit.getInstallments())
          .mapToObj(i -> createInstallment(i, monthlyInstallment,
              LocalDate.now().plusMonths(i)))
          .toList();
    });
  }

  private Installment createInstallment(int installmentNumber, BigDecimal amount,
      LocalDate dueDate) {
    return Installment.builder()
        .installmentNumber(installmentNumber)
        .amount(amount.setScale(2, RoundingMode.HALF_UP))
        .dueDate(dueDate)
        .paid(false)
        .build();
  }

}
