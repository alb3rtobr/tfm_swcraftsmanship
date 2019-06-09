package com.craftsmanship.tfm.exceptions;

public class ItemWithNoStockAvailable extends Exception {

    private static final long serialVersionUID = -4557697844139338189L;

    public ItemWithNoStockAvailable(String message) {
        super(message);
    }

    public ItemWithNoStockAvailable(Long id, int stock, int quantity) {
        super("No stock available for item " + id + " (available=" + stock + ", desired=" + quantity + ")");
    }
}