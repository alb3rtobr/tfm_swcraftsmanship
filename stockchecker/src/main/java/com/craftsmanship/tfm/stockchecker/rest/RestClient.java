
package com.craftsmanship.tfm.stockchecker.rest;

import org.springframework.web.client.RestTemplate;

import com.craftsmanship.tfm.models.Item;

public interface RestClient {

	public PurchaseOrder sendPurchaseOrder(Item item, int currentStock);

	public RestTemplate getRestTemplate();
}
