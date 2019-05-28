package com.craftsmanship.tfm.restapi.controllers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.craftsmanship.tfm.exceptions.ItemDoesNotExist;
import com.craftsmanship.tfm.exceptions.OrderDoesNotExist;
import com.craftsmanship.tfm.models.ItemOperation;
import com.craftsmanship.tfm.models.DomainItemPurchase;
import com.craftsmanship.tfm.models.OperationType;
import com.craftsmanship.tfm.models.DomainOrder;
import com.craftsmanship.tfm.persistence.OrderPersistence;
import com.craftsmanship.tfm.restapi.kafka.service.ItemOperationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("api/v1/")
public class OrderRestController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ItemRestController.class);

    // Persistence Handler
    @Autowired
    private OrderPersistence orderPersistence;

    // Kafka Message Bus Service
    private final ItemOperationService itemOperationService;

    public OrderRestController(OrderPersistence orderPersistence, ItemOperationService itemOperationService) {
        this.orderPersistence = orderPersistence;
        this.itemOperationService = itemOperationService;
    }

    private void sendItemOperations(OperationType type, List<DomainItemPurchase> itemPurchases) {
        for (DomainItemPurchase itemPurchase : itemPurchases) {
            itemOperationService.sendItemOperation(new ItemOperation(type, itemPurchase.getItem()));
        }
    }

    @RequestMapping(value = "/orders", method = RequestMethod.POST)
    public DomainOrder create(@RequestBody DomainOrder order) {
        LOGGER.info("Creating order");

        try {
            DomainOrder orderResponse = (DomainOrder) orderPersistence.create(order);

            LOGGER.info("REST API orderResponse = " + orderResponse);

            // Send messages to Kafka topic

            this.sendItemOperations(OperationType.CREATED, 
                    (List <DomainItemPurchase>)(List)orderResponse.getItemPurchases());
            return orderResponse;
        } catch (ItemDoesNotExist e) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @RequestMapping(value = "/orders", method = RequestMethod.GET)
    public List<DomainOrder> list() {
        LOGGER.info("List orders");
        return (List<DomainOrder>)(List)orderPersistence.list();
    }

    @RequestMapping(value = "/orders/{id}", method = RequestMethod.GET)
    public DomainOrder get(@PathVariable Long id) {
        LOGGER.info("Get order with id: " + id);

        try {
            return (DomainOrder) orderPersistence.get(id);
        } catch (OrderDoesNotExist e) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @RequestMapping(value = "/orders/{id}", method = RequestMethod.PUT)
    public DomainOrder edit(@PathVariable Long id, @RequestBody DomainOrder order) {
        LOGGER.info("Edit order with id: " + id);

        try {
            return (DomainOrder) orderPersistence.update(id, order);
        } catch (ItemDoesNotExist | OrderDoesNotExist e) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @RequestMapping(value = "/orders/{id}", method = RequestMethod.DELETE)
    public DomainOrder delete(@PathVariable Long id) throws OrderDoesNotExist {
        LOGGER.info("Delete order with id: " + id);

        try {
            return (DomainOrder) orderPersistence.delete(id);
        } catch (OrderDoesNotExist e) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }
}