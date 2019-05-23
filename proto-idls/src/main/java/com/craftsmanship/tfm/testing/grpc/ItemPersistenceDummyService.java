package com.craftsmanship.tfm.testing.grpc;

import com.craftsmanship.tfm.exceptions.ItemAlreadyExists;
import com.craftsmanship.tfm.exceptions.ItemDoesNotExist;
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
import com.craftsmanship.tfm.models.Item;
import com.craftsmanship.tfm.persistence.ItemPersistence;
import com.craftsmanship.tfm.utils.ConversionUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.grpc.Status;

public class ItemPersistenceDummyService extends ItemPersistenceServiceImplBase {
    private static final Logger LOGGER = LoggerFactory.getLogger(ItemPersistenceDummyService.class);

    private ItemPersistence itemPersistence;

    public ItemPersistenceDummyService(ItemPersistence itemsPersistence) {
        this.itemPersistence = itemsPersistence;
    }

    @Override
    public void create(CreateItemRequest request, io.grpc.stub.StreamObserver<CreateItemResponse> responseObserver) {
        LOGGER.info("CREATE RPC CALLED");
        GrpcItem grpcItem = request.getItem();
        Item item = ConversionUtils.getItemFromGrpcItem(grpcItem);

        try {
            Item createdItem = itemPersistence.create(item);

            GrpcItem grpcItemResponse = ConversionUtils.getGrpcItemFromItem(createdItem);
            CreateItemResponse response = CreateItemResponse.newBuilder().setItem(grpcItemResponse).build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (ItemAlreadyExists e) {
            responseObserver.onError(Status.ALREADY_EXISTS
                    .withDescription("Item with id " + item.getId() + " or name " + item.getName() + " already exists")
                    .asRuntimeException());
        }
    }

    @Override
    public void list(Empty request, io.grpc.stub.StreamObserver<ListItemResponse> responseObserver) {
        LOGGER.info("LIST RPC CALLED");

        Builder responseBuilder = ListItemResponse.newBuilder();
        for (Item item : itemPersistence.list()) {
            GrpcItem grpcItem = ConversionUtils.getGrpcItemFromItem(item);
            responseBuilder.addItem(grpcItem);
        }
        ListItemResponse response = responseBuilder.build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void get(GetItemRequest request, io.grpc.stub.StreamObserver<GetItemResponse> responseObserver) {
        LOGGER.info("GET RPC CALLED");

        try {
            Item item = itemPersistence.get(request.getId());
            GrpcItem grpcItem = ConversionUtils.getGrpcItemFromItem(item);
            GetItemResponse response = GetItemResponse.newBuilder().setItem(grpcItem).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (ItemDoesNotExist e) {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription("Item with id " + request.getId() + " does not exist").asRuntimeException());
        }
    }

    @Override
    public void update(UpdateItemRequest request, io.grpc.stub.StreamObserver<UpdateItemResponse> responseObserver) {
        LOGGER.info("UPDATE RPC CALLED");

        try {
            // first check if the item does not exist
            itemPersistence.get(request.getId());

            Item item = ConversionUtils.getItemFromGrpcItem(request.getItem());
            Item createdItem = itemPersistence.update(request.getId(), item);
    
            GrpcItem grpcItemResponse = ConversionUtils.getGrpcItemFromItem(createdItem);
    
            UpdateItemResponse response = UpdateItemResponse.newBuilder().setItem(grpcItemResponse).build();
    
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (ItemDoesNotExist e) {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription("Item with id " + request.getId() + " does not exist").asRuntimeException());
        }
    }

    @Override
    public void delete(DeleteItemRequest request, io.grpc.stub.StreamObserver<DeleteItemResponse> responseObserver) {
        LOGGER.info("DELETE RPC CALLED");

        try {
            Item deletedItem = itemPersistence.delete(request.getId());

            GrpcItem grpcItemResponse = ConversionUtils.getGrpcItemFromItem(deletedItem);
            DeleteItemResponse response = DeleteItemResponse.newBuilder().setItem(grpcItemResponse).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (ItemDoesNotExist e) {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription("Item with id " + request.getId() + " does not exist").asRuntimeException());
        }
    }
}
