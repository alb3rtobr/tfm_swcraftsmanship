package com.craftsmanship.tfm.restapi.persistence.config;

import com.craftsmanship.tfm.restapi.persistence.ItemsPersistence;
import com.craftsmanship.tfm.restapi.persistence.ItemsPersistenceGrpc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!dev")
public class PersistenceConfig {
    @Bean
    public ItemsPersistence persistenceHandler(){
      return new ItemsPersistenceGrpc();
    }
}