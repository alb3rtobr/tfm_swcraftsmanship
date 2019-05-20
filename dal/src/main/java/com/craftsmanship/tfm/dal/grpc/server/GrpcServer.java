package com.craftsmanship.tfm.dal.grpc.server;

import java.io.IOException;

import com.craftsmanship.tfm.dal.model.Item;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import com.craftsmanship.tfm.dal.DataAccess;
import com.craftsmanship.tfm.idls.v2.ItemPersistence.CreateItemRequest;
import com.craftsmanship.tfm.idls.v2.ItemPersistence.CreateItemResponse;
import com.craftsmanship.tfm.idls.v2.ItemPersistence.DeleteItemRequest;
import com.craftsmanship.tfm.idls.v2.ItemPersistence.DeleteItemResponse;
import com.craftsmanship.tfm.idls.v2.ItemPersistence.Empty;
import com.craftsmanship.tfm.idls.v2.ItemPersistence.GetItemRequest;
import com.craftsmanship.tfm.idls.v2.ItemPersistence.GetItemResponse;
import com.craftsmanship.tfm.idls.v2.ItemPersistence.GrpcItem;
import com.craftsmanship.tfm.idls.v2.ItemPersistence.ListItemResponse;
import com.craftsmanship.tfm.idls.v2.ItemPersistence.UpdateItemRequest;
import com.craftsmanship.tfm.idls.v2.ItemPersistence.UpdateItemResponse;
import com.craftsmanship.tfm.idls.v2.ItemPersistence.ListItemResponse.Builder;
import com.craftsmanship.tfm.idls.v2.ItemPersistenceServiceGrpc.ItemPersistenceServiceImplBase;

@Component
public class GrpcServer {
    private static final Logger logger = LoggerFactory.getLogger(GrpcServer.class);

    private int port;
    private Server server;

    @Autowired 
    private ItemPersistenceServiceImpl itemPersistenceServiceImpl;

    public void start() throws IOException {

        server = ServerBuilder.forPort(this.port).addService(itemPersistenceServiceImpl).build().start();
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

    @Component
    class ItemPersistenceServiceImpl extends ItemPersistenceServiceImplBase {

        @Autowired
        private DataAccess dataAccess;

        public ItemPersistenceServiceImpl(DataAccess dataAccess) {
            this.dataAccess = dataAccess;
        }

        @Override
        public void create(CreateItemRequest request, io.grpc.stub.StreamObserver<CreateItemResponse> responseObserver) {
            logger.info("CREATE RPC CALLED");
            GrpcItem grpcItem = request.getItem();
            Item item = getItemFromGrpcItem(grpcItem);
            Item createdItem = dataAccess.create(item);

            GrpcItem grpcItemResponse = getGrpcItemFromItem(createdItem);
            CreateItemResponse response = CreateItemResponse.newBuilder().setItem(grpcItemResponse).build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }

        @Override
        public void list(Empty request, io.grpc.stub.StreamObserver<ListItemResponse> responseObserver) {
            logger.info("LIST RPC CALLED");

            Builder responseBuilder = ListItemResponse.newBuilder();
            for (Item item : dataAccess.list()) {
                GrpcItem grpcItem = getGrpcItemFromItem(item);
                responseBuilder.addItem(grpcItem);
            }
            ListItemResponse response = responseBuilder.build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }

        @Override
        public void get(GetItemRequest request, io.grpc.stub.StreamObserver<GetItemResponse> responseObserver) {
            logger.info("GET RPC CALLED");

            Item item = dataAccess.get(request.getId());
            GrpcItem grpcItem = getGrpcItemFromItem(item);

            GetItemResponse response = GetItemResponse.newBuilder().setItem(grpcItem).build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }

        @Override
        public void update(UpdateItemRequest request, io.grpc.stub.StreamObserver<UpdateItemResponse> responseObserver) {
            logger.info("UPDATE RPC CALLED");

            // TODO: right now, if the id does not exists, the item is creted, Should we
            // raise an error?
            Item item = getItemFromGrpcItem(request.getItem());
            Item createdItem = dataAccess.update(request.getId(), item);

            GrpcItem grpcItemResponse = getGrpcItemFromItem(createdItem);

            UpdateItemResponse response = UpdateItemResponse.newBuilder().setItem(grpcItemResponse).build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }

        @Override
        public void delete(DeleteItemRequest request, io.grpc.stub.StreamObserver<DeleteItemResponse> responseObserver) {
            logger.info("DELETE RPC CALLED");

            Item deletedItem = dataAccess.delete(request.getId());
            GrpcItem grpcItemResponse = getGrpcItemFromItem(deletedItem);

            DeleteItemResponse response = DeleteItemResponse.newBuilder().setItem(grpcItemResponse).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }

        private GrpcItem getGrpcItemFromItem(Item item) {
            return GrpcItem.newBuilder()
                    .setId(item.getId())
                    .setName(item.getName())
                    .setPrice(item.getPrice())
                    .setQuantity(item.getQuantity()).build();
        }
        private Item getItemFromGrpcItem(GrpcItem grpcItem) {
            return new Item(grpcItem.getId(), grpcItem.getName(), grpcItem.getPrice(), grpcItem.getQuantity());
        }
    }

    public void setPort(int port) {
        this.port = port;

    }
}