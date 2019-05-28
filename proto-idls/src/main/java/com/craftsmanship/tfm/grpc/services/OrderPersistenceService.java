package com.craftsmanship.tfm.grpc.services;

import com.craftsmanship.tfm.exceptions.ItemDoesNotExist;
import com.craftsmanship.tfm.exceptions.OrderDoesNotExist;
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
import com.craftsmanship.tfm.models.DomainOrder;
import com.craftsmanship.tfm.models.Order;
import com.craftsmanship.tfm.persistence.OrderPersistence;
import com.craftsmanship.tfm.utils.ConversionLogic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.grpc.Status;

public class OrderPersistenceService extends OrderPersistenceServiceImplBase {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderPersistenceService.class);

    private OrderPersistence orderPersistence;
    private ConversionLogic conversionLogic;

    public OrderPersistenceService(OrderPersistence orderPersistenceStub, ConversionLogic conversionLogic) {
        this.orderPersistence = orderPersistenceStub;
        this.conversionLogic = conversionLogic;
    }

    @Override
    public void create(CreateOrderRequest request, io.grpc.stub.StreamObserver<CreateOrderResponse> responseObserver) {
        LOGGER.info("CREATE RPC CALLED");
        GrpcOrder grpcOrder = request.getOrder();
        Order order = conversionLogic.getOrderFromGrpcOrder(grpcOrder);

        try {
            Order createdOrder = orderPersistence.create(order);

            GrpcOrder grpcOrderResponse = conversionLogic.getGrpcOrderFromOrder(createdOrder);
            CreateOrderResponse response = CreateOrderResponse.newBuilder().setOrder(grpcOrderResponse).build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (ItemDoesNotExist e) {
            responseObserver.onError(Status.NOT_FOUND.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void list(Empty request, io.grpc.stub.StreamObserver<ListOrderResponse> responseObserver) {
        LOGGER.info("LIST RPC CALLED");

        Builder responseBuilder = ListOrderResponse.newBuilder();
        for (Order order : orderPersistence.list()) {
            GrpcOrder grpcOrder = conversionLogic.getGrpcOrderFromOrder(order);
            responseBuilder.addListOfOrders(grpcOrder);
        }
        ListOrderResponse response = responseBuilder.build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void get(GetOrderRequest request, io.grpc.stub.StreamObserver<GetOrderResponse> responseObserver) {
        LOGGER.info("GET RPC CALLED");

        try {
            Order order = orderPersistence.get(request.getId());
            GrpcOrder grpcOrder = conversionLogic.getGrpcOrderFromOrder(order);
            GetOrderResponse response = GetOrderResponse.newBuilder().setOrder(grpcOrder).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (OrderDoesNotExist e) {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void update(UpdateOrderRequest request, io.grpc.stub.StreamObserver<UpdateOrderResponse> responseObserver) {
        LOGGER.info("UPDATE RPC CALLED");

        try {
            orderPersistence.get(request.getId());

            Order order = conversionLogic.getOrderFromGrpcOrder(request.getOrder());
            Order updatedOrder = orderPersistence.update(request.getId(), order);

            GrpcOrder grpcOrderResponse = conversionLogic.getGrpcOrderFromOrder(updatedOrder);

            UpdateOrderResponse response = UpdateOrderResponse.newBuilder().setOrder(grpcOrderResponse).build();
    
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (OrderDoesNotExist e) {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription(e.getMessage()).asRuntimeException());

        } catch (ItemDoesNotExist e) {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void delete(DeleteOrderRequest request, io.grpc.stub.StreamObserver<DeleteOrderResponse> responseObserver) {
        LOGGER.info("DELETE RPC CALLED");

        try {
            Order deletedOrder = orderPersistence.delete(request.getId());

            GrpcOrder grpcOrderResponse = conversionLogic.getGrpcOrderFromOrder(deletedOrder);
            DeleteOrderResponse response = DeleteOrderResponse.newBuilder().setOrder(grpcOrderResponse).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (OrderDoesNotExist e) {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription(e.getMessage()).asRuntimeException());

        }
    }
}
