package com.example.demo.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = AuthController.class)
public class AuthExceptionHandler {

	@ExceptionHandler(InvalidLoginRequestException.class)
	ResponseEntity<LoginErrorResponse> handleInvalidRequest(InvalidLoginRequestException exception) {
		return ResponseEntity.badRequest().body(new LoginErrorResponse(exception.getMessage()));
	}

	@ExceptionHandler(UnauthorizedLoginException.class)
	ResponseEntity<LoginErrorResponse> handleUnauthorized(UnauthorizedLoginException exception) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(new LoginErrorResponse(exception.getMessage()));
	}

	@ExceptionHandler(DuplicateMemberException.class)
	ResponseEntity<LoginErrorResponse> handleDuplicateMember(DuplicateMemberException exception) {
		return ResponseEntity.status(HttpStatus.CONFLICT)
				.body(new LoginErrorResponse(exception.getMessage()));
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	ResponseEntity<LoginErrorResponse> handleUnreadableBody() {
		return ResponseEntity.badRequest().body(new LoginErrorResponse("로그인 요청 형식이 올바르지 않습니다."));
	}
}
