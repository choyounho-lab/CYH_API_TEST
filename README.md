# CYH API Test

React, Vite, Tailwind CSS 프론트엔드와 Java 17, Spring Boot, Gradle 기반 백엔드로 구성된 프로젝트입니다. 하나의 PostgreSQL 연결을 Spring Data JPA와 MyBatis가 함께 사용합니다.

## 연결 구조

```text
HTTP GET /api/database/health
        ↓
DatabaseHealthController
        ↓
DatabaseHealthService
        ↓
DataSource
   ├─ Hibernate (Spring Data JPA)
   └─ SqlSession (MyBatis XML Mapper)
        ↓
PostgreSQL
```

향후 기능을 추가할 때는 아래 패키지 구조를 권장합니다.

```text
com.example.demo
├─ database/                 # DB 연결 확인 기능
└─ {domain}/
   ├─ {Domain}Controller.java
   ├─ {Domain}Service.java
   ├─ {Domain}Repository.java
   ├─ {Domain}.java          # JPA @Entity
   └─ {Domain}Mapper.java    # MyBatis Mapper

resources
└─ mapper/
   └─ {Domain}Mapper.xml     # 직접 작성하는 SQL
```

단순 CRUD와 객체 중심 작업은 JPA Repository를 사용하고, 복잡한 조회·통계·튜닝이 필요한 SQL은 MyBatis Mapper를 사용할 수 있습니다. 두 방식은 같은 트랜잭션과 `DataSource`를 공유합니다.

## 데이터베이스 마이그레이션

Spring Boot 시작 시 Flyway가 `src/main/resources/db/migration`의 미적용 SQL을 순서대로 실행합니다.

현재 마이그레이션:

```text
V1__create_member_account.sql → 로그인 회원 테이블 생성
```

로그인 테이블의 상세 컬럼은 [database/LOGIN_TABLE.md](database/LOGIN_TABLE.md)에서 확인할 수 있습니다. 이미 적용된 마이그레이션 파일은 수정하지 않고 이후 변경은 `V2__...sql` 파일로 추가합니다.

## 설치된 PostgreSQL에 최초 연결

Windows에 PostgreSQL이 설치되어 있다면 아래 스크립트를 한 번 실행합니다.

```powershell
.\scripts\setup-postgres.ps1
```

스크립트는 다음 순서로 암호를 요청합니다.

1. 기존 `postgres` 관리자 암호
2. 새 `cyh_app` 암호: `.env`의 `DB_PASSWORD`와 같은 값을 입력

이 스크립트는 `cyh_app` 로그인 계정과 `cyh_api_test` 데이터베이스를 생성합니다. 이미 존재하면 그대로 재사용합니다.

## Docker로 PostgreSQL 시작(대안)

Docker Desktop이 설치되어 있고 실행 중이어야 합니다.

```powershell
Copy-Item .env.example .env
# .env의 비밀번호를 로컬 개발용 값으로 변경
docker compose up -d postgres
docker compose ps
```

현재 작업 폴더에는 Git에서 제외되는 로컬용 `.env`가 이미 생성되어 있습니다. 운영 환경에서는 `.env` 파일을 배포하지 말고 배포 환경의 보안 환경변수를 사용하세요.

## 애플리케이션 실행과 확인

```powershell
.\gradlew.bat bootRun
```

다른 터미널에서 연결 상태를 확인합니다.

```powershell
Invoke-RestMethod http://localhost:8080/api/database/health
```

정상 연결 시 `status`가 `UP`, `product`가 `PostgreSQL`로 반환됩니다.

## 프론트엔드 실행

백엔드를 먼저 실행한 상태에서 새 터미널을 엽니다.

```powershell
cd frontend
npm.cmd install
npm.cmd run dev
```

브라우저에서 `http://localhost:5173`에 접속합니다. Vite 개발 서버는 `/api` 요청을 자동으로 `http://localhost:8080`의 Spring Boot로 전달합니다.

프론트엔드 프로덕션 빌드는 다음 명령으로 확인합니다.

```powershell
cd frontend
npm.cmd run build
```

## 로그인과 회원가입

프론트 화면에서 아이디, 이름, 이메일과 비밀번호로 가입할 수 있습니다. 비밀번호는 BCrypt 해시로 변환되어 PostgreSQL의 `member_account.password_hash`에 저장됩니다.

- `POST /api/auth/signup`: 회원가입
- `POST /api/auth/login`: 아이디와 비밀번호 확인
- 로그인 성공 시 프론트에서 `로그인되었습니다.` 팝업 표시

코드 실행 순서와 파일 역할은 [LOGIN_API.md](LOGIN_API.md)에서 확인할 수 있습니다. 현재는 자격 증명 확인까지만 구현되어 있으며 로그인 상태를 유지하는 세션 또는 토큰은 아직 없습니다.

## Cloudflare Pages 정적 배포

이 저장소의 `wrangler.toml`은 Vite 빌드 결과인 `frontend/dist`를 Pages 정적 자산으로 지정합니다. Cloudflare Pages에서는 다음 값을 사용합니다.

```text
Root directory: /
Build command: npm run build
Deploy command: npx wrangler pages deploy frontend/dist --project-name=cyh-api-test
```

`frontend` 소스 폴더가 아니라 반드시 `frontend/dist`가 배포되어야 합니다. 배포된 HTML이 `/src/main.jsx`를 직접 가리키면 소스 폴더가 잘못 배포된 상태입니다.

## 환경변수

| 이름 | 용도 | 로컬 예시 |
|---|---|---|
| `DB_URL` | JDBC 접속 주소 | `jdbc:postgresql://localhost:5432/cyh_api_test` |
| `DB_USERNAME` | 애플리케이션 DB 사용자 | `cyh_app` |
| `DB_PASSWORD` | 애플리케이션 DB 비밀번호 | 로컬에서 직접 지정 |
| `POSTGRES_DB` | Docker가 최초 생성할 DB | `cyh_api_test` |
| `POSTGRES_USER` | Docker가 최초 생성할 사용자 | `cyh_app` |
| `POSTGRES_PASSWORD` | Docker PostgreSQL 비밀번호 | 로컬에서 직접 지정 |

## 테스트

테스트는 외부 PostgreSQL 없이도 반복 실행할 수 있도록 PostgreSQL 호환 모드의 인메모리 H2를 사용합니다.

```powershell
.\gradlew.bat test
```

작업 내역은 [HISTORY.md](HISTORY.md), 파일별 역할은 [PROJECT_FILE_GUIDE.md](PROJECT_FILE_GUIDE.md)에서 확인할 수 있습니다.
