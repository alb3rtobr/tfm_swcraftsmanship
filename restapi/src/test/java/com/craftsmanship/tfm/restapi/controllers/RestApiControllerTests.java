package com.craftsmanship.tfm.restapi.controllers;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.rule.KafkaEmbedded;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@EmbeddedKafka
public class RestApiControllerTests {
    @LocalServerPort
    private int port;

    @Autowired
    private RestApiController restApiController;

    @Test
    public void contexLoads() throws Exception {
        assertThat(restApiController).isNotNull();
    }

    @Test
    public void test_when_item_is_added_then_kafka_topic_is_added() {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:" + port + "/greetings?message=hola", String.class);
        //assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
    }

}
