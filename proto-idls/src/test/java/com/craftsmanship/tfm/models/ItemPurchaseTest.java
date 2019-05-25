package com.craftsmanship.tfm.models;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.junit.Test;

public class ItemPurchaseTest {

    @Test
    public void test_when_two_equals_item_purchases_when_compared_then_return_true() {
        Item item1 = new Item.Builder().withName("Shoe").withPrice(2).withQuantity(100).build();
        Item item2 = new Item.Builder().withName("Shoe").withPrice(2).withQuantity(100).build();

        ItemPurchase itemPurchase1 = new ItemPurchase(item1, 10L);
        ItemPurchase itemPurchase2 = new ItemPurchase(item2, 10L);

        assertThat(itemPurchase1, equalTo(itemPurchase2));
    }

    @Test
    public void test_when_two_item_purchases_with_different_item_when_compared_then_return_false() {
        Item item1 = new Item.Builder().withName("Shoe").withPrice(2).withQuantity(100).build();
        Item item2 = new Item.Builder().withName("Car").withPrice(2).withQuantity(100).build();

        ItemPurchase itemPurchase1 = new ItemPurchase(item1, 10L);
        ItemPurchase itemPurchase2 = new ItemPurchase(item2, 10L);

        assertThat(itemPurchase1, not(equalTo(itemPurchase2)));
    }

    @Test
    public void test_when_two_item_purchases_with_different_quantity_when_compared_then_return_false() {
        Item item1 = new Item.Builder().withName("Shoe").withPrice(2).withQuantity(100).build();
        Item item2 = new Item.Builder().withName("Shoe").withPrice(2).withQuantity(100).build();

        ItemPurchase itemPurchase1 = new ItemPurchase(item1, 10L);
        ItemPurchase itemPurchase2 = new ItemPurchase(item2, 50L);

        assertThat(itemPurchase1, not(equalTo(itemPurchase2)));
    }
}
