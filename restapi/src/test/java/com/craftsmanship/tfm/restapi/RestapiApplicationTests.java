package com.craftsmanship.tfm.restapi;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext
@ActiveProfiles("dev")
@TestPropertySource(properties = { "kafka.bootstrap-servers=localhost" })
public class RestapiApplicationTests {

	@Test
	public void contextLoads() {
	}

}
