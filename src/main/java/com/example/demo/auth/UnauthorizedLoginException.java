package com.example.demo.auth;

public class UnauthorizedLoginException extends RuntimeException {

	public UnauthorizedLoginException() {
		super("아이디 또는 비밀번호가 올바르지 않습니다.");
	}
}
