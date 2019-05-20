package com.craftsmanship.tfm.testing.grpc;

import java.util.concurrent.TimeUnit;

import com.craftsmanship.tfm.models.Item;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import com.craftsmanship.tfm.idls.v1.ItemPersistenceServiceGrpc;
import com.craftsmanship.tfm.idls.v1.ItemPersistence.CreateItemRequest;
import com.craftsmanship.tfm.idls.v1.ItemPersistence.CreateItemResponse;
import com.craftsmanship.tfm.idls.v1.ItemPersistence.GrpcItem;
import com.craftsmanship.tfm.idls.v1.ItemPersistenceServiceGrpc.ItemPersistenceServiceBlockingStub;
import com.craftsmanship.tfm.idls.v1.ItemPersistenceServiceGrpc.ItemPersistenceServiceStub;

public class ItemPersistenceExampleClient {
    private static final Logger logger = LoggerFactory.getLogger(ItemPersistenceExampleClient.class);

    private final ManagedChannel channel;
    private final ItemPersistenceServiceBlockingStub blockingStub;
    private final ItemPersistenceServiceStub asyncStub;

    /** Construct client for accessing RouteGuide server at {@code host:port}. */
    public ItemPersistenceExampleClient(String host, int port) {
        this(ManagedChannelBuilder.forAddress(host, port).usePlaintext());
    }

    /**
     * Construct client for accessing RouteGuide server using the existing channel.
     */
    public ItemPersistenceExampleClient(ManagedChannelBuilder<?> channelBuilder) {
        channel = channelBuilder.build();
        blockingStub = ItemPersistenceServiceGrpc.newBlockingStub(channel);
        asyncStub = ItemPersistenceServiceGrpc.newStub(channel);
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    public Item createItem(Item item) {
        logger.info("Creating Item");

        GrpcItem grpcItem = GrpcItem.newBuilder().setDescription(item.getName()).build();

        CreateItemRequest request = CreateItemRequest.newBuilder().setItem(grpcItem).build();

        CreateItemResponse response = blockingStub.create(request);
        GrpcItem itemResponse = response.getItem();
        logger.info("Received GrpcItem: " + grpcItem);

        Item result = new Item.Builder().withId(grpcItem.getId()).withName(grpcItem.getDescription()).build();

        return result;
    }

    public static void main(String[] args) throws InterruptedException {

        logger.info("Starting...");

        ItemPersistenceExampleClient client = new ItemPersistenceExampleClient("localhost", 50051);

        try {
            Item item = new Item.Builder().withName("Zapato").build();

            // Looking for a valid feature
            Item receivedItem = client.createItem(item);
            logger.info("Received item: " + receivedItem);
        } finally {
            client.shutdown();
        }

        logger.info("Closing.");
    }
}