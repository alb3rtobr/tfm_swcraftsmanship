package com.craftsmanship.tfm.restapi.persistence;

import java.util.List;

import com.craftsmanship.tfm.models.Item;
import com.craftsmanship.tfm.restapi.grpc.ItemPersistenceGrpcClient;

public class ItemsPersistenceGrpc implements ItemsPersistence {

    private ItemPersistenceGrpcClient grpcClient;

    public ItemsPersistenceGrpc(String serverHost, int serverPort) {
        grpcClient = new ItemPersistenceGrpcClient(serverHost, serverPort);
    }

    @Override
    public Item create(Item item) {
        return grpcClient.createItem(item);
    }

    @Override
    public List<Item> list() {
        return grpcClient.list();
    }

    @Override
    public Item get(Long id) {
        return null;
    }

    @Override
    public Item update(Long id, Item item) {
        return null;
    }

    @Override
    public Item delete(Long id) {
        return null;
    }

}