package com.nttbank.microservices.creditservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;

/**
 * Configuration class for MongoDB.
 * This configuration is responsible for customizing the MongoDB converter by removing
 * the default MongoDB type mapper, which adds a `_class` field to each document.
 */
@Configuration
@RequiredArgsConstructor
public class MongoConfig implements InitializingBean {

  @Lazy
  private final MappingMongoConverter converter;

  @Override
  public void afterPropertiesSet() throws Exception {
    converter.setTypeMapper(new DefaultMongoTypeMapper(null));
  }
}
