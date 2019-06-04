package com.craftsmanship.tfm.dal.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.metrics.CompositeMeterRegistryAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import com.craftsmanship.tfm.dal.grpc.server.GrpcServer;
import com.craftsmanship.tfm.dal.model.EntityItem;

@RunWith(SpringRunner.class)
@DataJpaTest
@Import({MetricsAutoConfiguration.class, CompositeMeterRegistryAutoConfiguration.class})
public class ItemEntityTest {

    @Autowired
    private TestEntityManager entityManager;

    //Needed to be able to stop server when running all unit tests in a row
    @Autowired
    private GrpcServer grpcServer;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private EntityItem item;

    @Before
    public void setUp() throws Exception {
        item = new EntityItem("Item description", 199, 25);
    }

    @After
    public void tearDown() throws Exception {
        grpcServer.stop();
    }

    @Test
    public void whenSaveItem_thenReturnItem() {
        EntityItem savedItem = this.entityManager.persistAndFlush(item);
        assertThat(savedItem.getName()).isEqualTo("Item description");
        assertThat(savedItem.getPrice()).isEqualTo(199);
        assertThat(savedItem.getStock()).isEqualTo(25);
    }

    @Test
    public void givenSavedItem_whenFindById_thenReturnItem() {
        EntityItem savedItem = this.entityManager.persistAndFlush(item);
        EntityItem foundItem = this.entityManager.find(EntityItem.class, savedItem.getId());
        assertThat(savedItem.getName()).isEqualTo(foundItem.getName());
        assertThat(savedItem.getPrice()).isEqualTo(199);
        assertThat(savedItem.getStock()).isEqualTo(25);
    }
}
