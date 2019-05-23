package com.craftsmanship.tfm.testing.grpc;

import java.io.IOException;

import com.craftsmanship.tfm.testing.persistence.OrderPersistenceStub;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.grpc.Server;
import io.grpc.inprocess.InProcessServerBuilder;

public class OrderPersistenceInProcessServer {

    private static final Logger logger = LoggerFactory.getLogger(OrderPersistenceInProcessServer.class);

    private OrderPersistenceDummyService service;
    private Server server;

    public OrderPersistenceInProcessServer(OrderPersistenceStub orderPersistenceStub) {
        this.service = new OrderPersistenceDummyService(orderPersistenceStub);
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
                OrderPersistenceInProcessServer.this.stop();
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