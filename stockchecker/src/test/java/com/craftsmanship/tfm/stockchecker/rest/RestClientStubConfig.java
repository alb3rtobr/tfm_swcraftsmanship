package com.craftsmanship.tfm.stockchecker.rest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("dev")
public class RestClientStubConfig {


	@Bean
	public RestClient restClient() {
		return new RestClientStub();
	}
}
