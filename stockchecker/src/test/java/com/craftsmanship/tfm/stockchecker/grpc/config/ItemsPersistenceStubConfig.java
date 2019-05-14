package com.craftsmanship.tfm.stockchecker.grpc.config;

import com.craftsmanship.tfm.persistence.ItemPersistence;
import com.craftsmanship.tfm.persistence.ItemPersistenceGrpc;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("dev")
public class ItemsPersistenceStubConfig {
    @Bean
    @Primary
    public ItemPersistence persistenceHandler(){
      return Mockito.mock(ItemPersistenceGrpc.class);
    }
}
