package com.craftsmanship.tfm.stockchecker.grpc;

import com.craftsmanship.tfm.stockchecker.grpc.ItemPersistenceGrpcClient;

public class ItemsPersistenceGrpc implements ItemsPersistence {

	private ItemPersistenceGrpcClient grpcClient;

    public ItemsPersistenceGrpc(String serverHost, int serverPort) {
        grpcClient = new ItemPersistenceGrpcClient(serverHost, serverPort);
    }

    @Override
    public int count() {
        return grpcClient.count();
    }

}
