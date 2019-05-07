package com.craftsmanship.tfm.dal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import com.craftsmanship.tfm.dal.grpc.server.GrpcServer;

@SpringBootApplication
@ComponentScan("com.craftsmanship.tfm.dal")
public class GrpcServerApplication implements CommandLineRunner{

	@Autowired 
	private GrpcServer grpcServer;
	
	@Value(value = "${grpc-server.port}")
	private int grpcServerPort;
	
	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(GrpcServerApplication.class);
		for (String arg:args) {
			System.out.println(arg + ":" + arg);
		}
        app.run(args);
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println("grpcServerPort: " + grpcServerPort);
		grpcServer.setPort(grpcServerPort);
		grpcServer.start();
		
		//TODO: check if needed next line ??
//		grpcServer.blockUntilShutdown();
	}
}
