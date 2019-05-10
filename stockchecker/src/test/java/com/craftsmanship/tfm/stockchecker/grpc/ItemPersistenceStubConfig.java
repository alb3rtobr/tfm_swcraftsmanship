package com.craftsmanship.tfm.stockchecker.grpc;

import com.craftsmanship.tfm.stockchecker.grpc.ItemsPersistence;
import com.craftsmanship.tfm.stockchecker.grpc.ItemPersistenceStub;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("dev")
public class ItemPersistenceStubConfig {
    @Bean
    @Primary
    public ItemsPersistence persistenceHandler(){
      return Mockito.mock(ItemPersistenceStub.class);
    }
}
