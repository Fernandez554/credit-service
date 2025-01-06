package com.nttbank.microservices.creditservice.model.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * Class that represents an installment of a credit.
 */
@Builder
@Data
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Installment {

  private Installment() {
  }

  private int installmentNumber;
  private BigDecimal amount;
  private LocalDate dueDate;
  private boolean paid;
  private LocalDate paymentDate;
}
