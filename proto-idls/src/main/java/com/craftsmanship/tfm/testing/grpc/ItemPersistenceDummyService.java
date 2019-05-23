package com.craftsmanship.tfm.testing.grpc;

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
import com.craftsmanship.tfm.testing.persistence.ItemPersistenceStub;
import com.craftsmanship.tfm.utils.ConversionUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.grpc.Status;

public class ItemPersistenceDummyService extends ItemPersistenceServiceImplBase {
    private static final Logger LOGGER = LoggerFactory.getLogger(ItemPersistenceDummyService.class);

    private ItemPersistenceStub itemPersistence;

    public ItemPersistenceDummyService(ItemPersistenceStub itemsPersistenceStub) {
        this.itemPersistence = itemsPersistenceStub;
    }

    @Override
    public void create(CreateItemRequest request, io.grpc.stub.StreamObserver<CreateItemResponse> responseObserver) {
        LOGGER.info("CREATE RPC CALLED");
        GrpcItem grpcItem = request.getItem();
        Item item = ConversionUtils.getItemFromGrpcItem(grpcItem);
        Item createdItem = itemPersistence.create(item);

        GrpcItem grpcItemResponse = ConversionUtils.getGrpcItemFromItem(createdItem);
        CreateItemResponse response = CreateItemResponse.newBuilder().setItem(grpcItemResponse).build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
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

        Item item = itemPersistence.get(request.getId());

        if (item != null) {
            GrpcItem grpcItem = ConversionUtils.getGrpcItemFromItem(item);
            GetItemResponse response = GetItemResponse.newBuilder().setItem(grpcItem).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription("Item with id " + request.getId() + " does not exist").asRuntimeException());
        }
    }

    @Override
    public void update(UpdateItemRequest request, io.grpc.stub.StreamObserver<UpdateItemResponse> responseObserver) {
        LOGGER.info("UPDATE RPC CALLED");

        if (itemPersistence.get(request.getId()) != null) {
            Item item = ConversionUtils.getItemFromGrpcItem(request.getItem());
            Item createdItem = itemPersistence.update(request.getId(), item);

            GrpcItem grpcItemResponse = ConversionUtils.getGrpcItemFromItem(createdItem);

            UpdateItemResponse response = UpdateItemResponse.newBuilder().setItem(grpcItemResponse).build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } else {
            // There is not Item with that id, so exception
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription("Item with id " + request.getId() + " does not exist").asRuntimeException());
        }
    }

    @Override
    public void delete(DeleteItemRequest request, io.grpc.stub.StreamObserver<DeleteItemResponse> responseObserver) {
        LOGGER.info("DELETE RPC CALLED");

        Item deletedItem = itemPersistence.delete(request.getId());

        if (deletedItem != null) {
            GrpcItem grpcItemResponse = ConversionUtils.getGrpcItemFromItem(deletedItem);
            DeleteItemResponse response = DeleteItemResponse.newBuilder().setItem(grpcItemResponse).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription("Item with id " + request.getId() + " does not exist").asRuntimeException());
        }
    }
}
