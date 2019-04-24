package com.craftsmanship.tfm.restapi.kafka.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Service
public class GreetingsService {
    private static final Logger LOGGER = LoggerFactory.getLogger(GreetingsService.class);

    private static final String MYTOPIC = "mytopic";

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessage(String message) {

        LOGGER.info("KIKO: Sending Message to topic " + MYTOPIC);
             
        ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(MYTOPIC, message);
         
        future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
     
            @Override
            public void onSuccess(SendResult<String, String> result) {
                LOGGER.info("KIKO: Sent message=[" + message + 
                  "] with offset=[" + result.getRecordMetadata().offset() + "]");
            }
            @Override
            public void onFailure(Throwable ex) {
                LOGGER.error("KIKO: Unable to send message=["
                  + message + "] due to : " + ex.getMessage());
            }
        });
    }
}