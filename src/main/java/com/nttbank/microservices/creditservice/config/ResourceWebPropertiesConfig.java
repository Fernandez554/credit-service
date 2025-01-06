package com.nttbank.microservices.creditservice.config;

import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class to customize and define web-related properties for the application.
 * This configuration registers a {@link WebProperties.Resources} bean, which can be used
 * to configure web resources such as static content.
 *
 * <p>By defining this bean, you can customize various settings related to web resource handling
 * within your Spring Boot application. The default implementation can be customized further
 * if needed.</p>
 */
@Configuration
public class ResourceWebPropertiesConfig {

  @Bean
  public WebProperties.Resources resources() {
    return new WebProperties.Resources();
  }
}
