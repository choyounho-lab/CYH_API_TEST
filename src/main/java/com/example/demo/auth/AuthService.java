package com.example.demo.auth;

import java.time.OffsetDateTime;
import java.util.Locale;
import java.util.regex.Pattern;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

	private static final int MAX_LOGIN_ID_LENGTH = 50;
	private static final int MAX_PASSWORD_LENGTH = 200;
	private static final int MAX_DISPLAY_NAME_LENGTH = 100;
	private static final int MAX_EMAIL_LENGTH = 254;
	private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

	private final MemberAccountMapper memberAccountMapper;
	private final PasswordEncoder passwordEncoder;

	public AuthService(MemberAccountMapper memberAccountMapper, PasswordEncoder passwordEncoder) {
		this.memberAccountMapper = memberAccountMapper;
		this.passwordEncoder = passwordEncoder;
	}

	@Transactional(noRollbackFor = UnauthorizedLoginException.class)
	public LoginResponse login(LoginRequest request) {
		validateLogin(request);

		String normalizedLoginId = request.loginId().trim();
		MemberAccount account = memberAccountMapper.findByLoginId(normalizedLoginId);

		if (account == null || !account.canLoginAt(OffsetDateTime.now())) {
			throw new UnauthorizedLoginException();
		}

		if (!passwordEncoder.matches(request.password(), account.passwordHash())) {
			memberAccountMapper.recordLoginFailure(account.memberId());
			throw new UnauthorizedLoginException();
		}

		memberAccountMapper.recordLoginSuccess(account.memberId());
		return new LoginResponse(
				account.memberId(),
				account.loginId(),
				account.displayName(),
				account.role());
	}

	@Transactional
	public SignUpResponse signUp(SignUpRequest request) {
		validateSignUp(request);

		String loginId = request.loginId().trim();
		String displayName = request.displayName().trim();
		String email = request.email().trim().toLowerCase(Locale.ROOT);

		if (memberAccountMapper.countByLoginId(loginId) > 0) {
			throw new DuplicateMemberException("이미 사용 중인 아이디입니다.");
		}
		if (memberAccountMapper.countByEmail(email) > 0) {
			throw new DuplicateMemberException("이미 사용 중인 이메일입니다.");
		}

		String passwordHash = passwordEncoder.encode(request.password());
		try {
			memberAccountMapper.insertMember(loginId, passwordHash, displayName, email);
		} catch (DuplicateKeyException exception) {
			throw new DuplicateMemberException("이미 사용 중인 아이디 또는 이메일입니다.");
		}

		MemberAccount account = memberAccountMapper.findByLoginId(loginId);
		if (account == null) {
			throw new IllegalStateException("회원가입 후 계정을 조회할 수 없습니다.");
		}

		return new SignUpResponse(
				account.memberId(),
				account.loginId(),
				account.displayName(),
				account.email(),
				account.role());
	}

	private void validateLogin(LoginRequest request) {
		if (request == null || request.loginId() == null || request.loginId().isBlank()
				|| request.password() == null || request.password().isBlank()) {
			throw new InvalidLoginRequestException("아이디와 비밀번호를 입력해주세요.");
		}

		if (request.loginId().trim().length() > MAX_LOGIN_ID_LENGTH
				|| request.password().length() > MAX_PASSWORD_LENGTH) {
			throw new InvalidLoginRequestException("입력값이 허용된 길이를 초과했습니다.");
		}
	}

	private void validateSignUp(SignUpRequest request) {
		if (request == null
				|| request.loginId() == null || request.loginId().isBlank()
				|| request.displayName() == null || request.displayName().isBlank()
				|| request.email() == null || request.email().isBlank()
				|| request.password() == null || request.password().isBlank()) {
			throw new InvalidLoginRequestException("회원가입 정보를 모두 입력해주세요.");
		}

		String loginId = request.loginId().trim();
		String displayName = request.displayName().trim();
		String email = request.email().trim();

		if (loginId.length() < 4 || loginId.length() > MAX_LOGIN_ID_LENGTH) {
			throw new InvalidLoginRequestException("아이디는 4자 이상 50자 이하로 입력해주세요.");
		}
		if (displayName.length() > MAX_DISPLAY_NAME_LENGTH) {
			throw new InvalidLoginRequestException("이름은 100자 이하로 입력해주세요.");
		}
		if (email.length() > MAX_EMAIL_LENGTH || !EMAIL_PATTERN.matcher(email).matches()) {
			throw new InvalidLoginRequestException("올바른 이메일을 입력해주세요.");
		}
		if (request.password().length() < 8 || request.password().length() > MAX_PASSWORD_LENGTH) {
			throw new InvalidLoginRequestException("비밀번호는 8자 이상 200자 이하로 입력해주세요.");
		}
	}
}
