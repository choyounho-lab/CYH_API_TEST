package com.example.demo.auth;

public record SignUpRequest(
		String loginId,
		String displayName,
		String email,
		String password) {
}
