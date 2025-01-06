package com.nttbank.microservices.creditservice.model.response;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Represents a response object containing customer details.
 * This class is used to hold and serialize customer information
 * in the response sent to the client.
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
public class CustomerResponse {

  private String id;
  private String type;
  private String profile;
  private String name;
  private String phone;
  private String email;
  private String address;
  private String dateOfBirth;

}