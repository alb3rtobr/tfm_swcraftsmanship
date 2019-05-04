package com.craftsmanship.tfm.dal.grpc.server;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GrpcServerApplication implements CommandLineRunner{

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(GrpcServerApplication.class);
        app.run(args);
	}

	@Override
	public void run(String... args) throws Exception {
		final GrpcServer grpcServer = new GrpcServer(50051);
		grpcServer.start();
		grpcServer.blockUntilShutdown();
	}
}
