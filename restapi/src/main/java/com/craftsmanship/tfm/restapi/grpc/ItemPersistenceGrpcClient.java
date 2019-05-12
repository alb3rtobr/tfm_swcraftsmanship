package com.craftsmanship.tfm.restapi.grpc;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.craftsmanship.tfm.models.Item;
import com.craftsmanship.tfm.utils.ConversionUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import com.craftsmanship.tfm.idls.v1.ItemPersistenceServiceGrpc;
import com.craftsmanship.tfm.idls.v1.ItemPersistence.CountItemResponse;
import com.craftsmanship.tfm.idls.v1.ItemPersistence.CreateItemRequest;
import com.craftsmanship.tfm.idls.v1.ItemPersistence.CreateItemResponse;
import com.craftsmanship.tfm.idls.v1.ItemPersistence.Empty;
import com.craftsmanship.tfm.idls.v1.ItemPersistence.GetItemRequest;
import com.craftsmanship.tfm.idls.v1.ItemPersistence.GetItemResponse;
import com.craftsmanship.tfm.idls.v1.ItemPersistence.ListItemResponse;
import com.craftsmanship.tfm.idls.v1.ItemPersistence.UpdateItemRequest;
import com.craftsmanship.tfm.idls.v1.ItemPersistence.UpdateItemResponse;
import com.craftsmanship.tfm.idls.v1.ItemPersistence.GrpcItem;
import com.craftsmanship.tfm.idls.v1.ItemPersistenceServiceGrpc.ItemPersistenceServiceBlockingStub;
import com.craftsmanship.tfm.idls.v1.ItemPersistenceServiceGrpc.ItemPersistenceServiceStub;

public class ItemPersistenceGrpcClient {
    private static final Logger logger = LoggerFactory.getLogger(ItemPersistenceGrpcClient.class);

    private final ManagedChannel channel;
    private final ItemPersistenceServiceBlockingStub blockingStub;
    private final ItemPersistenceServiceStub asyncStub;

    /** Construct client for accessing ItemPersistenceService server at {@code host:port}. */
    public ItemPersistenceGrpcClient(String host, int port) {
        this(ManagedChannelBuilder.forAddress(host, port).usePlaintext().build());
    }

    /**
     * Construct client for accessing ItemPersistenceService server using the existing channel.
     */
    public ItemPersistenceGrpcClient(ManagedChannel channel) {
        this.channel = channel;
        this.blockingStub = ItemPersistenceServiceGrpc.newBlockingStub(channel);
        this.asyncStub = ItemPersistenceServiceGrpc.newStub(channel);
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    public Item create(Item item) {
        logger.info("Creating Item");

        GrpcItem grpcItem = GrpcItem.newBuilder().setDescription(item.getDescription()).build();

        CreateItemRequest request = CreateItemRequest.newBuilder().setItem(grpcItem).build();

        CreateItemResponse response = blockingStub.create(request);
        GrpcItem grpcItemResponse = response.getItem();
        logger.info("Received GrpcItem: " + grpcItem);

        return ConversionUtils.getItemFromGrpcItem(grpcItemResponse);
    }

    public List<Item> list() {
        logger.info("List all Items");

        Empty request = Empty.newBuilder().build();

        ListItemResponse response = blockingStub.list(request);
        List<GrpcItem> itemsResponse = response.getItemList();

        List<Item> result = new ArrayList<>();
        for (GrpcItem grpcItem: itemsResponse) {
            result.add(ConversionUtils.getItemFromGrpcItem(grpcItem));
        }

        return result;
    }

    public Item get(Long id) {
        logger.info("Get item with id: " + id);

        GetItemRequest request = GetItemRequest.newBuilder().setId(id).build();

        GetItemResponse response = blockingStub.get(request);

        return ConversionUtils.getItemFromGrpcItem(response.getItem());
    }

    public Item update(Long id, Item item) {
        logger.info("Updating item with id: " + id);

        UpdateItemRequest request = UpdateItemRequest.newBuilder()
            .setId(id)
            .setItem(ConversionUtils.getGrpcItemFromItem(item))
            .build();

        UpdateItemResponse response = blockingStub.update(request);

        return ConversionUtils.getItemFromGrpcItem(response.getItem());
    }

    public int count() {
        logger.info("Count all Items");

        Empty request = Empty.newBuilder().build();

        CountItemResponse response = blockingStub.count(request);
        return response.getNumberOfItems();
    }
}