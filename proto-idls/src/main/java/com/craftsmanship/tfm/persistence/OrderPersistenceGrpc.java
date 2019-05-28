package com.craftsmanship.tfm.persistence;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import com.craftsmanship.tfm.models.DomainOrder;
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
    public DomainOrder create(Order order) throws ItemDoesNotExist {
        return grpcClient.create((DomainOrder) order);
    }

    @Override
    public List<Order> list() {
        return new ArrayList<Order>(grpcClient.list());
    }

    @Override
    public DomainOrder get(Long id) throws OrderDoesNotExist {
        return grpcClient.get(id);
    }

    @Override
    public DomainOrder update(Long id, Order order) throws OrderDoesNotExist, ItemDoesNotExist {
        return grpcClient.update(id, (DomainOrder) order);
    }

    @Override
    public DomainOrder delete(Long id) throws OrderDoesNotExist {
        return grpcClient.delete(id);
    }

}