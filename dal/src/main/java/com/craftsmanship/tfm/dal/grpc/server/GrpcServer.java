package com.craftsmanship.tfm.dal.grpc.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;

public class GrpcServer {

    private static final Logger logger = LoggerFactory.getLogger(GrpcServer.class);

    private int port;
    private Server server;
    private List<BindableService> services;

    public GrpcServer(List<BindableService> services) {
        this.services = new ArrayList<BindableService>(services);
    }

    public GrpcServer(BindableService service) {
        this.services = new ArrayList<BindableService>();
        this.services.add(service);
    }

    public void start() throws IOException {
        
        ServerBuilder severBuilder = ServerBuilder.forPort(this.port);
        
        for (BindableService service : services) {
            severBuilder.addService(service);
        }

        server = severBuilder.build().start();

        logger.info("Server started, listening on " + port);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown
                // hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                GrpcServer.this.stop();
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

    public void initialize() {
    }

    public void setPort(int port) {
        this.port = port;

    }
}