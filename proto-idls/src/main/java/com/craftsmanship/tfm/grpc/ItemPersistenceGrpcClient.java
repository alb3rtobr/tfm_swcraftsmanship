package com.craftsmanship.tfm.grpc;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.craftsmanship.tfm.models.DomainItem;
import com.craftsmanship.tfm.utils.ConversionUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;

import com.craftsmanship.tfm.exceptions.ItemAlreadyExists;
import com.craftsmanship.tfm.exceptions.ItemDoesNotExist;
import com.craftsmanship.tfm.idls.v2.ItemPersistenceServiceGrpc;
import com.craftsmanship.tfm.idls.v2.ItemPersistence.CreateItemRequest;
import com.craftsmanship.tfm.idls.v2.ItemPersistence.CreateItemResponse;
import com.craftsmanship.tfm.idls.v2.ItemPersistence.DeleteItemRequest;
import com.craftsmanship.tfm.idls.v2.ItemPersistence.DeleteItemResponse;
import com.craftsmanship.tfm.idls.v2.ItemPersistence.Empty;
import com.craftsmanship.tfm.idls.v2.ItemPersistence.GetItemRequest;
import com.craftsmanship.tfm.idls.v2.ItemPersistence.GetItemResponse;
import com.craftsmanship.tfm.idls.v2.ItemPersistence.ListItemResponse;
import com.craftsmanship.tfm.idls.v2.ItemPersistence.UpdateItemRequest;
import com.craftsmanship.tfm.idls.v2.ItemPersistence.UpdateItemResponse;
import com.craftsmanship.tfm.idls.v2.ItemPersistence.GrpcItem;
import com.craftsmanship.tfm.idls.v2.ItemPersistenceServiceGrpc.ItemPersistenceServiceBlockingStub;

public class ItemPersistenceGrpcClient {
    private static final Logger logger = LoggerFactory.getLogger(ItemPersistenceGrpcClient.class);

    private final ManagedChannel channel;
    private final ItemPersistenceServiceBlockingStub blockingStub;

    /**
     * Construct client for accessing ItemPersistenceService server at
     * {@code host:port}.
     */
    public ItemPersistenceGrpcClient(String host, int port) {
        this(ManagedChannelBuilder.forAddress(host, port).usePlaintext().build());
    }

    /**
     * Construct client for accessing ItemPersistenceService server using the
     * existing channel.
     */
    public ItemPersistenceGrpcClient(ManagedChannel channel) {
        this.channel = channel;
        this.blockingStub = ItemPersistenceServiceGrpc.newBlockingStub(channel);
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    public DomainItem create(DomainItem item) throws ItemAlreadyExists {
        logger.info("Creating Item");

        GrpcItem grpcItem = ConversionUtils.getGrpcItemFromItem(item);

        CreateItemRequest request = CreateItemRequest.newBuilder().setItem(grpcItem).build();

        DomainItem itemReceived = null;
        try {
            CreateItemResponse response = blockingStub.create(request);
            itemReceived = ConversionUtils.getItemFromGrpcItem(response.getItem());
        } catch (StatusRuntimeException e) {
            logger.error("Exception creating Item: " + e.getMessage());
            Status status = Status.fromThrowable(e);
            if (status.getCode() == Status.Code.ALREADY_EXISTS) {
                throw new ItemAlreadyExists(item.getName());
            } else if (status.getCode() == Status.Code.INTERNAL) {
                throw new RuntimeException(status.getDescription());
            } else {
                throw new RuntimeException("UNKNOWN ERROR");
            }
        }

        return itemReceived;
    }

    public List<DomainItem> list() {
        logger.info("List all Items");

        Empty request = Empty.newBuilder().build();

        List<DomainItem> result = new ArrayList<>();
        try {
            ListItemResponse response = blockingStub.list(request);
            List<GrpcItem> itemsResponse = response.getItemList();
    
            for (GrpcItem grpcItem : itemsResponse) {
                result.add(ConversionUtils.getItemFromGrpcItem(grpcItem));
            }
        } catch (StatusRuntimeException e) {
            logger.error("Exception listing items: " + e.getMessage());
            Status status = Status.fromThrowable(e);
            if (status.getCode() == Status.Code.INTERNAL) {
                throw new RuntimeException(status.getDescription());
            } else {
                throw new RuntimeException("UNKNOWN ERROR");
            }
        }

        return result;
    }

    public DomainItem get(Long id) throws ItemDoesNotExist {
        logger.info("Get item with id: " + id);

        GetItemRequest request = GetItemRequest.newBuilder().setId(id).build();

        DomainItem item = null;
        try {
            GetItemResponse response = blockingStub.get(request);
            item = ConversionUtils.getItemFromGrpcItem(response.getItem());
        } catch (StatusRuntimeException e) {
            logger.error("Exception getting item with id " + id + ": " + e.getMessage());
            Status status = Status.fromThrowable(e);
            if (status.getCode() == Status.Code.NOT_FOUND) {
                throw new ItemDoesNotExist(id);
            } else if (status.getCode() == Status.Code.INTERNAL) {
                throw new RuntimeException(status.getDescription());
            } else {
                throw new RuntimeException("UNKNOWN ERROR");
            }
        }

        return item;
    }

    public DomainItem update(Long id, DomainItem item) throws ItemDoesNotExist {
        logger.info("Updating item with id: " + id);

        UpdateItemRequest request = UpdateItemRequest.newBuilder().setId(id)
                .setItem(ConversionUtils.getGrpcItemFromItem(item)).build();

        DomainItem updatedItem = null;
        try {
            UpdateItemResponse response = blockingStub.update(request);
            updatedItem = ConversionUtils.getItemFromGrpcItem(response.getItem());
        } catch (StatusRuntimeException e) {
            logger.error("Exception updating item with id " + id + ": " + e.getMessage());
            Status status = Status.fromThrowable(e);
            if (status.getCode() == Status.Code.NOT_FOUND) {
                throw new ItemDoesNotExist(id);
            } else if (status.getCode() == Status.Code.INTERNAL) {
                throw new RuntimeException(status.getDescription());
            } else {
                throw new RuntimeException("UNKNOWN ERROR");
            }
        }

        return updatedItem;
    }

    public DomainItem delete(Long id) throws ItemDoesNotExist {
        logger.info("Deleting item with id: " + id);

        DeleteItemRequest request = DeleteItemRequest.newBuilder().setId(id).build();

        DomainItem item = null;
        try {
            DeleteItemResponse response = blockingStub.delete(request);
            item = ConversionUtils.getItemFromGrpcItem(response.getItem());
        } catch (StatusRuntimeException e) {
            logger.error("Exception deleting item with id " + id + ": " + e.getMessage());
            Status status = Status.fromThrowable(e);
            if (status.getCode() == Status.Code.NOT_FOUND) {
                throw new ItemDoesNotExist(id);
            } else if (status.getCode() == Status.Code.INTERNAL) {
                throw new RuntimeException(status.getDescription());
            } else {
                throw new RuntimeException("UNKNOWN ERROR");
            }
        }

        return item;
    }
}