package com.craftsmanship.tfm.persistence;

import java.util.List;

import com.craftsmanship.tfm.models.Item;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.craftsmanship.tfm.exceptions.CustomException;
import com.craftsmanship.tfm.exceptions.ItemAlreadyExists;
import com.craftsmanship.tfm.exceptions.ItemDoesNotExist;
import com.craftsmanship.tfm.grpc.ItemPersistenceGrpcClient;

import io.grpc.ManagedChannel;

public class ItemPersistenceGrpc implements ItemPersistence {

    private static final Logger LOGGER = LoggerFactory.getLogger(ItemPersistenceGrpc.class);

    private ItemPersistenceGrpcClient grpcClient;

    public ItemPersistenceGrpc(String serverHost, int serverPort) {
        LOGGER.debug("Creating ItemPersistenceGrpcClient...");
        grpcClient = new ItemPersistenceGrpcClient(serverHost, serverPort);
    }

    public ItemPersistenceGrpc(ManagedChannel channel) {
        grpcClient = new ItemPersistenceGrpcClient(channel);
    }

    public void close() throws InterruptedException {
        grpcClient.shutdown();
    }

    @Override
    public Item create(Item item) throws ItemAlreadyExists {
        return grpcClient.create(item);
    }

    @Override
    public List<Item> list() {
        return grpcClient.list();
    }

    @Override
    public Item get(Long id) throws ItemDoesNotExist {
        return grpcClient.get(id);
    }

    @Override
    public Item update(Long id, Item item) throws ItemDoesNotExist {
        return grpcClient.update(id, item);
    }

    @Override
    public Item delete(Long id) throws ItemDoesNotExist {
        return grpcClient.delete(id);
    }

}