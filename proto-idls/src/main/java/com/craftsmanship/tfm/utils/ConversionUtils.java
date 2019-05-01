package com.craftsmanship.tfm.utils;

import com.craftsmanship.tfm.models.Item;

import v1.ItemPersistence.GrpcItem;

public class ConversionUtils {
    public static Item getItemFromGrpcItem(GrpcItem grpcItem) {
        return new Item.Builder()
            .withId(grpcItem.getId())
            .withDescription(grpcItem.getDescription())
            .build();
    }

    public static GrpcItem getGrpcItemFromItem(Item item) {
        return GrpcItem.newBuilder()
            .setId(item.getId())
            .setDescription(item.getDescription())
            .build();
    }
}