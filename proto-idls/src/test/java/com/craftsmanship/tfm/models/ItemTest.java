package com.craftsmanship.tfm.models;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.junit.Test;

public class ItemTest {

    @Test
    public void test_when_two_equals_items_when_compared_then_return_true() {
        Item item1 = new Item.Builder().withId(1L).withName("Car").withPrice(2).withStock(100).build();
        Item item2 = new Item.Builder().withId(1L).withName("Car").withPrice(2).withStock(100).build();

        assertThat(item1, equalTo(item2));
    }

    @Test
    public void test_when_two_items_with_different_id_when_compared_then_return_false() {
        Item item1 = new Item.Builder().withId(1L).withName("Car").withPrice(2).withStock(100).build();
        Item item2 = new Item.Builder().withId(1000L).withName("Car").withPrice(2).withStock(100).build();

        assertThat(item1, not(equalTo(item2)));
    }

    @Test
    public void test_when_two_items_with_different_name_when_compared_then_return_false() {
        Item item1 = new Item.Builder().withId(1L).withName("Car").withPrice(2).withStock(100).build();
        Item item2 = new Item.Builder().withId(1L).withName("Plane").withPrice(2).withStock(100).build();

        assertThat(item1, not(equalTo(item2)));
    }

    @Test
    public void test_when_two_items_with_different_price_when_compared_then_return_false() {
        Item item1 = new Item.Builder().withId(1L).withName("Car").withPrice(2).withStock(100).build();
        Item item2 = new Item.Builder().withId(1L).withName("Car").withPrice(10).withStock(100).build();

        assertThat(item1, not(equalTo(item2)));
    }

    @Test
    public void test_when_two_items_with_different_stock_when_compared_then_return_false() {
        Item item1 = new Item.Builder().withId(1L).withName("Car").withPrice(2).withStock(100).build();
        Item item2 = new Item.Builder().withId(1L).withName("Car").withPrice(2).withStock(500).build();

        assertThat(item1, not(equalTo(item2)));
    }
}
