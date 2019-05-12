package com.craftsmanship.tfm.stockchecker.grpc;

import com.craftsmanship.tfm.models.Item;
import com.craftsmanship.tfm.stockchecker.grpc.ItemPersistenceGrpcClient;

public class ItemsPersistenceGrpc implements ItemsPersistence {

	private ItemPersistenceGrpcClient grpcClient;

    public ItemsPersistenceGrpc(String serverHost, int serverPort) {
        grpcClient = new ItemPersistenceGrpcClient(serverHost, serverPort);
    }
    
    @Override
    //Used in tests
    public Item create(Item item) {
    	return grpcClient.create(item);
    }
    
    @Override
    public int count() {
        return grpcClient.count();
    }

}
