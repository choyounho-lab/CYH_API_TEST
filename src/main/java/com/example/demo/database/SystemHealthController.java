package com.example.demo.database;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class SystemHealthController {

	@GetMapping("/health")
	public HealthResponse health() {
		return new HealthResponse("UP");
	}

	record HealthResponse(String status) {
	}
}
