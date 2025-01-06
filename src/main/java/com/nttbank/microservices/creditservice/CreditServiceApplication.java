package com.nttbank.microservices.creditservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactivefeign.spring.config.EnableReactiveFeignClients;

@EnableReactiveFeignClients
@SpringBootApplication
public class CreditServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(CreditServiceApplication.class, args);
  }

}
