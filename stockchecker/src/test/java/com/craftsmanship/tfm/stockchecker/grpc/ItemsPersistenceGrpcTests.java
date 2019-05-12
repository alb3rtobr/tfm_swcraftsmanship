package com.craftsmanship.tfm.stockchecker.grpc;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.craftsmanship.tfm.models.Item;

import com.craftsmanship.tfm.testing.grpc.ItemPersistenceExampleServer;

public class ItemsPersistenceGrpcTests {
	private static final Logger LOGGER = LoggerFactory.getLogger(ItemsPersistenceGrpcTests.class);

    private class GrpcServerRunnable implements Runnable {
        private ItemPersistenceExampleServer gRpcServer;

        public synchronized void doStop() throws InterruptedException {
            LOGGER.info("Stopping gRPC Server...");
            gRpcServer.stop();
            gRpcServer.blockUntilShutdown();
        }

        public synchronized void initialize() {
            LOGGER.info("Initializing gRPC Server...");
            gRpcServer.initialize();
        }

        @Override
        public void run() {
            gRpcServer = new ItemPersistenceExampleServer(50051);
            try {
                LOGGER.info("Starting gRPC Server...");
                gRpcServer.start();
                gRpcServer.blockUntilShutdown();
            } catch (Exception e) {
                throw new RuntimeException("Exception running gRPC Server");
            }
        }
    }

    private GrpcServerRunnable grpcServerRunnable;

    // TODO: This should be BeforeClass
    @Before
    public void setUp() {
        grpcServerRunnable = new GrpcServerRunnable();
        Thread thread = new Thread(grpcServerRunnable);
        thread.start();
    }

    // TODO: the server stop should be AfterClass and initialize @After
    @After
    public void tearDown() throws InterruptedException {
        //TODO
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        grpcServerRunnable.initialize();
        grpcServerRunnable.doStop();
    }
    
    @Test
    public void given_anItemWithNoStock_then_countReturnZero() {
        
        ItemsPersistenceGrpc itemsPersistenceGrpc = new ItemsPersistenceGrpc("localhost", 50051);
        
        int count = itemsPersistenceGrpc.count();

        assertThat(count, equalTo(0));
    }
    
    @Test
    public void given_anItemWithStock_then_countReturnTheStock() {
        
        ItemsPersistenceGrpc itemsPersistenceGrpc = new ItemsPersistenceGrpc("localhost", 50051);
        Item item = new Item.Builder().withDescription("MegaDrive").build();
        itemsPersistenceGrpc.create(item);
        
        int count = itemsPersistenceGrpc.count();

        assertThat(count, equalTo(1));
    }
}
