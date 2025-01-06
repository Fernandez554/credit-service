package com.nttbank.microservices.creditservice.model.response;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Represents a response object containing movement details.
 * This class is used to hold and serialize movement information.
 */
@Data
@Getter
@Setter
@Builder
@ToString
@JsonSerialize
@JsonDeserialize
@AllArgsConstructor
@NoArgsConstructor
public class MovementResponse {
  private String id;
  private String customerId;
  private String accountId;
  private String type;
  private BigDecimal amount;
  private BigDecimal balanceAfterMovement;
  private LocalDateTime timestamp;
  private String description;
}
