package com.craftsmanship.tfm.dal.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.craftsmanship.tfm.dal.DataAccess;
import com.craftsmanship.tfm.dal.grpc.server.GrpcServer;
import com.craftsmanship.tfm.dal.grpc.server.ItemPersistenceServiceImpl;

@Configuration
public class DalConfig {

    @Autowired
    DataAccess dataAccess;

    @Bean
    public GrpcServer grpcServer() {

        ItemPersistenceServiceImpl service = new ItemPersistenceServiceImpl(dataAccess);
        GrpcServer grpcServer = new GrpcServer(service);
        return grpcServer;
    }

}
