package com.example.demo.auth;

public record LoginRequest(
		String loginId,
		String password) {
}
