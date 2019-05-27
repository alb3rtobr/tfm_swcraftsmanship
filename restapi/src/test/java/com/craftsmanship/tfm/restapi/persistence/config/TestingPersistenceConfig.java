package com.craftsmanship.tfm.restapi.persistence.config;

import com.craftsmanship.tfm.testing.persistence.ItemPersistenceStub;
import com.craftsmanship.tfm.testing.persistence.OrderPersistenceStub;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("dev")
public class TestingPersistenceConfig {

    @Autowired
    ItemPersistenceStub itemPersistence;

    @Bean
    public ItemPersistenceStub itemPersistenceHandler() {
      return new ItemPersistenceStub();
    }

    @Bean
    public OrderPersistenceStub orderPersistenceHandler() {
      return new OrderPersistenceStub(itemPersistence);
    }
}