package com.craftsmanship.tfm.restapi.persistence.config;

import com.craftsmanship.tfm.restapi.persistence.ItemsPersistence;
import com.craftsmanship.tfm.restapi.persistence.ItemsPersistenceStub;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("dev")
public class TestingPersistenceConfig {
    @Bean
    public ItemsPersistence persistenceHandler(){
      return new ItemsPersistenceStub();
    }
}