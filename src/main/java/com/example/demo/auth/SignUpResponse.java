package com.example.demo.auth;

public record SignUpResponse(
		long memberId,
		String loginId,
		String displayName,
		String email,
		String role) {
}
