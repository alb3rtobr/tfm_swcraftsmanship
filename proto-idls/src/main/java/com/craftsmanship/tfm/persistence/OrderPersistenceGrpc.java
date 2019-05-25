package com.craftsmanship.tfm.persistence;

import java.util.List;

import com.craftsmanship.tfm.models.Order;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.craftsmanship.tfm.exceptions.ItemDoesNotExist;
import com.craftsmanship.tfm.exceptions.OrderDoesNotExist;
import com.craftsmanship.tfm.grpc.OrderPersistenceGrpcClient;

import io.grpc.ManagedChannel;

public class OrderPersistenceGrpc implements OrderPersistence {

    private static final Logger LOGGER = LoggerFactory.getLogger(ItemPersistenceGrpc.class);

    private OrderPersistenceGrpcClient grpcClient;

    public OrderPersistenceGrpc(String serverHost, int serverPort) {
        LOGGER.debug("Creating OrderPersistenceGrpcClient...");
        grpcClient = new OrderPersistenceGrpcClient(serverHost, serverPort);
    }

    public OrderPersistenceGrpc(ManagedChannel channel) {
        grpcClient = new OrderPersistenceGrpcClient(channel);
    }

    public void close() throws InterruptedException {
        grpcClient.shutdown();
    }

    @Override
    public Order create(Order order) throws ItemDoesNotExist {
        return grpcClient.create(order);
    }

    @Override
    public List<Order> list() {
        return grpcClient.list();
    }

    @Override
    public Order get(Long id) throws OrderDoesNotExist {
        return grpcClient.get(id);
    }

    @Override
    public Order update(Long id, Order order) throws OrderDoesNotExist, ItemDoesNotExist {
        return grpcClient.update(id, order);
    }

    @Override
    public Order delete(Long id) throws OrderDoesNotExist {
        return grpcClient.delete(id);
    }

}