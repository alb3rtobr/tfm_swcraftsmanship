package com.craftsmanship.tfm.stockchecker.rest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!dev")
public class PurchaseOrderClientConfig {
	
	@Value(value = "${stockchecker.rest.host}")
	private String restHost;

	@Value(value = "${stockchecker.rest.port}")
	private int restPort;
	
	@Value(value = "${stockchecker.rest.endpoint}")
	private String restEndPoint;
	
	@Bean
	public RestClient restClient() {
		return new PurchaseOrderClient(restHost,restPort,restEndPoint);
	}
}
