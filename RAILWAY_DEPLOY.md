# Railway 백엔드 배포 안내

이 프로젝트는 Dockerfile과 `railway.json`이 준비되어 있어 Railway에서 Spring Boot 백엔드를 배포할 수 있습니다.

## 1. Railway 프로젝트 생성

1. Railway에 로그인합니다.
2. `New Project`를 선택합니다.
3. `Deploy from GitHub repo`에서 `CYH_API_TEST` 저장소를 선택합니다.
4. 저장소의 `main` 브랜치를 선택합니다.
5. 저장소 루트의 `Dockerfile`을 사용하도록 배포합니다.

## 2. PostgreSQL 추가

같은 Railway 프로젝트에서 PostgreSQL 서비스를 추가합니다. PostgreSQL 서비스가 제공하는 연결 정보를 백엔드 서비스 환경변수에 등록합니다.

```text
DB_URL=jdbc:postgresql://<host>:<port>/<database>
DB_USERNAME=<database-user>
DB_PASSWORD=<database-password>
APP_CORS_ALLOWED_ORIGINS=https://cyh-api-test.pages.dev
```

실제 값은 Railway Variables에만 입력하고 GitHub나 채팅에 기록하지 않습니다.

## 3. 포트와 상태 확인

- Spring Boot는 Railway가 제공하는 `PORT` 환경변수를 자동으로 사용합니다.
- Railway health check 경로는 `/api/health`입니다.
- 배포 후 `https://<railway-domain>/api/health`가 `{"status":"UP"}`를 반환해야 합니다.

## 4. Cloudflare Pages 연결

Railway에서 발급한 백엔드 주소를 Cloudflare Pages 빌드 환경변수에 등록합니다.

```text
VITE_API_BASE_URL=https://<railway-domain>
```

그 뒤 Pages를 다시 배포하면 프론트의 `/api/auth/login`, `/api/auth/signup` 요청이 Railway 백엔드로 전송됩니다.

## 5. 보안 확인

- PostgreSQL은 인터넷에 직접 공개하지 않습니다.
- `DB_PASSWORD`는 Railway Variables에만 저장합니다.
- `APP_CORS_ALLOWED_ORIGINS`에는 실제 Pages 도메인만 입력합니다.
- HTTPS 주소만 사용합니다.
- Cloudflare WAF와 API 요청 제한을 추가합니다.

현재 API는 자격 증명 확인까지 구현되어 있으며, 로그인 상태 유지용 세션/JWT와 로그아웃은 다음 단계입니다.
