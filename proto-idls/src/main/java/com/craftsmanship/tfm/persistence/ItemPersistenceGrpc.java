package com.craftsmanship.tfm.persistence;

import java.util.List;

import com.craftsmanship.tfm.models.Item;
import com.craftsmanship.tfm.grpc.ItemPersistenceGrpcClient;

import io.grpc.ManagedChannel;

public class ItemPersistenceGrpc implements ItemPersistence {

    private ItemPersistenceGrpcClient grpcClient;

    public ItemPersistenceGrpc(String serverHost, int serverPort) {
        grpcClient = new ItemPersistenceGrpcClient(serverHost, serverPort);
    }

    public ItemPersistenceGrpc(ManagedChannel channel) {
        grpcClient = new ItemPersistenceGrpcClient(channel);
    }

    public void close() throws InterruptedException {
        grpcClient.shutdown();
    }

    @Override
    public Item create(Item item) {
        return grpcClient.create(item);
    }

    @Override
    public List<Item> list() {
        return grpcClient.list();
    }

    @Override
    public Item get(Long id) {
        return grpcClient.get(id);
    }

    @Override
    public Item update(Long id, Item item) {
        return grpcClient.update(id, item);
    }

    @Override
    public Item delete(Long id) {
        return null;
    }

    @Override
    public int count() {
        return grpcClient.count();
    }

}