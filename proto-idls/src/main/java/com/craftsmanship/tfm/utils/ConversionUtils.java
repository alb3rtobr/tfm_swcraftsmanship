package com.craftsmanship.tfm.utils;

import com.craftsmanship.tfm.models.Item;

import com.craftsmanship.tfm.idls.v2.ItemPersistence.GrpcItem;

public class ConversionUtils {
    public static Item getItemFromGrpcItem(GrpcItem grpcItem) {
        return new Item.Builder()
            .withId(grpcItem.getId())
            .withName(grpcItem.getName())
            .withPrice(grpcItem.getPrice())
            .withQuantity(grpcItem.getQuantity())
            .build();
    }

    public static GrpcItem getGrpcItemFromItem(Item item) {
        return GrpcItem.newBuilder()
            .setId(item.getId())
            .setName(item.getName())
            .setPrice(item.getPrice())
            .setQuantity(item.getQuantity())
            .build();
    }
}