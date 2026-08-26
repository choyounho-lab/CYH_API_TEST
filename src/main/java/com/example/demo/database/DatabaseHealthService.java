package com.example.demo.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.stereotype.Service;

@Service
public class DatabaseHealthService {

	private final DataSource dataSource;
	private final DatabaseProbeMapper databaseProbeMapper;

	public DatabaseHealthService(DataSource dataSource, DatabaseProbeMapper databaseProbeMapper) {
		this.dataSource = dataSource;
		this.databaseProbeMapper = databaseProbeMapper;
	}

	public DatabaseConnectionInfo checkConnection() throws SQLException {
		int queryResult = databaseProbeMapper.ping();
		if (queryResult != 1) {
			throw new IllegalStateException("MyBatis database check returned an unexpected result.");
		}

		try (Connection connection = dataSource.getConnection()) {
			DatabaseMetaData metadata = connection.getMetaData();
			return new DatabaseConnectionInfo(
					"UP",
					"UP",
					metadata.getDatabaseProductName(),
					metadata.getDatabaseProductVersion(),
					metadata.getURL(),
					metadata.getUserName());
		}
	}
}
