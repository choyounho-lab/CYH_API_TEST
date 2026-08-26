package com.example.demo.auth;

public record LoginResponse(
		long memberId,
		String loginId,
		String displayName,
		String role) {
}
