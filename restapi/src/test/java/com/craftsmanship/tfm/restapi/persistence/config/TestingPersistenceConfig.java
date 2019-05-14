package com.craftsmanship.tfm.restapi.persistence.config;

import com.craftsmanship.tfm.persistence.ItemPersistence;
import com.craftsmanship.tfm.testing.persistence.ItemPersistenceStub;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("dev")
public class TestingPersistenceConfig {
    @Bean
    public ItemPersistence persistenceHandler(){
      return new ItemPersistenceStub();
    }
}