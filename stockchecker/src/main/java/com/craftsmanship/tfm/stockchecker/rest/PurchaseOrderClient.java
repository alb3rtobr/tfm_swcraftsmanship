package com.craftsmanship.tfm.stockchecker.rest;

import org.springframework.web.client.RestTemplate;

import com.craftsmanship.tfm.models.Item;

public class PurchaseOrderClient implements RestClient {

	private final String uri;
	
	public PurchaseOrderClient(String restHost, int restPort, String restEndPoint) {
		
		uri = "http://"+restHost+":"+restPort+"/"+restEndPoint;
	}

	@Override
	public void sendPurchaseOrder(Item item) {
		
	    PurchaseOrder newOrder = new PurchaseOrder(item);
	 
	    RestTemplate restTemplate = new RestTemplate();
	    PurchaseOrder result=restTemplate.postForObject( uri, newOrder, PurchaseOrder.class);
	 
	    System.out.println(result);
	}

}
