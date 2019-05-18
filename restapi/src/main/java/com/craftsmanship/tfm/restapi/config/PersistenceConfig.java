package com.craftsmanship.tfm.restapi.config;

import com.craftsmanship.tfm.persistence.ItemPersistenceGrpc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@ConfigurationProperties(prefix = "dal")
@Profile("!dev")
public class PersistenceConfig {

  private static final Logger LOGGER = LoggerFactory.getLogger(PersistenceConfig.class);

  @Value(value = "${dal.host}")
  private String serverHost;

  @Value(value = "${dal.port}")
  private int serverPort;

  @Bean
  public ItemPersistenceGrpc persistenceHandler() {
    return new ItemPersistenceGrpc(serverHost, serverPort);
  }
}