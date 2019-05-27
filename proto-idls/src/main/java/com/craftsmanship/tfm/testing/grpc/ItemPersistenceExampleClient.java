package com.craftsmanship.tfm.testing.grpc;

import java.util.concurrent.TimeUnit;

import com.craftsmanship.tfm.models.DomainItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import com.craftsmanship.tfm.idls.v1.ItemPersistenceServiceGrpc;
import com.craftsmanship.tfm.idls.v1.ItemPersistence.CreateItemRequest;
import com.craftsmanship.tfm.idls.v1.ItemPersistence.CreateItemResponse;
import com.craftsmanship.tfm.idls.v1.ItemPersistence.GrpcItem;
import com.craftsmanship.tfm.idls.v1.ItemPersistenceServiceGrpc.ItemPersistenceServiceBlockingStub;

public class ItemPersistenceExampleClient {
    private static final Logger logger = LoggerFactory.getLogger(ItemPersistenceExampleClient.class);

    private final ManagedChannel channel;
    private final ItemPersistenceServiceBlockingStub blockingStub;

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
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    public DomainItem createItem(DomainItem item) {
        logger.info("Creating Item");

        GrpcItem grpcItem = GrpcItem.newBuilder().setDescription(item.getName()).build();

        CreateItemRequest request = CreateItemRequest.newBuilder().setItem(grpcItem).build();

        CreateItemResponse response = blockingStub.create(request);
        GrpcItem itemResponse = response.getItem();
        logger.info("Received GrpcItem: " + itemResponse);

        DomainItem result = new DomainItem.Builder().withId(itemResponse.getId()).withName(itemResponse.getDescription()).build();

        return result;
    }

    public static void main(String[] args) throws InterruptedException {

        logger.info("Starting...");

        ItemPersistenceExampleClient client = new ItemPersistenceExampleClient("localhost", 50051);

        try {
            DomainItem item = new DomainItem.Builder().withName("Zapato").build();

            // Looking for a valid feature
            DomainItem receivedItem = client.createItem(item);
            logger.info("Received item: " + receivedItem);
        } finally {
            client.shutdown();
        }

        logger.info("Closing.");
    }
}