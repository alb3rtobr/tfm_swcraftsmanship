package com.craftsmanship.tfm.testing.grpc;

import java.io.IOException;

import com.craftsmanship.tfm.grpc.services.ItemPersistenceService;
import com.craftsmanship.tfm.testing.persistence.ItemPersistenceStub;
import com.craftsmanship.tfm.utils.ConversionLogic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.grpc.Server;
import io.grpc.inprocess.InProcessServerBuilder;

public class ItemPersistenceInProcessServer {

    private static final Logger logger = LoggerFactory.getLogger(ItemPersistenceInProcessServer.class);

    private ItemPersistenceService service;
    private Server server;
    private ConversionLogic domainConversion;

    public ItemPersistenceInProcessServer(ItemPersistenceStub itemPersistenceStub) {
        this.domainConversion = new ConversionLogic();
        this.service = new ItemPersistenceService(itemPersistenceStub, domainConversion);
    }

    public void start() throws IOException, InstantiationException, IllegalAccessException {
        server = InProcessServerBuilder.forName("test").directExecutor().addService(service).build()
                .start();
        logger.info("InProcessServer started.");
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown
                // hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                ItemPersistenceInProcessServer.this.stop();
                System.err.println("*** server shut down");
            }
        });
    }

    public void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    /**
     * Await termination on the main thread since the grpc library uses daemon
     * threads.
     */
    public void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }
}