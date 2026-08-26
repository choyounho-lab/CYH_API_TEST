package com.example.demo;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.database.DatabaseConnectionInfo;
import com.example.demo.database.DatabaseHealthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DemoApplicationTests {

	@Autowired
	private DatabaseHealthService databaseHealthService;

	@Test
	void contextLoadsAndConnectsToTestDatabase() throws Exception {
		DatabaseConnectionInfo connection = databaseHealthService.checkConnection();

		assertThat(connection.status()).isEqualTo("UP");
		assertThat(connection.mybatisStatus()).isEqualTo("UP");
		assertThat(connection.product()).isEqualTo("H2");
	}

}
