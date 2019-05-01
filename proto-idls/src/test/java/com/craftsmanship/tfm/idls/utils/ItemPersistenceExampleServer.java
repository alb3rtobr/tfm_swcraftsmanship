package com.craftsmanship.tfm.idls.utils;

import java.io.IOException;

import com.craftsmanship.tfm.idls.stubs.ItemsPersistenceStub;
import com.craftsmanship.tfm.models.Item;
import com.craftsmanship.tfm.utils.ConversionUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import v1.ItemPersistence.CreateItemResponse;
import v1.ItemPersistence.DeleteItemResponse;
import v1.ItemPersistence.GetItemResponse;
import v1.ItemPersistence.GrpcItem;
import v1.ItemPersistence.ListItemResponse;
import v1.ItemPersistence.UpdateItemResponse;
import v1.ItemPersistence.ListItemResponse.Builder;
import v1.ItemPersistenceServiceGrpc.ItemPersistenceServiceImplBase;

public class ItemPersistenceExampleServer {
  private static final Logger logger = LoggerFactory.getLogger(ItemPersistenceExampleServer.class);

  private Server server;

  private void start() throws IOException {
    /* The port on which the server should run */
    int port = 50051;
    server = ServerBuilder.forPort(port).addService(new ItemPersistenceServiceImpl()).build().start();
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

  private void stop() {
    if (server != null) {
      server.shutdown();
    }
  }

  /**
   * Await termination on the main thread since the grpc library uses daemon
   * threads.
   */
  private void blockUntilShutdown() throws InterruptedException {
    if (server != null) {
      server.awaitTermination();
    }
  }

  /**
   * Main launches the server from the command line.
   */
  public static void main(String[] args) throws IOException, InterruptedException {
    final ItemPersistenceExampleServer server = new ItemPersistenceExampleServer();
    server.start();
    server.blockUntilShutdown();
  }

  static class ItemPersistenceServiceImpl extends ItemPersistenceServiceImplBase {

    private static ItemsPersistenceStub itemsPersistence = new ItemsPersistenceStub();

    @Override
    public void create(v1.ItemPersistence.CreateItemRequest request,
        io.grpc.stub.StreamObserver<v1.ItemPersistence.CreateItemResponse> responseObserver) {
      logger.info("CREATE RPC CALLED");
      GrpcItem grpcItem = request.getItem();
      Item item = new Item.Builder().withDescription(grpcItem.getDescription()).build();
      Item createdItem = itemsPersistence.create(item);

      GrpcItem grpcItemResponse = GrpcItem.newBuilder()
        .setId(createdItem.getId())
        .setDescription(createdItem.getDescription())
        .build();
      CreateItemResponse response = CreateItemResponse.newBuilder()
        .setItem(grpcItemResponse)
        .build();

      responseObserver.onNext(response);
      responseObserver.onCompleted();
    }

    @Override
    public void list(v1.ItemPersistence.ListItemRequest request,
        io.grpc.stub.StreamObserver<v1.ItemPersistence.ListItemResponse> responseObserver) {
      logger.info("LIST RPC CALLED");

      Builder responseBuilder = ListItemResponse.newBuilder();
      for (Item item: itemsPersistence.list()) {
        GrpcItem grpcItem = ConversionUtils.getGrpcItemFromItem(item);
        responseBuilder.addItem(grpcItem);
      }
      ListItemResponse response = responseBuilder.build();

      responseObserver.onNext(response);
      responseObserver.onCompleted();
    }

    @Override
    public void get(v1.ItemPersistence.GetItemRequest request,
        io.grpc.stub.StreamObserver<v1.ItemPersistence.GetItemResponse> responseObserver) {
      logger.info("GET RPC CALLED");

      Item item = itemsPersistence.get(request.getId());
      GrpcItem grpcItem = ConversionUtils.getGrpcItemFromItem(item);
      GetItemResponse response = GetItemResponse.newBuilder().setItem(grpcItem).build();

      responseObserver.onNext(response);
      responseObserver.onCompleted();
    }

    @Override
    public void update(v1.ItemPersistence.UpdateItemRequest request,
        io.grpc.stub.StreamObserver<v1.ItemPersistence.UpdateItemResponse> responseObserver) {
      logger.info("UPDATE RPC CALLED");

      //TODO: right now, if the id does not exists, the item is creted, Should we raise an error?
      Item item = ConversionUtils.getItemFromGrpcItem(request.getItem());
      Item createdItem = itemsPersistence.update(request.getId(), item);

      GrpcItem grpcItemResponse = ConversionUtils.getGrpcItemFromItem(createdItem);

      UpdateItemResponse response = UpdateItemResponse.newBuilder()
      .setItem(grpcItemResponse)
      .build();

      responseObserver.onNext(response);
      responseObserver.onCompleted();
    }

    @Override
    public void delete(v1.ItemPersistence.DeleteItemRequest request,
        io.grpc.stub.StreamObserver<v1.ItemPersistence.DeleteItemResponse> responseObserver) {
      logger.info("DELETE RPC CALLED");

      Item deletedItem = itemsPersistence.delete(request.getId());
      GrpcItem grpcItemResponse = ConversionUtils.getGrpcItemFromItem(deletedItem);

      DeleteItemResponse response = DeleteItemResponse.newBuilder().setItem(grpcItemResponse).build();
      responseObserver.onNext(response);
      responseObserver.onCompleted();
    }
  }
}