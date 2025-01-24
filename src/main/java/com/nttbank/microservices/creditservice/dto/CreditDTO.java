package com.nttbank.microservices.creditservice.dto;

import com.nttbank.microservices.creditservice.model.entity.CreditStatus;
import com.nttbank.microservices.creditservice.model.entity.Installment;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Represents the Credit Data Transfer Object (DTO). */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreditDTO {

  private String id;
  @NotNull
  private String customerId;
  @NotNull
  private BigDecimal creditAmount;
  @NotNull
  private BigDecimal interestRate;
  @NotNull
  @Min(value = 1, message = "Installments must be greater or equal than 1")
  @Max(value = 36, message = "Installments must be less than or equal than 36")
  private Integer installments;
  private List<Installment> lstInstallments;
  private LocalDate startDate;
  private LocalDate endDate;
  @Builder.Default
  private LocalDateTime createdAt = LocalDateTime.now();
  @Builder.Default
  private LocalDateTime updatedAt = LocalDateTime.now();
  @Builder.Default
  private CreditStatus status = CreditStatus.active;
}
