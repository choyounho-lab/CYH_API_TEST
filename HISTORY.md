# 프로젝트 작업 히스토리

## 2026-08-26

### 1. PostgreSQL 연결

- 로컬 PostgreSQL 18.6 서비스 실행 상태를 확인했습니다.
- 프로젝트 전용 `cyh_api_test` 데이터베이스를 생성했습니다.
- DB 접속정보를 환경변수와 Git에서 제외되는 `.env`로 분리했습니다.
- PostgreSQL JDBC Driver와 HikariCP 연결을 확인했습니다.
- Docker로도 같은 DB 환경을 만들 수 있도록 `compose.yaml`을 추가했습니다.
- 설치형 PostgreSQL 초기화를 위한 SQL과 PowerShell 스크립트를 추가했습니다.

### 2. Spring Data JPA 적용

- Spring Data JPA 의존성을 추가했습니다.
- PostgreSQL과 JPA/Hibernate 연결을 구성했습니다.
- `open-in-view`를 비활성화하고 DB 시간대를 설정했습니다.
- 이후 도메인별 Entity와 Repository를 추가할 수 있는 기반을 만들었습니다.

### 3. MyBatis 적용

- JPA를 유지하면서 MyBatis Spring Boot Starter를 함께 추가했습니다.
- Java Mapper 인터페이스와 XML Mapper 구조를 구성했습니다.
- `DatabaseProbeMapper.ping()`과 `SELECT 1` SQL을 연결했습니다.
- JPA와 MyBatis가 동일한 `DataSource`를 공유하도록 구성했습니다.

### 4. DB 헬스 API 구현

- `GET /api/database/health` 점검 API를 만들었습니다.
- Controller → Service → MyBatis Mapper → PostgreSQL 흐름을 구성했습니다.
- PostgreSQL 제품명, 버전, URL, 사용자와 MyBatis 실행 상태를 JSON으로 반환합니다.
- 실제 PostgreSQL에서 `status: UP`, `mybatisStatus: UP`을 확인했습니다.

### 5. 테스트와 보안

- 테스트 전용 H2 인메모리 DB를 PostgreSQL 호환 모드로 구성했습니다.
- Spring Boot, JPA, MyBatis 동시 실행 테스트를 추가했습니다.
- Gradle 테스트가 `BUILD SUCCESSFUL`로 통과했습니다.
- 실제 비밀번호가 들어 있는 `.env`와 빌드 캐시를 Git에서 제외했습니다.

### 6. 백엔드 문서화와 첫 Git 반영

- DB 설치, 환경변수, 실행법을 `README.md`에 정리했습니다.
- 파일별 역할과 권장 패키지 구조를 `PROJECT_FILE_GUIDE.md`에 정리했습니다.
- 백엔드 기반 작업을 `f64b0c5` 커밋으로 GitHub `main` 브랜치에 푸시했습니다.

### 7. React 프론트엔드 적용

- 저장소 내부 `frontend/` 폴더에 React 19와 Vite 8 프로젝트를 구성했습니다.
- 백엔드와 분리된 프론트엔드 개발·빌드 환경을 만들었습니다.
- Vite 개발 서버의 `/api` 요청을 Spring Boot 8080 포트로 전달하도록 프록시를 설정했습니다.
- PostgreSQL, JPA, MyBatis 연결 상태를 표시하는 반응형 대시보드를 구현했습니다.
- 로딩, 정상 연결, 연결 실패 상태와 수동 새로고침 기능을 구현했습니다.
- 30초마다 백엔드 연결 상태를 자동으로 다시 확인하도록 구현했습니다.

### 8. 프론트엔드 검증

- npm 의존성 설치와 보안 감사를 완료했으며 발견된 취약점은 0건입니다.
- `npm.cmd run build` 프로덕션 빌드가 성공했습니다.
- 프론트엔드 `http://127.0.0.1:5173` 응답 코드 200을 확인했습니다.
- 프론트엔드 프록시를 통한 `/api/database/health` 호출이 실제 PostgreSQL 18.6 응답을 반환하는 것을 확인했습니다.

### 9. Tailwind CSS로 스타일 구조 개선

- 길게 작성했던 일반 CSS를 Tailwind CSS 유틸리티 방식으로 변경했습니다.
- Tailwind 공식 Vite 플러그인을 추가했습니다.
- `styles.css`는 Tailwind를 불러오는 한 줄만 남겼습니다.
- 기존 반응형 대시보드 디자인을 React 컴포넌트의 Tailwind 클래스로 이전했습니다.
- 변경 후 프로덕션 빌드와 실제 백엔드 API 연결을 다시 확인했습니다.

### 10. 로그인 회원 테이블 생성

