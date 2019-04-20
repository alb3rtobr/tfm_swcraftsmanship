package com.craftsmanship.tfm.restapi.kafka.config;

import com.craftsmanship.tfm.restapi.kafka.streams.GreetingsStreams;

import org.springframework.cloud.stream.annotation.EnableBinding;

@EnableBinding(GreetingsStreams.class)
public class StreamsConfig {
}