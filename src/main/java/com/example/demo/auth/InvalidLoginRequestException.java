package com.example.demo.auth;

public class InvalidLoginRequestException extends RuntimeException {

	public InvalidLoginRequestException(String message) {
		super(message);
	}
}
