package com.craftsmanship.tfm.stockchecker.grpc;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import com.craftsmanship.tfm.idls.v1.ItemPersistenceServiceGrpc;
import com.craftsmanship.tfm.idls.v1.ItemPersistence.CountItemResponse;
import com.craftsmanship.tfm.idls.v1.ItemPersistence.Empty;
import com.craftsmanship.tfm.idls.v1.ItemPersistenceServiceGrpc.ItemPersistenceServiceBlockingStub;

public class ItemPersistenceGrpcClient {
    private static final Logger logger = LoggerFactory.getLogger(ItemPersistenceGrpcClient.class);

    private final ManagedChannel channel;
    private final ItemPersistenceServiceBlockingStub blockingStub;
    
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
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    public int count() {
        logger.info("Count all Items");

        Empty request = Empty.newBuilder().build();

        CountItemResponse response = blockingStub.count(request);
        return response.getNumberOfItems();
    }
}