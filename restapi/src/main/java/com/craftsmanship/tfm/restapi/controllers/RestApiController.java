package com.craftsmanship.tfm.restapi.controllers;

import com.craftsmanship.tfm.restapi.kafka.model.Item;
import com.craftsmanship.tfm.restapi.kafka.model.ItemOperation;
import com.craftsmanship.tfm.restapi.kafka.service.ItemOperationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/")
public class RestApiController {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestApiController.class);

    private final ItemOperationService itemOperationService;

    public RestApiController(ItemOperationService itemOperationService) {
        this.itemOperationService = itemOperationService;
    }

    @RequestMapping(value = "/items", method = RequestMethod.POST)
    public Item create(@RequestBody Item item) {
        LOGGER.info("Creating item");

        itemOperationService.sendItemOperation(new ItemOperation(item));
        return item;
    }
}