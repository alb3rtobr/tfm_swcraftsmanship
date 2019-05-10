package com.craftsmanship.tfm.stockchecker.rest;

import org.springframework.web.client.RestTemplate;

import com.craftsmanship.tfm.models.Item;

public class RestClientStub implements RestClient {

	@Override
	public PurchaseOrder sendPurchaseOrder(Item item) {
		return null;
	}
	
	//TODO
	public RestTemplate getRestTemplate() {
		return null;
	}

}
