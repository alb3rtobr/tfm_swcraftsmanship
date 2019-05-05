package com.craftsmanship.tfm.dal.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import com.craftsmanship.tfm.dal.grpc.server.GrpcServer;
import com.craftsmanship.tfm.dal.model.Item;

@RunWith(SpringRunner.class)
@DataJpaTest
public class ItemEntityTest {
	
	@Autowired
	private TestEntityManager entityManager;
	
	//Needed to be able to stop server when running all unit tests in a row
	@Autowired
	private GrpcServer grpcServer;
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	private Item item;
	
	@Before
	public void setUp() throws Exception {
		item = new Item("Item description");
	}

	@After
	public void tearDown() throws Exception {
		grpcServer.stop();
	}
	
	@Test
	public void whenSaveItem_thenReturnItem() {
		Item savedItem = this.entityManager.persistAndFlush(item);
		assertThat(savedItem.getDescription()).isEqualTo("Item description");
	}
	
	@Test
	public void givenSavedItem_whenFindById_thenReturnItem() {
		Item savedItem = this.entityManager.persistAndFlush(item);
		Item foundItem = this.entityManager.find(Item.class, savedItem.getId());
		assertThat(savedItem.getDescription()).isEqualTo(foundItem.getDescription());
	}
}
