package com.craftsmanship.tfm.restapi.controllers;

import com.craftsmanship.tfm.restapi.kafka.model.Greetings;
import com.craftsmanship.tfm.restapi.kafka.service.GreetingsService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestApiController {
    private final GreetingsService greetingsService;

    public RestApiController(GreetingsService greetingsService) {
        this.greetingsService = greetingsService;
    }

    @RequestMapping("/greetings")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String greetings(@RequestParam("message") String message) {
        Greetings greetings = new Greetings.Builder().withMessage(message).withTimestamp(System.currentTimeMillis()).build();
        greetingsService.sendGreeting(greetings);
        return "OK";
    }
}