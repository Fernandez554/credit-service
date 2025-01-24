package com.nttbank.microservices.creditservice.service.impl;

import com.nttbank.microservices.creditservice.model.entity.Credit;
import com.nttbank.microservices.creditservice.model.entity.CreditStatus;
import com.nttbank.microservices.creditservice.model.entity.CreditTransactions;
import com.nttbank.microservices.creditservice.model.entity.Installment;
import com.nttbank.microservices.creditservice.model.entity.TransactionType;
import com.nttbank.microservices.creditservice.model.response.CustomerResponse;
import com.nttbank.microservices.creditservice.repo.ICreditRepo;
import com.nttbank.microservices.creditservice.repo.ICreditTransactionRepo;
import com.nttbank.microservices.creditservice.service.CreditService;
import com.nttbank.microservices.creditservice.service.CustomerService;
import com.nttbank.microservices.creditservice.service.MovementService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Credit service implementation.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CreditServiceImpl implements CreditService {

  private final CustomerService customerService;
  private final MovementService movementService;
  private final ICreditTransactionRepo transactionRepo;
  private final ICreditRepo repo;

  @Override
  public Flux<Credit> findAll() {
    return repo.findAll();
  }

  @Override
  public Mono<CreditTransactions> save(Credit credit) {
    return customerService.findCustomerById(credit.getCustomerId())
        .flatMap(this::hasActiveCredits)
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
                  .build()))
              .flatMap(repo::save)
              .flatMap(
                  cc -> saveTransaction(cc, cc.getCreditAmount(),
                      TransactionType.credit_approval, null));
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
            .build()));
  }

  @Override
  public Mono<CreditTransactions> payInstallment(String creditId, String installmentNumber) {
    return repo.findById(creditId)
        .flatMap(credit -> {
          return credit.getLstInstallments().stream()
              .filter(installment ->
                  installment.getInstallmentNumber() == Integer.parseInt(installmentNumber)
                      && !installment.isPaid())
              .findFirst()
              .map(installment -> {
                if (credit.getLstInstallments()
                    .stream()
                    .filter(i -> !i.isPaid())
                    .count() == 1) {
                  credit.setStatus(CreditStatus.paid);
                }
                installment.setPaid(true);
                return repo.save(credit).flatMap(c -> {
                  return saveTransaction(c,
                      installment.getAmount(),
                      TransactionType.installment_pay,
                      String.valueOf(installment.getInstallmentNumber()));
                });
              })
              .orElse(Mono.error(
                  new IllegalArgumentException("Installment not found or already paid")));
        })
        .switchIfEmpty(Mono.error(new IllegalArgumentException(
            "Credit not found")));
  }

  @Override
  public Mono<Credit> findById(String creditId) {
    return repo.findById(creditId).flatMap(credit -> {
      return findCreditTransactions(creditId)
          .collectList()
          .map(lstTransactions -> {
            credit.setLstTransactions(lstTransactions);
            return credit;
          });
    });
  }

  private Mono<CustomerResponse> hasActiveCredits(CustomerResponse customer) {
    if ("business".equals(customer.getType())) {
      return Mono.just(customer);
    }
    return repo.countByCustomerIdAndStatus(customer.getId(), CreditStatus.active.name())
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

  private Mono<CreditTransactions> saveTransaction(Credit credit, BigDecimal amount,
      TransactionType type, String installment) {

    return transactionRepo.save(CreditTransactions.builder()
        .customerId(credit.getCustomerId())
        .creditId(credit.getId())
        .creditAmount(amount)
        .installment(installment)
        .type(type)
        .createdAt(LocalDateTime.now())
        .build()
    );
  }

  private Flux<CreditTransactions> findCreditTransactions(String creditId) {
    return transactionRepo.findAllByCreditId(creditId)
        .map(transaction ->
            CreditTransactions.builder()
                .creditAmount(transaction.getCreditAmount())
                .installment(transaction.getInstallment())
                .type(transaction.getType())
                .createdAt(transaction.getCreatedAt())
                .description(transaction.getDescription())
                .build()
        );
  }


}
