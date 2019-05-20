package com.craftsmanship.tfm.dal.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.craftsmanship.tfm.dal.grpc.server.GrpcServer;
import com.craftsmanship.tfm.dal.model.Item;

@RunWith(SpringRunner.class)
@DataJpaTest
public class ItemRepositoryTest {

	@Autowired 
	private ItemRepository itemRepository;

	//Needed to be able to stop server when running all unit tests in a row
	@Autowired
	private GrpcServer grpcServer;
	
	private Item item;
	
	@Before
	public void setUp() throws Exception {
		item = new Item("Item for repository description");
	}

	@After
	public void tearDown() throws Exception {
		grpcServer.stop();
	}

	@Test
	public void givenItemRepository_whenSaveItem_thenReturnItem() {
		Item savedItem = itemRepository.save(item);
		assertThat(savedItem.getName()).isEqualTo("Item for repository description");
	}

	@Test
	public void givenItemRepository_whenSaveItem_thenFoundById() {
		Item savedItem = itemRepository.save(item);
		assertThat(itemRepository.findById(item.getId())).isEqualTo(Optional.of(savedItem));
	}
}
