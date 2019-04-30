package com.craftsmanship.tfm.restapi.grpc;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.craftsmanship.tfm.models.Item;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import v1.ItemPersistenceServiceGrpc;
import v1.ItemPersistence.CreateItemRequest;
import v1.ItemPersistence.CreateItemResponse;
import v1.ItemPersistence.ListItemRequest;
import v1.ItemPersistence.ListItemResponse;
import v1.ItemPersistence.GrpcItem;
import v1.ItemPersistenceServiceGrpc.ItemPersistenceServiceBlockingStub;
import v1.ItemPersistenceServiceGrpc.ItemPersistenceServiceStub;

public class ItemPersistenceGrpcClient {
    private static final Logger logger = LoggerFactory.getLogger(ItemPersistenceGrpcClient.class);

    private final ManagedChannel channel;
    private final ItemPersistenceServiceBlockingStub blockingStub;
    private final ItemPersistenceServiceStub asyncStub;

    /** Construct client for accessing ItemPersistenceService server at {@code host:port}. */
    public ItemPersistenceGrpcClient(String host, int port) {
        this(ManagedChannelBuilder.forAddress(host, port).usePlaintext());
    }

    /**
     * Construct client for accessing ItemPersistenceService server using the existing channel.
     */
    public ItemPersistenceGrpcClient(ManagedChannelBuilder<?> channelBuilder) {
        channel = channelBuilder.build();
        blockingStub = ItemPersistenceServiceGrpc.newBlockingStub(channel);
        asyncStub = ItemPersistenceServiceGrpc.newStub(channel);
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    public Item createItem(Item item) {
        logger.info("Creating Item");

        GrpcItem grpcItem = GrpcItem.newBuilder().setDescription(item.getDescription()).build();

        CreateItemRequest request = CreateItemRequest.newBuilder().setItem(grpcItem).build();

        CreateItemResponse response = blockingStub.create(request);
        GrpcItem itemResponse = response.getItem();
        logger.info("Received GrpcItem: " + grpcItem);

        Item result = new Item.Builder()
            .withId(itemResponse.getId())
            .withDescription(itemResponse.getDescription()
            ).build();

        return result;
    }

    public List<Item> list() {
        logger.info("List all Items");

        ListItemRequest request = ListItemRequest.newBuilder().build();

        ListItemResponse response = blockingStub.list(request);
        List<GrpcItem> itemsResponse = response.getItemList();

        List<Item> result = new ArrayList<>();
        for (GrpcItem grpcItem: itemsResponse) {
            Item item = new Item.Builder()
                .withId(grpcItem.getId())
                .withDescription(grpcItem.getDescription())
                .build();
            result.add(item);
        }

        return result;
    }
}