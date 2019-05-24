package com.craftsmanship.tfm.grpc.servers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.inprocess.InProcessServerBuilder;

public class PersistenceInProcessGrpcServer {

    private static final Logger logger = LoggerFactory.getLogger(PersistenceInProcessGrpcServer.class);

    private static final String GRPC_SERVER_NAME = "PersistenceGrpcServer";

    private List<BindableService> services;
    private Server server;

    public PersistenceInProcessGrpcServer(List<BindableService> services) {
        this.services = new ArrayList<BindableService>(services);
    }

    public PersistenceInProcessGrpcServer(BindableService service) {
        this.services = new ArrayList<BindableService>();
        this.services.add(service);
    }

    public void start() throws IOException, InstantiationException, IllegalAccessException {
        InProcessServerBuilder builder = InProcessServerBuilder.forName(GRPC_SERVER_NAME).directExecutor();

        for (BindableService service : services) {
            builder.addService(service);
        }

        this.server = builder.build().start();
        logger.info("InProcessServer started.");
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown
                // hook.
                logger.info("*** shutting down gRPC server since JVM is shutting down");
                PersistenceInProcessGrpcServer.this.stop();
                logger.info("*** server shut down");
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