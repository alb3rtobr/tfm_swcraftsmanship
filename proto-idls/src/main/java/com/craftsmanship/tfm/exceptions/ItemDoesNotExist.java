package com.craftsmanship.tfm.exceptions;

public class ItemDoesNotExist extends Exception {

    private static final long serialVersionUID = -4557697844139338189L;

    public ItemDoesNotExist(Long id) {
        super("Item with id " + id + " does not exist");
    }
}