package com.craftsmanship.tfm.dal.grpc.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.craftsmanship.tfm.dal.ItemDAO;
import com.craftsmanship.tfm.dal.model.EntityItem;
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

public class ItemPersistenceServiceImpl extends ItemPersistenceServiceImplBase {
    private static final Logger logger = LoggerFactory.getLogger(GrpcServer.class);

    private ItemDAO itemDAO;

    public ItemPersistenceServiceImpl(ItemDAO itemDAO) {
        this.itemDAO = itemDAO;
    }

    @Override
    public void create(CreateItemRequest request, io.grpc.stub.StreamObserver<CreateItemResponse> responseObserver) {
        logger.info("CREATE RPC CALLED");
        GrpcItem grpcItem = request.getItem();
        EntityItem item = getItemFromGrpcItem(grpcItem);
        EntityItem createdItem = itemDAO.create(item);

        GrpcItem grpcItemResponse = getGrpcItemFromItem(createdItem);
        CreateItemResponse response = CreateItemResponse.newBuilder().setItem(grpcItemResponse).build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void list(Empty request, io.grpc.stub.StreamObserver<ListItemResponse> responseObserver) {
        logger.info("LIST RPC CALLED");

        Builder responseBuilder = ListItemResponse.newBuilder();
        for (EntityItem item : itemDAO.list()) {
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

        EntityItem item = itemDAO.get(request.getId());
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
        EntityItem item = getItemFromGrpcItem(request.getItem());
        EntityItem createdItem = itemDAO.update(request.getId(), item);

        GrpcItem grpcItemResponse = getGrpcItemFromItem(createdItem);

        UpdateItemResponse response = UpdateItemResponse.newBuilder().setItem(grpcItemResponse).build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void delete(DeleteItemRequest request, io.grpc.stub.StreamObserver<DeleteItemResponse> responseObserver) {
        logger.info("DELETE RPC CALLED");

        EntityItem deletedItem = itemDAO.delete(request.getId());
        GrpcItem grpcItemResponse = getGrpcItemFromItem(deletedItem);

        DeleteItemResponse response = DeleteItemResponse.newBuilder().setItem(grpcItemResponse).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    private GrpcItem getGrpcItemFromItem(EntityItem item) {
        return GrpcItem.newBuilder()
                .setId(item.getId())
                .setName(item.getName())
                .setPrice(item.getPrice())
                .setStock(item.getStock()).build();
    }
    private EntityItem getItemFromGrpcItem(GrpcItem grpcItem) {
        return new EntityItem(grpcItem.getId(), grpcItem.getName(), grpcItem.getPrice(), grpcItem.getStock());
    }
}