- Spring Boot 4 전용 Flyway Starter와 PostgreSQL Flyway 모듈을 추가했습니다.
- `V1__create_member_account.sql` 마이그레이션을 작성했습니다.
- 실제 PostgreSQL `cyh_api_test.public.member_account` 테이블을 생성했습니다.
- 로그인 ID, 비밀번호 해시, 표시 이름, 이메일, 권한과 계정 상태 컬럼을 추가했습니다.
- 로그인 실패 횟수, 잠금 만료 시각, 마지막 로그인 시각과 이메일 인증 시각을 추가했습니다.
- 생성·수정·비밀번호 변경 시각을 `TIMESTAMPTZ`로 저장하도록 구성했습니다.
- 로그인 ID와 이메일이 영문 대소문자만 다르게 중복되지 않도록 고유 인덱스를 추가했습니다.
- 권한, 계정 상태, 실패 횟수와 빈 문자열을 검사하는 DB 제약조건을 추가했습니다.
- Flyway 이력 `version 1 / create member account / success`를 실제 DB에서 확인했습니다.
- 테이블은 생성만 했으며 초기 회원 데이터는 넣지 않았습니다.

### 11. React 로그인·회원가입 폼 구현

- 기존 PostgreSQL 상태 대시보드 화면을 제거했습니다.
- 로그인 폼과 회원가입 폼이 전환되는 화면으로 변경했습니다.
- 제공받은 뉴모피즘 디자인을 React와 Tailwind 유틸리티 클래스로 적용했습니다.
- 로그인 버튼을 누르면 `POST /api/auth/login`으로 JSON 요청을 보내도록 구현했습니다.
- API가 성공 응답을 반환하면 `로그인되었습니다.` 브라우저 팝업을 표시합니다.
- 회원가입 성공 시 로그인 화면으로 돌아오며 가입한 아이디를 자동으로 채웁니다.
- 잘못된 계정, 중복 회원과 서버 오류 메시지를 폼 안에 표시합니다.
- 숨겨진 회원가입 폼이 로그인 입력을 가로채지 않도록 비활성 패널의 포인터 입력을 차단했습니다.
- 프론트엔드 프로덕션 빌드가 성공하는 것을 확인했습니다.

### 12. MyBatis 로그인·회원가입 API 구현

- `POST /api/auth/signup` 회원가입 API를 구현했습니다.
- 회원 아이디, 이름, 이메일과 비밀번호 입력 검증을 추가했습니다.
- 비밀번호 원문 대신 BCrypt 강도 12의 해시를 저장합니다.
- 아이디와 이메일 중복을 확인하고 `409 Conflict`를 반환합니다.
- `POST /api/auth/login` 로그인 API를 구현했습니다.
- MyBatis XML에서 회원 조회, 가입 INSERT, 성공·실패 기록 UPDATE를 실행합니다.
- 로그인 성공 시 실패 횟수를 초기화하고 마지막 로그인 시각을 갱신합니다.
- 로그인 실패 시 트랜잭션이 롤백되지 않도록 실패 횟수를 저장합니다.
- 단위 테스트와 H2 기반 MyBatis 통합 테스트를 추가했습니다.
- 실제 PostgreSQL에서 가입 `200`, 로그인 `200`, 중복 가입 `409`를 확인했습니다.
- 검증에 사용한 임시 회원은 테스트 후 삭제했습니다.

### 13. 실제 화면과 DBeaver 최종 확인

- 화면에서 직접 가입한 회원 데이터가 PostgreSQL에 저장되는 것을 확인했습니다.
- DBeaver가 `postgres.public`에 연결되어 있어 회원 데이터가 보이지 않던 원인을 확인했습니다.
- 애플리케이션 데이터는 `cyh_api_test.public.member_account`에서 조회해야 한다는 내용을 작업 기록에 추가했습니다.
- 로그인 화면의 입력값이 숨겨진 회원가입 폼으로 들어가던 문제를 수정했습니다.
- 수정 후 로그인 입력, 회원가입 전환, 프론트 빌드와 서버 응답을 다시 확인했습니다.
- 현재 구현 상태와 다음 작업 순서를 `WORKLOG.md`에 최신화했습니다.

## 현재 가능한 기능

- React 프론트엔드 실행과 화면 표시
- Tailwind CSS 기반 반응형 화면
- Spring Boot REST API 실행
- PostgreSQL 실제 연결
- JPA와 MyBatis 동시 사용
- MyBatis XML SQL 실행
- 프론트엔드에서 백엔드 API 호출
- 로그인·회원가입 화면과 API
- BCrypt 비밀번호 해시 저장과 비교
- Flyway 기반 DB 스키마 자동 마이그레이션
- 로그인 회원 테이블
- 백엔드 자동 테스트와 프론트엔드 프로덕션 빌드

## 아직 구현하지 않은 기능

- 서비스 도메인 테이블과 실제 CRUD API
- 로그인 유지용 세션 또는 인증 토큰
- 로그인 사용자 권한 검사
- 계정 자동 잠금과 잠금 해제
- 이메일 인증과 비밀번호 재설정
- 여러 프론트엔드 페이지와 라우팅
- 파일 업로드
- 운영 서버와 데이터베이스 배포
- CI/CD 자동 배포
