package com.example.demo.database;

import java.sql.SQLException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/database")
public class DatabaseHealthController {

	private final DatabaseHealthService databaseHealthService;

	public DatabaseHealthController(DatabaseHealthService databaseHealthService) {
		this.databaseHealthService = databaseHealthService;
	}

	@GetMapping("/health")
	public DatabaseConnectionInfo health() throws SQLException {
		return databaseHealthService.checkConnection();
	}
}
