package com.craftsmanship.tfm.restapi.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestApiController {
    @RequestMapping("/greeting")
    public String greeting() {
        return "Hello!";
    }
}