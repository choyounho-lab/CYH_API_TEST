package com.example.demo.auth;

import java.time.OffsetDateTime;

public record MemberAccount(
		Long memberId,
		String loginId,
		String passwordHash,
		String displayName,
		String email,
		String role,
		String status,
		Integer failedLoginCount,
		OffsetDateTime lockedUntil) {

	boolean canLoginAt(OffsetDateTime now) {
		return "ACTIVE".equals(status)
				&& (lockedUntil == null || !lockedUntil.isAfter(now));
	}
}
