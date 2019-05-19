package com.craftsmanship.tfm.stockchecker.rest.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import com.craftsmanship.tfm.models.Item;
import com.craftsmanship.tfm.stockchecker.rest.PurchaseOrder;
import com.craftsmanship.tfm.stockchecker.rest.RestClient;

@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext
public class PurchaseOrderClientTest {

	@Autowired
	private RestClient client;
	
	@Test
	public void whenItemsAreBelowThreshold_thenRestApiIsCalledWithExpectedItem() throws Exception {
		
		Item item= new Item.Builder().withDescription("PlayStation4").build();
		PurchaseOrder nullOrder = client.sendPurchaseOrder(item, 3);
		assertNull(nullOrder);
	}
	
	@Test
	public void whenItemsAreAboveThreshold_thenRestApiIsNotCalled() {
		Item item= new Item.Builder().withDescription("MegaDrive").build();
		PurchaseOrder order = client.sendPurchaseOrder(item, 0);
		PurchaseOrder expected= new PurchaseOrder(item);
		assertNotNull(order);
		assertEquals("PurchaseOrder generated is not the expected.",order.getItem().getDescription(),expected.getItem().getDescription());
	}
}
