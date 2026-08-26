# 로그인·회원가입 API 분석 안내서

## 전체 흐름

```text
React App.jsx
    ↓ POST /api/auth/signup 또는 /api/auth/login
AuthController
    ↓
AuthService
    ├─ 입력값 검증
    ├─ BCrypt 해시 생성 또는 비밀번호 비교
    ↓
MemberAccountMapper.java
    ↓
MemberAccountMapper.xml
    ↓ SELECT / INSERT / UPDATE
PostgreSQL member_account
```

## 회원가입

`POST /api/auth/signup`

```json
{
  "loginId": "sample-user",
  "displayName": "샘플 사용자",
  "email": "sample@example.com",
  "password": "sample-password"
}
```

처리 순서는 다음과 같습니다.

1. `AuthController`가 JSON을 `SignUpRequest`로 받습니다.
2. `AuthService`가 필수값, 길이와 이메일 형식을 검사합니다.
3. MyBatis로 아이디와 이메일 중복을 확인합니다.
4. `PasswordEncoder`가 비밀번호를 BCrypt 해시로 변환합니다.
5. MyBatis가 `member_account`에 회원을 INSERT합니다.
6. 비밀번호를 제외한 `SignUpResponse`를 반환합니다.

## 로그인

`POST /api/auth/login`

```json
{
  "loginId": "sample-user",
  "password": "sample-password"
}
```

처리 순서는 다음과 같습니다.

1. MyBatis가 대소문자를 무시하고 `login_id`로 회원을 조회합니다.
2. `AuthService`가 계정 상태와 잠금 시각을 확인합니다.
3. `PasswordEncoder.matches()`가 입력 비밀번호와 DB 해시를 비교합니다.
4. 성공하면 실패 횟수를 0으로 만들고 `last_login_at`을 갱신합니다.
5. 실패하면 `failed_login_count`를 증가시키고 `401`을 반환합니다.

비밀번호 비교는 SQL이 아니라 Java Service에서 실행합니다. 비밀번호 원문과 `password_hash`는 API 응답에 포함하지 않습니다.

## 응답 상태

| 상태 | 의미 |
|---|---|
| `200 OK` | 회원가입 또는 로그인 성공 |
| `400 Bad Request` | 필수값, 길이 또는 이메일 형식 오류 |
| `401 Unauthorized` | 아이디 또는 비밀번호 불일치, 사용 불가능한 계정 |
| `409 Conflict` | 이미 사용 중인 아이디 또는 이메일 |

## 주요 파일

| 파일 | 역할 |
|---|---|
| `AuthController.java` | API 주소와 요청·응답 연결 |
| `AuthService.java` | 검증, BCrypt와 트랜잭션 처리 |
| `MemberAccountMapper.java` | MyBatis 메서드 선언 |
| `MemberAccountMapper.xml` | 실제 회원 SQL |
| `PasswordConfiguration.java` | `PasswordEncoder` Bean 등록 |
| `AuthExceptionHandler.java` | 예외를 400·401·409 JSON으로 변환 |
| `frontend/src/api/authApi.js` | React에서 백엔드 API 호출 |
| `frontend/src/App.jsx` | 로그인·회원가입 화면과 팝업 |

## 아직 없는 기능

현재 로그인은 아이디와 비밀번호가 맞는지 확인하는 단계까지입니다. 새로고침 후에도 로그인 상태를 유지하거나 보호된 API를 구분하려면 다음 단계로 세션 또는 토큰과 권한 검사가 필요합니다.
