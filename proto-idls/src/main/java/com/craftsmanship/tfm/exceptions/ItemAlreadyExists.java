package com.craftsmanship.tfm.exceptions;

public class ItemAlreadyExists extends Exception {

    private static final long serialVersionUID = -4557697844139338189L;

    public ItemAlreadyExists(String name) {
        super("Item with name " + name + " already exists");
    }
}