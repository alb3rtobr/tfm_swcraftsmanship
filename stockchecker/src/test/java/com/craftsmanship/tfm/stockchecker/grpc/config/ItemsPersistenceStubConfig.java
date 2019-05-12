package com.craftsmanship.tfm.stockchecker.grpc.config;

import com.craftsmanship.tfm.stockchecker.grpc.ItemsPersistence;
import com.craftsmanship.tfm.stockchecker.grpc.ItemsPersistenceStub;

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
    public ItemsPersistence persistenceHandler(){
      return Mockito.mock(ItemsPersistenceStub.class);
    }
}
