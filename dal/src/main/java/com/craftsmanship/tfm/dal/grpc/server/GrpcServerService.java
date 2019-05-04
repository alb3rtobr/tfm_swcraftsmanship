package com.craftsmanship.tfm.dal.grpc.server;

import com.craftsmanship.tfm.dal.DataAccess;
import com.craftsmanship.tfm.dal.model.Item;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import com.craftsmanship.tfm.idls.v1.ItemPersistence.CreateItemRequest;
import com.craftsmanship.tfm.idls.v1.ItemPersistence.CreateItemResponse;
import com.craftsmanship.tfm.idls.v1.ItemPersistenceServiceGrpc.ItemPersistenceServiceImplBase;

@GrpcService
public class GrpcServerService extends ItemPersistenceServiceImplBase {
	
    @Override
    public void create(CreateItemRequest req, StreamObserver<CreateItemResponse> responseObserver) {
    	
    	// DAL will be called to create a new Item with:
    	// req.getItem().getId();
    	// req.getItem().getDescription();
    	Item item = new Item(req.getItem().getDescription());
    	
    	DataAccess dataAccess = new DataAccess();
    	
    	dataAccess.create(item);
    	
//    	CreateItemResponse response = CreateItemResponse.newBuilder().setItem(value)("Hello ==> " + req.getName()).build();
//        responseObserver.onNext(response);
    	responseObserver.onCompleted();
    }
}  