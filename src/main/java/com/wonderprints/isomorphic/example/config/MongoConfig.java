package com.wonderprints.isomorphic.example.config;

import com.mongodb.MongoClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class MongoConfig extends AbstractMongoConfiguration {
  @Override
  @Bean
  public MongoClient mongoClient() {
    return new MongoClient("127.0.0.1", 27017);
  }

  @Override
  @Bean
  public MongoTemplate mongoTemplate() {
    return new MongoTemplate(mongoClient(), "test");
  }

  @Override
  protected String getDatabaseName() {
    return "test";
  }
}
