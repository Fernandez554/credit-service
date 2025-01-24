package com.nttbank.microservices.creditservice.model.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "credit_transactions")
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreditTransactions {
  @EqualsAndHashCode.Include
  @Id
  private String id;

  private String customerId;

  private String creditId;

  @NotNull(message = "Transaction type cannot be null.")
  private TransactionType type;

  @DecimalMin(value = "0.00", message = "Amount must be greater than 0.")
  @NotNull(message = "Credit Amount cannot be null.")
  private BigDecimal creditAmount;

  private String installment;

  @NotNull(message = "Timestamp cannot be null.")
  private LocalDateTime createdAt;

  @Size(max = 255, message = "Description cannot exceed 255 characters.")
  private String description;
}
