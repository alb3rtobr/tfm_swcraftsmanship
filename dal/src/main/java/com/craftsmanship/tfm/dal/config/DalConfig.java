package com.craftsmanship.tfm.dal.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.craftsmanship.tfm.dal.ItemDAO;
import com.craftsmanship.tfm.dal.grpc.server.EntityConversion;
import com.craftsmanship.tfm.dal.grpc.server.GrpcServer;
import com.craftsmanship.tfm.grpc.services.ItemPersistenceService;

@Configuration
public class DalConfig {

    @Autowired
    ItemDAO itemDAO;

    @Bean
    public GrpcServer grpcServer() {

        EntityConversion entityConversion = new EntityConversion();
        ItemPersistenceService service = new ItemPersistenceService(itemDAO, entityConversion);
        GrpcServer grpcServer = new GrpcServer(service);
        return grpcServer;
    }

}
