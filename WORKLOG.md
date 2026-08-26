# 작업 기록 및 재개 지점

이 파일은 다음 작업을 시작할 때 가장 먼저 확인하는 최신 상태 요약입니다. 상세 변경 순서는 `HISTORY.md`, 로그인 코드 분석은 `LOGIN_API.md`, 파일별 역할은 `PROJECT_FILE_GUIDE.md`를 확인합니다.

## 프로젝트

- 저장소: `https://github.com/choyounho-lab/CYH_API_TEST`
- 브랜치: `main`
- 프로젝트명: `cyh-api-test`
- 백엔드: Java 17, Spring Boot 4, Gradle
- 데이터베이스: 로컬 PostgreSQL 18.6
- 프론트엔드: React 19, Vite 8, Tailwind CSS 4
- 데이터 접근: Spring Data JPA와 MyBatis 동시 사용

## Git 상태

- 백엔드 DB 기반 작업은 `f64b0c5` 커밋으로 GitHub `main`에 푸시했습니다.
- 로그인 회원 테이블, 로그인·회원가입 API와 현재 프론트 변경은 아직 커밋·푸시하지 않았습니다.
- 로컬 `.env`와 실제 DB 비밀번호는 Git에서 제외합니다.

## 실행 주소

- React 개발 서버: `http://localhost:5173`
- Spring Boot API: `http://localhost:8080`
- DB 점검 API: `GET http://localhost:8080/api/database/health`
- 회원가입 API: `POST http://localhost:8080/api/auth/signup`
- 로그인 API: `POST http://localhost:8080/api/auth/login`
- Jenkins는 Windows에 설치하고 로그인을 확인했지만 CI/CD Job과 배포는 아직 구성하지 않았습니다.

개발 서버는 작업 세션이나 PC를 종료하면 함께 꺼질 수 있으므로 재개할 때 실행 상태를 다시 확인합니다.

## 데이터베이스

- 애플리케이션 연결 대상은 `cyh_api_test.public`입니다.
- 회원 테이블은 `cyh_api_test.public.member_account`입니다.
- Flyway의 `V1__create_member_account.sql`이 테이블과 인덱스를 생성합니다.
- 회원 아이디와 이메일은 영문 대소문자를 무시하고 중복을 허용하지 않습니다.
- 비밀번호 원문은 저장하지 않고 BCrypt 해시만 `password_hash`에 저장합니다.
- 실제 화면 회원가입으로 생성된 회원 데이터가 PostgreSQL에 저장되는 것을 확인했습니다.

DBeaver에서 데이터가 보이지 않으면 연결 설정의 Database가 `postgres`가 아닌 `cyh_api_test`인지 확인합니다. 두 데이터베이스에 모두 `public` 스키마가 있으므로 스키마 이름만 봐서는 구분할 수 없습니다.

확인 SQL:

```sql
SELECT current_database(), current_schema();

SELECT member_id,
       login_id,
       display_name,
       email,
       role,
       status,
       created_at
FROM public.member_account
ORDER BY member_id DESC;
```

## 백엔드 완료 기능

- PostgreSQL, JPA/Hibernate와 MyBatis가 같은 `DataSource`를 사용합니다.
- `GET /api/database/health`에서 PostgreSQL과 MyBatis 연결 상태를 확인합니다.
- Flyway가 미적용 DB 마이그레이션을 시작 시 자동 실행합니다.
- `POST /api/auth/signup`에서 아이디, 이름, 이메일과 비밀번호를 검증합니다.
- MyBatis가 아이디·이메일 중복을 확인하고 회원을 INSERT합니다.
- BCrypt 강도 12로 비밀번호 해시를 생성합니다.
- `POST /api/auth/login`에서 MyBatis로 회원을 조회하고 BCrypt 해시를 비교합니다.
- 로그인 성공 시 실패 횟수를 초기화하고 마지막 로그인 시각을 기록합니다.
- 로그인 실패 시 실패 횟수를 증가시키고 `401`을 반환합니다.
- 잘못된 요청은 `400`, 중복 가입은 `409` JSON 오류로 반환합니다.
- JPA 의존성과 설정은 제거하지 않고 유지했습니다.

## 프론트엔드 완료 기능

- 제공받은 뉴모피즘 로그인·회원가입 화면을 React와 Tailwind로 구현했습니다.
- 주황색 overlay의 `Sign Up`과 `Sign In` 버튼으로 두 폼이 0.5초 동안 전환됩니다.
- 일반 CSS를 길게 작성하지 않고 `frontend/src/styles.css`에는 Tailwind import 한 줄만 유지합니다.
- 회원가입 성공 시 알림을 표시하고 가입 아이디를 로그인 폼에 자동 입력합니다.
- 로그인 성공 시 `로그인되었습니다.` 브라우저 팝업을 표시합니다.
- 인증 실패, 잘못된 입력과 중복 가입 오류를 폼 내부에 표시합니다.
- 숨겨진 회원가입 폼이 로그인 입력을 가로채던 문제를 `pointer-events-none`으로 해결했습니다.
- 소셜 로그인 버튼은 디자인만 있으며 실제 외부 인증 기능은 없습니다.

## 검증 완료

- 전체 Gradle 자동 테스트가 `BUILD SUCCESSFUL`로 통과했습니다.
- H2에서 실제 MyBatis XML을 이용한 회원가입·로그인 통합 테스트가 통과했습니다.
- 프론트엔드 `npm.cmd run build`가 성공했습니다.
- 실제 PostgreSQL에서 회원가입 `200`, 로그인 `200`, 중복 가입 `409`를 확인했습니다.
- 프론트 Vite 프록시를 통한 인증 API 호출을 확인했습니다.
- 검증 목적으로 만든 임시 회원은 삭제했으며 사용자가 화면에서 가입한 회원은 유지했습니다.

## 아직 구현하지 않은 기능

- 로그인 상태 유지용 서버 세션 또는 JWT
- 로그인 사용자 권한에 따른 API 접근 제어
- 로그아웃
- 계정 자동 잠금·해제 정책
- 이메일 인증과 비밀번호 재설정
- 서비스 도메인 CRUD와 추가 프론트 페이지
- 운영 서버와 PostgreSQL 외부 배포
- Jenkins Pipeline과 GitHub 연동 자동 배포
- 소셜 로그인

## 다음 권장 작업

1. 현재 로그인·회원가입 변경을 검토합니다.
2. Git 변경사항과 비밀값 포함 여부를 다시 검사합니다.
3. 검토가 끝나면 커밋하고 GitHub `main`에 푸시합니다.
4. 그다음 HTTP 전용 쿠키 기반 세션과 로그아웃을 구현합니다.
5. 인증이 필요한 테스트 API를 하나 만들어 권한 검사를 확인합니다.
6. Jenkins Pipeline에서 백엔드 테스트와 프론트 빌드를 자동 실행합니다.

## 재개 방법

새 대화에서 작업을 재개할 때 아래 순서로 확인합니다.

1. `WORKLOG.md`
2. `HISTORY.md`
3. `git status`
4. PostgreSQL, 백엔드 8080과 프론트 5173 실행 상태

기록된 비밀번호는 없으므로 로컬 실행 시 Git에서 제외된 `.env`를 사용합니다.
