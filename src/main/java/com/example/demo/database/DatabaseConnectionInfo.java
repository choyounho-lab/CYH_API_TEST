package com.example.demo.database;

public record DatabaseConnectionInfo(
		String status,
		String mybatisStatus,
		String product,
		String version,
		String url,
		String username) {
}
