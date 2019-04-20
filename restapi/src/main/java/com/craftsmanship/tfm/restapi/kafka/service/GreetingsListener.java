package com.craftsmanship.tfm.restapi.kafka.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.craftsmanship.tfm.restapi.kafka.model.Greetings;
import com.craftsmanship.tfm.restapi.kafka.streams.GreetingsStreams;

@Component
@EnableBinding(Greetings.class)
public class GreetingsListener {
    private static Logger logger = LoggerFactory.getLogger(GreetingsListener.class);

    @StreamListener(GreetingsStreams.INPUT)
    public void handleGreetings(@Payload Greetings greetings) {
        logger.info("Received greetings: {}", greetings);
    }
}