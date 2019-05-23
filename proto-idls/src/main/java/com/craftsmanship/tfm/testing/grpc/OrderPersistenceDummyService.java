package com.craftsmanship.tfm.testing.grpc;

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
import com.craftsmanship.tfm.idls.v2.OrderPersistence.ListOrderResponse.Builder;
import com.craftsmanship.tfm.idls.v2.OrderPersistenceServiceGrpc.OrderPersistenceServiceImplBase;
import com.craftsmanship.tfm.models.Order;
import com.craftsmanship.tfm.testing.persistence.OrderPersistenceStub;
import com.craftsmanship.tfm.utils.ConversionUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.grpc.Status;

public class OrderPersistenceDummyService extends OrderPersistenceServiceImplBase {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderPersistenceDummyService.class);

    private OrderPersistenceStub orderPersistence;

    public OrderPersistenceDummyService(OrderPersistenceStub orderPersistenceStub) {
        this.orderPersistence = orderPersistenceStub;
    }

    @Override
    public void create(CreateOrderRequest request, io.grpc.stub.StreamObserver<CreateOrderResponse> responseObserver) {
        LOGGER.info("CREATE RPC CALLED");
        GrpcOrder grpcOrder = request.getOrder();
        Order order = ConversionUtils.getOrderFromGrpcOrder(grpcOrder);
        Order createdOrder = orderPersistence.create(order);

        GrpcOrder grpcOrderResponse = ConversionUtils.getGrpcOrderFromOrder(createdOrder);
        CreateOrderResponse response = CreateOrderResponse.newBuilder().setOrder(grpcOrderResponse).build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void list(Empty request, io.grpc.stub.StreamObserver<ListOrderResponse> responseObserver) {
        LOGGER.info("LIST RPC CALLED");

        Builder responseBuilder = ListOrderResponse.newBuilder();
        for (Order order : orderPersistence.list()) {
            GrpcOrder grpcOrder = ConversionUtils.getGrpcOrderFromOrder(order);
            responseBuilder.addListOfOrders(grpcOrder);
        }
        ListOrderResponse response = responseBuilder.build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void get(GetOrderRequest request, io.grpc.stub.StreamObserver<GetOrderResponse> responseObserver) {
        LOGGER.info("GET RPC CALLED");

        Order order = orderPersistence.get(request.getId());

        if (order != null) {
            GrpcOrder grpcOrder = ConversionUtils.getGrpcOrderFromOrder(order);
            GetOrderResponse response = GetOrderResponse.newBuilder().setOrder(grpcOrder).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Order with id " + request.getId() + " does not exist").asRuntimeException());
        }
    }

    @Override
    public void update(UpdateOrderRequest request, io.grpc.stub.StreamObserver<UpdateOrderResponse> responseObserver) {
        LOGGER.info("UPDATE RPC CALLED");

        if (orderPersistence.get(request.getId()) != null) {
            Order order = ConversionUtils.getOrderFromGrpcOrder(request.getOrder());
            Order updatedOrder = orderPersistence.update(request.getId(), order);

            GrpcOrder grpcOrderResponse = ConversionUtils.getGrpcOrderFromOrder(updatedOrder);

            UpdateOrderResponse response = UpdateOrderResponse.newBuilder().setOrder(grpcOrderResponse).build();
    
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } else {
            // There is not Order with that id, so exception
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Order with id " + request.getId() + " does not exist").asRuntimeException());
        }
    }

    @Override
    public void delete(DeleteOrderRequest request, io.grpc.stub.StreamObserver<DeleteOrderResponse> responseObserver) {
        LOGGER.info("DELETE RPC CALLED");

        Order deletedOrder = orderPersistence.delete(request.getId());

        if (deletedOrder != null) {
            GrpcOrder grpcOrderResponse = ConversionUtils.getGrpcOrderFromOrder(deletedOrder);
            DeleteOrderResponse response = DeleteOrderResponse.newBuilder().setOrder(grpcOrderResponse).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Order with id " + request.getId() + " does not exist").asRuntimeException());
        }
    }
}
