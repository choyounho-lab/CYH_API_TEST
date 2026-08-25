# 작업 기록

## 프로젝트

- 저장소: `https://github.com/choyounho-lab/CYH_API_TEST`
- 브랜치: `main`
- 프로젝트명: `cyh-api-test`
- 기술 환경: Java 17, Spring Boot, Gradle

## 완료한 작업

- 기존 버스 관련 컨트롤러, 서비스, DTO, CSV Runner를 모두 제거했다.
- TAGO, GBIS, 네이버, 카카오, Selenium 관련 설정과 의존성을 제거했다.
- 기본 Spring Boot 애플리케이션과 기본 컨텍스트 테스트만 남겼다.
- 로컬 VS Code 설정은 Git에서 제외했다.
- `./gradlew test`가 통과하는 것을 확인했다.
- 정리한 프로젝트를 GitHub `main` 브랜치에 푸시했다.

## 현재 상태

- 아직 데이터베이스는 연결하지 않았다.
- 아직 도메인, 테이블, 엔티티, Repository, Service, Controller는 만들지 않았다.
- 새로운 API의 구체적인 주제도 아직 정하지 않았다.

## 다음 작업 시작점

PostgreSQL을 기준으로 아래 순서대로 진행한다.

1. 로컬 PostgreSQL 설치 및 실행 상태 확인
2. 개발용 데이터베이스와 사용자 생성
3. DBeaver에서 PostgreSQL 연결 확인
4. Spring Boot에 PostgreSQL 드라이버와 Spring Data JPA 추가
5. 접속 정보는 환경변수로 관리하고 비밀번호는 Git에 올리지 않기
6. Spring Boot 데이터베이스 연결 테스트
7. 만들 API의 주제를 정한 후 첫 테이블과 CRUD API 구현
8. 테스트 통과 후 커밋 및 GitHub 푸시

## 재개 방법

새 대화에서 "전에 하던 작업 기억해?"라고 요청하면 이 파일과 Git 상태를 먼저 확인하고,
`다음 작업 시작점`부터 이어서 진행한다.
