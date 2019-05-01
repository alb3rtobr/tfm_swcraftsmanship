package com.craftsmanship.tfm.dal.model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ItemTest {
	
	@Before
	public void setUp() throws Exception {	
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test_when_description_is_set_then_it_is_get_ok() {
		Item testedItem = new Item();
		testedItem.setDescription("tested item description");
		assert(testedItem.getDescription().equals("tested item description"));
	}

	@Test
	public void test_when_is_built_with_description_then_it_is_get_ok() {
		Item testedItem = new Item("tested item description");
		assert(testedItem.getDescription().equals("tested item description"));
	}

}
