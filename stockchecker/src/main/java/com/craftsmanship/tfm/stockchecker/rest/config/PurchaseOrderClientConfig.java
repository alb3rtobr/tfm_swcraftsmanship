package com.craftsmanship.tfm.stockchecker.rest.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.craftsmanship.tfm.stockchecker.rest.PurchaseOrderClient;
import com.craftsmanship.tfm.stockchecker.rest.RestClient;

@Configuration
@EnableAutoConfiguration
@ConfigurationProperties(prefix = "stockchecker")
public class PurchaseOrderClientConfig {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PurchaseOrderClientConfig.class);
	
	@Value(value = "${stockchecker.threshold}")
	private int MIN_STOCK_THRESHOLD;
	
	@Value(value = "${stockchecker.rest.host}")
	private String restHost;

	@Value(value = "${stockchecker.rest.port}")
	private int restPort;
	
	@Value(value = "${stockchecker.rest.endpoint}")
	private String restEndPoint;
	
	@Bean
	public RestClient restClient() {
		return new PurchaseOrderClient(restHost,restPort,restEndPoint,MIN_STOCK_THRESHOLD);
	}
	
	public int getThreshold() {
		return this.MIN_STOCK_THRESHOLD;
	}
}
