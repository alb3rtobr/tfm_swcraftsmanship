package com.craftsmanship.tfm.exceptions;

public class OrderDoesNotExist extends Exception {

    private static final long serialVersionUID = -4557697844139338189L;

    public OrderDoesNotExist(Long id) {
        super("Order with id " + id + " does not exist");
    }
}