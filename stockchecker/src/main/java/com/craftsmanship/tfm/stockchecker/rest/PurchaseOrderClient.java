package com.craftsmanship.tfm.stockchecker.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import com.craftsmanship.tfm.models.Item;

public class PurchaseOrderClient implements RestClient {

	private static final Logger LOGGER = LoggerFactory.getLogger(PurchaseOrderClient.class);
	
	private final String uri;
	
	public PurchaseOrderClient(String restHost, int restPort, String restEndPoint) {
		
		uri = "http://"+restHost+":"+restPort+"/"+restEndPoint;
	}

	@Autowired
    private RestTemplate restTemplate;
	
	public RestTemplate getRestTemplate() {
		return this.restTemplate;
	}
	
	@Override
	public PurchaseOrder sendPurchaseOrder(Item item) {		
		PurchaseOrder newOrder = new PurchaseOrder(item);
	    PurchaseOrder result=restTemplate.postForObject( uri, newOrder, PurchaseOrder.class);
	    LOGGER.info("sendPurchaseOrder returns ["+result.toString()+"]");
	    return result;
	}

}
