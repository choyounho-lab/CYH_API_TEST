# 로그인 회원 테이블 안내

## 생성 위치

- 실제 데이터베이스: PostgreSQL `cyh_api_test.public.member_account`
- 생성 SQL: `src/main/resources/db/migration/V1__create_member_account.sql`
- 적용 방식: Spring Boot 시작 시 Flyway가 아직 실행되지 않은 마이그레이션을 자동 적용

## 컬럼

| 컬럼 | 타입 | 역할 |
|---|---|---|
| `member_id` | `BIGINT` | 자동 증가 회원 식별자, 기본키 |
| `login_id` | `VARCHAR(50)` | 로그인 아이디, 영문 대소문자를 무시하고 중복 금지 |
| `password_hash` | `VARCHAR(255)` | 비밀번호 원문이 아닌 Argon2id/bcrypt 해시 |
| `display_name` | `VARCHAR(100)` | 화면에 표시할 회원 이름 |
| `email` | `VARCHAR(254)` | 이메일, 영문 대소문자를 무시하고 중복 금지 |
| `role` | `VARCHAR(20)` | `USER` 또는 `ADMIN` |
| `status` | `VARCHAR(20)` | `ACTIVE`, `LOCKED`, `DISABLED`, `WITHDRAWN` |
| `failed_login_count` | `INTEGER` | 연속 로그인 실패 횟수 |
| `locked_until` | `TIMESTAMPTZ` | 계정 잠금 만료 시각 |
| `last_login_at` | `TIMESTAMPTZ` | 마지막 로그인 성공 시각 |
| `email_verified_at` | `TIMESTAMPTZ` | 이메일 인증 완료 시각 |
| `password_changed_at` | `TIMESTAMPTZ` | 마지막 비밀번호 변경 시각 |
| `created_at` | `TIMESTAMPTZ` | 계정 생성 시각 |
| `updated_at` | `TIMESTAMPTZ` | 계정 최종 수정 시각 |

## 보안 원칙

- 비밀번호 원문은 데이터베이스에 저장하지 않습니다.
- `password_hash`에는 Spring Security에서 생성한 Argon2id 또는 bcrypt 결과만 저장합니다.
- 단순 SHA-256 같은 빠른 해시는 비밀번호 저장에 사용하지 않습니다.
- 로그인 실패 횟수와 잠금 만료 시각을 이용해 반복 대입 공격을 제한합니다.

## 현재 MyBatis에서 사용하는 주요 SQL

`MemberAccountMapper.xml`에서 아래 작업을 구현했습니다.

- `login_id`로 활성 계정 조회
- 회원 가입 INSERT
- 로그인 성공 시 실패 횟수 초기화와 마지막 로그인 시각 갱신
- 로그인 실패 시 실패 횟수 증가

계정 잠금과 잠금 해제 정책은 이후 구현할 항목입니다.

로그인 ID 조회는 테이블의 대소문자 무시 고유 인덱스를 활용하도록 다음 조건을 사용합니다.

```sql
WHERE lower(login_id) = lower(#{loginId})
```
