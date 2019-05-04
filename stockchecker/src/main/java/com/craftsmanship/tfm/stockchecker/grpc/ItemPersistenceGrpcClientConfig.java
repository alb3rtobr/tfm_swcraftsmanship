package com.craftsmanship.tfm.stockchecker.grpc;

import com.craftsmanship.tfm.stockchecker.grpc.ItemsPersistence;
import com.craftsmanship.tfm.stockchecker.grpc.ItemsPersistenceGrpc;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!dev")
public class ItemPersistenceGrpcClientConfig {

  @Value(value = "${dal.host}")
  private String serverHost;

  @Value(value = "${dal.port}")
  private int serverPort;

  @Bean
  public ItemsPersistence persistenceHandler() {
    return new ItemsPersistenceGrpc(serverHost, serverPort);
  }
}
