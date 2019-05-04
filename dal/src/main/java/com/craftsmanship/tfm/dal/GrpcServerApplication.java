package com.craftsmanship.tfm.dal;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.craftsmanship.tfm.dal.grpc.server.GrpcServer;

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
		//TODO: check if needed next line ??
//		grpcServer.blockUntilShutdown();
	}
}
