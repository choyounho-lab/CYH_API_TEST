package com.example.demo.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTests {

	@Mock
	private MemberAccountMapper memberAccountMapper;

	private AuthService authService;
	private BCryptPasswordEncoder passwordEncoder;

	@BeforeEach
	void setUp() {
		passwordEncoder = new BCryptPasswordEncoder(4);
		authService = new AuthService(memberAccountMapper, passwordEncoder);
	}

	@Test
	void returnsMemberInformationWhenCredentialsAreCorrect() {
		MemberAccount account = activeAccount(passwordEncoder.encode("correct-password"));
		when(memberAccountMapper.findByLoginId("tester")).thenReturn(account);

		LoginResponse response = authService.login(new LoginRequest(" tester ", "correct-password"));

		assertThat(response.memberId()).isEqualTo(1L);
		assertThat(response.loginId()).isEqualTo("tester");
		assertThat(response.displayName()).isEqualTo("테스터");
		assertThat(response.role()).isEqualTo("USER");
		verify(memberAccountMapper).recordLoginSuccess(1L);
		verify(memberAccountMapper, never()).recordLoginFailure(1L);
	}

	@Test
	void recordsFailureAndRejectsAnIncorrectPassword() {
		MemberAccount account = activeAccount(passwordEncoder.encode("correct-password"));
		when(memberAccountMapper.findByLoginId("tester")).thenReturn(account);

		assertThatThrownBy(() -> authService.login(new LoginRequest("tester", "wrong-password")))
				.isInstanceOf(UnauthorizedLoginException.class)
				.hasMessage("아이디 또는 비밀번호가 올바르지 않습니다.");

		verify(memberAccountMapper).recordLoginFailure(1L);
		verify(memberAccountMapper, never()).recordLoginSuccess(1L);
	}

	@Test
	void rejectsAnUnknownLoginIdWithoutUpdatingRows() {
		when(memberAccountMapper.findByLoginId("unknown")).thenReturn(null);

		assertThatThrownBy(() -> authService.login(new LoginRequest("unknown", "password")))
				.isInstanceOf(UnauthorizedLoginException.class);

		verify(memberAccountMapper, never()).recordLoginFailure(1L);
		verify(memberAccountMapper, never()).recordLoginSuccess(1L);
	}

	@Test
	void rejectsBlankCredentialsBeforeQueryingTheDatabase() {
		assertThatThrownBy(() -> authService.login(new LoginRequest(" ", "")))
				.isInstanceOf(InvalidLoginRequestException.class)
				.hasMessage("아이디와 비밀번호를 입력해주세요.");

		verify(memberAccountMapper, never()).findByLoginId(" ");
	}

	private MemberAccount activeAccount(String passwordHash) {
		return new MemberAccount(
				1L,
				"tester",
				passwordHash,
				"테스터",
				"tester@example.com",
				"USER",
				"ACTIVE",
				0,
				null);
	}
}
