package com.craftsmanship.tfm.stockchecker.grpc;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.craftsmanship.tfm.idls.v1.ItemPersistenceServiceGrpc;
import com.craftsmanship.tfm.idls.v1.ItemPersistenceServiceGrpc.ItemPersistenceServiceBlockingStub;
import com.craftsmanship.tfm.models.Item;

import com.craftsmanship.tfm.testing.grpc.ItemPersistenceInProcessServer;
import com.craftsmanship.tfm.testing.persistence.ItemsPersistenceStub;

import io.grpc.ManagedChannel;
import io.grpc.inprocess.InProcessChannelBuilder;

public class ItemsPersistenceGrpcTests {
	private static final Logger LOGGER = LoggerFactory.getLogger(ItemsPersistenceGrpcTests.class);
	
	private ItemPersistenceInProcessServer itemPersistenceGrpcServer;
	private ItemsPersistenceGrpc grpcClient;
	private ItemPersistenceServiceBlockingStub blockingStub;

	
    @Before
    public void setUp() throws IOException, InstantiationException, IllegalAccessException {
    	// Create the Item Persistence stub
    	ItemsPersistenceStub itemPersistenceStub = new ItemsPersistenceStub(); 
    	itemPersistenceGrpcServer = new ItemPersistenceInProcessServer(itemPersistenceStub);
    	itemPersistenceGrpcServer.start();
    	ManagedChannel channel = InProcessChannelBuilder
    			.forName("test")
    			.directExecutor()
    			// Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid
    			// needing certificates.
    			.usePlaintext(true)
    			.build();
    	grpcClient = new ItemsPersistenceGrpc(channel);
    	blockingStub = ItemPersistenceServiceGrpc.newBlockingStub(channel);
    }

    // TODO: the server stop should be AfterClass and initialize @After
    @After
    public void tearDown() throws InterruptedException {
    	grpcClient.close();
    	itemPersistenceGrpcServer.stop();
    }
    
    @Test
    public void given_anItemWithNoStock_then_countReturnZero() {
        
        int count = grpcClient.count();
        assertThat(count, equalTo(0));
    }
    
    @Test
    public void given_anItemWithStock_then_countReturnTheStock() {
        
        Item item = new Item.Builder().withDescription("MegaDrive").build();
        grpcClient.create(item);
        
        int count = grpcClient.count();

        assertThat(count, equalTo(1));
    }
}
