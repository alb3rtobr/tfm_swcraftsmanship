package com.craftsmanship.tfm.grpc;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.craftsmanship.tfm.exceptions.ItemDoesNotExist;
import com.craftsmanship.tfm.exceptions.OrderDoesNotExist;
import com.craftsmanship.tfm.idls.v2.OrderPersistenceServiceGrpc;
import com.craftsmanship.tfm.idls.v2.ItemPersistence.Empty;
import com.craftsmanship.tfm.idls.v2.OrderPersistence.CreateOrderRequest;
import com.craftsmanship.tfm.idls.v2.OrderPersistence.CreateOrderResponse;
import com.craftsmanship.tfm.idls.v2.OrderPersistence.DeleteOrderRequest;
import com.craftsmanship.tfm.idls.v2.OrderPersistence.DeleteOrderResponse;
import com.craftsmanship.tfm.idls.v2.OrderPersistence.GetOrderRequest;
import com.craftsmanship.tfm.idls.v2.OrderPersistence.GetOrderResponse;
import com.craftsmanship.tfm.idls.v2.OrderPersistence.GrpcOrder;
import com.craftsmanship.tfm.idls.v2.OrderPersistence.ListOrderResponse;
import com.craftsmanship.tfm.idls.v2.OrderPersistence.UpdateOrderRequest;
import com.craftsmanship.tfm.idls.v2.OrderPersistence.UpdateOrderResponse;
import com.craftsmanship.tfm.idls.v2.OrderPersistenceServiceGrpc.OrderPersistenceServiceBlockingStub;
import com.craftsmanship.tfm.models.Order;
import com.craftsmanship.tfm.utils.DomainConversion;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;

public class OrderPersistenceGrpcClient {
    private static final Logger logger = LoggerFactory.getLogger(OrderPersistenceGrpcClient.class);

    private final ManagedChannel channel;
    private final OrderPersistenceServiceBlockingStub blockingStub;
    private DomainConversion domainConversion;

    /**
     * Construct client for accessing OrderPersistenceService server at
     * {@code host:port}.
     */
    public OrderPersistenceGrpcClient(String host, int port) {
        this(ManagedChannelBuilder.forAddress(host, port).usePlaintext().build());
    }

    /**
     * Construct client for accessing OrderPersistenceService server using the
     * existing channel.
     */
    public OrderPersistenceGrpcClient(ManagedChannel channel) {
        this.channel = channel;
        this.blockingStub = OrderPersistenceServiceGrpc.newBlockingStub(channel);
        this.domainConversion = new DomainConversion();
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    public Order create(Order order) throws ItemDoesNotExist {
        logger.info("Creating Order");

        GrpcOrder grpcOrder = domainConversion.getGrpcOrderFromOrder(order);

        CreateOrderRequest request = CreateOrderRequest.newBuilder().setOrder(grpcOrder).build();

        Order orderReceived = null;
        try {
            CreateOrderResponse response = blockingStub.create(request);
            orderReceived = domainConversion.getOrderFromGrpcOrder(response.getOrder());
        } catch (StatusRuntimeException e) {
            logger.error("Exception creating Order: " + e.getMessage());
            Status status = Status.fromThrowable(e);
            if (status.getCode() == Status.Code.NOT_FOUND) {
                // TODO: how to get the item id from the error?
                throw new ItemDoesNotExist(0L);
            } else if (status.getCode() == Status.Code.INTERNAL) {
                throw new RuntimeException(status.getDescription());
            } else {
                throw new RuntimeException("UNKNOWN ERROR");
            }
        }

        return orderReceived;
    }

    public List<Order> list() {
        logger.info("List all Orders");

        Empty request = Empty.newBuilder().build();

        List<Order> result = new ArrayList<>();
        try {
            ListOrderResponse response = blockingStub.list(request);
            List<GrpcOrder> ordersResponse = response.getListOfOrdersList();
    
            for (GrpcOrder grpcOrder : ordersResponse) {
                result.add(domainConversion.getOrderFromGrpcOrder(grpcOrder));
            }
        } catch (StatusRuntimeException e) {
            logger.error("Exception listing orders: " + e.getMessage());
            Status status = Status.fromThrowable(e);
            if (status.getCode() == Status.Code.INTERNAL) {
                throw new RuntimeException(status.getDescription());
            } else {
                throw new RuntimeException("UNKNOWN ERROR");
            }
        }

        return result;
    }

    public Order get(Long id) throws OrderDoesNotExist {
        logger.info("Get order with id: " + id);

        GetOrderRequest request = GetOrderRequest.newBuilder().setId(id).build();

        Order order = null;
        try {
            GetOrderResponse response = blockingStub.get(request);
            order = domainConversion.getOrderFromGrpcOrder(response.getOrder());
        } catch (StatusRuntimeException e) {
            logger.error("Exception getting order with id " + id + ": " + e.getMessage());
            Status status = Status.fromThrowable(e);
            if (status.getCode() == Status.Code.NOT_FOUND) {
                throw new OrderDoesNotExist(id);
            } else if (status.getCode() == Status.Code.INTERNAL) {
                throw new RuntimeException(status.getDescription());
            } else {
                throw new RuntimeException("UNKNOWN ERROR");
            }
        }

        return order;
    }

    public Order update(Long id, Order order) throws OrderDoesNotExist, ItemDoesNotExist {
        logger.info("Updating order with id: " + id);

        UpdateOrderRequest request = UpdateOrderRequest.newBuilder().setId(id)
                .setOrder(domainConversion.getGrpcOrderFromOrder(order)).build();

        Order updatedOrder = null;
        try {
            UpdateOrderResponse response = blockingStub.update(request);
            updatedOrder = domainConversion.getOrderFromGrpcOrder(response.getOrder());
        } catch (StatusRuntimeException e) {
            logger.error("Exception updating order with id " + id + ": " + e.getMessage());
            Status status = Status.fromThrowable(e);
            if (status.getCode() == Status.Code.NOT_FOUND) {
                if (status.getDescription().contains("Item")) {
                    throw new ItemDoesNotExist(e.getMessage());
                } else {
                    throw new OrderDoesNotExist(id);
                }
            } else if (status.getCode() == Status.Code.INTERNAL) {
                throw new RuntimeException(status.getDescription());
            } else {
                throw new RuntimeException("UNKNOWN ERROR");
            }
        }

        return updatedOrder;
    }

    public Order delete(Long id) throws OrderDoesNotExist {
        logger.info("Deleting order with id: " + id);

        DeleteOrderRequest request = DeleteOrderRequest.newBuilder().setId(id).build();

        Order order = null;
        try {
            DeleteOrderResponse response = blockingStub.delete(request);
            order = domainConversion.getOrderFromGrpcOrder(response.getOrder());
        } catch (StatusRuntimeException e) {
            logger.error("Exception deleting order with id " + id + ": " + e.getMessage());
            Status status = Status.fromThrowable(e);
            if (status.getCode() == Status.Code.NOT_FOUND) {
                throw new OrderDoesNotExist(id);
            } else if (status.getCode() == Status.Code.INTERNAL) {
                throw new RuntimeException(status.getDescription());
            } else {
                throw new RuntimeException("UNKNOWN ERROR");
            }
        }

        return order;
    }
}