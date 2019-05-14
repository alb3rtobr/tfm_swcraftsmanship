package com.craftsmanship.tfm.stockchecker.grpc.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.craftsmanship.tfm.persistence.ItemPersistence;
import com.craftsmanship.tfm.persistence.ItemPersistenceGrpc;

@Configuration
@Profile("!dev")
public class ItemPersistenceGrpcConfig {

  @Value(value = "${dal.host}")
  private String serverHost;

  @Value(value = "${dal.port}")
  private int serverPort;

  @Bean
  public ItemPersistence persistenceHandler() {
    return new ItemPersistenceGrpc(serverHost, serverPort);
  }
}
