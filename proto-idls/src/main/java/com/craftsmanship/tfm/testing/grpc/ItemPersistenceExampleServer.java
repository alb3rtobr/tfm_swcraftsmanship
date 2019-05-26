package com.craftsmanship.tfm.testing.grpc;

import java.io.IOException;

import com.craftsmanship.tfm.grpc.services.ItemPersistenceService;
import com.craftsmanship.tfm.testing.persistence.ItemPersistenceStub;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.grpc.Server;
import io.grpc.ServerBuilder;

public class ItemPersistenceExampleServer {
  private static final Logger logger = LoggerFactory.getLogger(ItemPersistenceExampleServer.class);

  private int port;
  private Server server;
  private ItemPersistenceStub itemPersistenceStub;

  public ItemPersistenceExampleServer(int port) {
    this.port = port;
  }

  public void start() throws IOException {
    // create the items persistence stub
    itemPersistenceStub = new ItemPersistenceStub();

    server = ServerBuilder.forPort(this.port).addService(new ItemPersistenceService(itemPersistenceStub)).build().start();
    logger.info("Server started, listening on " + port);
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        // Use stderr here since the logger may have been reset by its JVM shutdown
        // hook.
        System.err.println("*** shutting down gRPC server since JVM is shutting down");
        ItemPersistenceExampleServer.this.stop();
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
    itemPersistenceStub.initialize();
  }

  /**
   * Main launches the server from the command line.
   */
  public static void main(String[] args) throws IOException, InterruptedException {
    final ItemPersistenceExampleServer server = new ItemPersistenceExampleServer(50051);
    server.start();
    server.blockUntilShutdown();
  }
}