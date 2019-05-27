package com.craftsmanship.tfm.restapi.controllers;

import java.util.List;

import com.craftsmanship.tfm.exceptions.ItemAlreadyExists;
import com.craftsmanship.tfm.exceptions.ItemDoesNotExist;
import com.craftsmanship.tfm.models.DomainItem;
import com.craftsmanship.tfm.models.ItemOperation;
import com.craftsmanship.tfm.models.OperationType;
import com.craftsmanship.tfm.restapi.kafka.service.ItemOperationService;
import com.craftsmanship.tfm.persistence.ItemPersistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/")
public class ItemRestController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ItemRestController.class);

    // Persistence Handler
    @Autowired
    private ItemPersistence itemPersistence;

    // Kafka Message Bus Service
    private final ItemOperationService itemOperationService;

    public ItemRestController(ItemPersistence itemPersistence, ItemOperationService itemOperationService) {
        this.itemPersistence = itemPersistence;
        this.itemOperationService = itemOperationService;
    }

    @RequestMapping(value = "/items", method = RequestMethod.POST)
    public DomainItem create(@RequestBody DomainItem item) throws ItemAlreadyExists {
        LOGGER.info("Creating item");

        // Create item
        DomainItem itemResponse = itemPersistence.create(item);

        LOGGER.info("REST API itemResponse = " + itemResponse);

        // Send message to Kafka topic
        itemOperationService.sendItemOperation(new ItemOperation(OperationType.CREATED, itemResponse));
        return itemResponse;
    }

    @RequestMapping(value = "/items", method = RequestMethod.GET)
    public List<DomainItem> list() {
        LOGGER.info("List items");
        return itemPersistence.list();
    }

    @RequestMapping(value = "/items/{id}", method = RequestMethod.GET)
    public DomainItem get(@PathVariable Long id) throws ItemDoesNotExist {
        LOGGER.info("Get item with id: " + id);
        return itemPersistence.get(id);
    }

    @RequestMapping(value = "/items/{id}", method = RequestMethod.PUT)
    public DomainItem edit(@PathVariable Long id, @RequestBody DomainItem item) throws ItemDoesNotExist {
        LOGGER.info("Edit item with id: " + id);
        return itemPersistence.update(id, item);
    }

    @RequestMapping(value = "/items/{id}", method = RequestMethod.DELETE)
    public DomainItem delete(@PathVariable Long id) throws ItemDoesNotExist {
        LOGGER.info("Delete item with id: " + id);

        // delete de item
        DomainItem deletedItem = itemPersistence.delete(id);

        // Send message to Kafka topic
        itemOperationService.sendItemOperation(new ItemOperation(OperationType.DELETED, deletedItem));

        return deletedItem;
    }
}