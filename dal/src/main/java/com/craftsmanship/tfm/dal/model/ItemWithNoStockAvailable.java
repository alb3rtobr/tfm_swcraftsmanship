package com.craftsmanship.tfm.dal.model;

public class ItemWithNoStockAvailable extends Exception {

    private static final long serialVersionUID = -4557697844139338189L;

    public ItemWithNoStockAvailable(EntityItem item, int desiredStock) {
        super("No stock available for item " + item.getId() + " (available=" + item.getStock() + ", desired=" + desiredStock + ")");
    }
}