# CYH API Frontend

React, Vite, Tailwind CSS로 만든 프론트엔드입니다. 개발 서버는 `/api` 요청을 `http://localhost:8080`의 Spring Boot로 전달합니다.

화면 스타일은 긴 CSS 파일 대신 React 컴포넌트의 Tailwind 유틸리티 클래스로 관리합니다. `src/styles.css`에는 Tailwind import만 있습니다.

현재 화면은 로그인과 회원가입 폼을 전환할 수 있습니다. 회원가입하면 PostgreSQL에 BCrypt 비밀번호 해시가 저장되고, 가입한 아이디로 로그인할 수 있습니다. 로그인 API가 성공하면 브라우저에서 `로그인되었습니다.` 팝업을 표시합니다.

백엔드 API는 `POST /api/auth/signup`과 `POST /api/auth/login`입니다. 로그인 상태 유지용 세션이나 토큰은 아직 구현하지 않았습니다.

## 실행

백엔드를 먼저 실행합니다.

```powershell
.\gradlew.bat bootRun
```

새 터미널에서 프론트엔드를 실행합니다.

```powershell
cd frontend
npm.cmd install
npm.cmd run dev
```

브라우저에서 `http://localhost:5173`에 접속합니다.

## 빌드

```powershell
cd frontend
npm.cmd run build
```

운영 API 주소가 별도 도메인이라면 빌드 전에 `VITE_API_BASE_URL` 환경변수를 설정합니다.
