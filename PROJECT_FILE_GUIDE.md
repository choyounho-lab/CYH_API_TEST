# 프로젝트 파일 역할 안내서

이 문서는 현재 프로젝트에 있는 주요 파일이 무슨 역할을 하는지 설명합니다.

## 1. 전체 실행 흐름

```text
클라이언트 요청
    ↓
DatabaseHealthController.java   요청 주소를 받음
    ↓
DatabaseHealthService.java      실행할 작업을 결정함
    ↓
DatabaseProbeMapper.java        사용할 MyBatis 메서드를 선언함
    ↓
DatabaseProbeMapper.xml         실제 SQL을 실행함
    ↓
PostgreSQL cyh_api_test
```

JPA 기능을 만들면 아래 흐름도 함께 사용할 수 있습니다.

```text
Controller → Service → JPA Repository → PostgreSQL
Controller → Service → MyBatis Mapper → PostgreSQL
```

간단한 객체 저장과 CRUD는 JPA, 복잡한 조회와 직접 작성한 SQL은 MyBatis를 사용하면 됩니다.

## 2. Java 소스 파일

### `src/main/java/com/example/demo/DemoApplication.java`

- Spring Boot 애플리케이션의 시작 파일입니다.
- `main()` 메서드를 실행하면 내장 Tomcat과 Spring이 시작됩니다.
- `@SpringBootApplication`이 현재 패키지 아래의 Controller, Service, Mapper 등을 자동으로 찾습니다.

### `src/main/java/com/example/demo/database/DatabaseHealthController.java`

- HTTP 요청을 받는 Controller입니다.
- `GET /api/database/health` 주소를 담당합니다.
- 직접 SQL을 실행하지 않고 `DatabaseHealthService`에 작업을 요청합니다.

### `src/main/java/com/example/demo/database/DatabaseHealthService.java`

- Controller와 데이터 접근 계층 사이에서 작업을 처리하는 Service입니다.
- Spring의 `DataSource`와 MyBatis의 `DatabaseProbeMapper`를 생성자 주입으로 받습니다.
- MyBatis 쿼리 결과와 PostgreSQL 연결 정보를 조합해서 Controller에 반환합니다.

### `src/main/java/com/example/demo/database/DatabaseProbeMapper.java`

- MyBatis Mapper 인터페이스입니다.
- Java에서는 `ping()`이라는 메서드만 선언합니다.
- `@Mapper`가 있기 때문에 Spring이 구현 객체를 자동으로 만들어 주입합니다.
- 실제 `SELECT 1` SQL은 같은 이름의 XML Mapper에 작성되어 있습니다.

### `src/main/java/com/example/demo/database/DatabaseConnectionInfo.java`

- DB 연결 결과를 담는 응답 DTO입니다.
- `status`, `mybatisStatus`, PostgreSQL 버전, 접속 URL, 사용자 이름을 JSON으로 반환할 때 사용합니다.
- Java `record`이므로 값을 보관하는 용도로 사용하는 간단한 객체입니다.

## 3. SQL과 MyBatis 파일

### `src/main/resources/mapper/DatabaseProbeMapper.xml`

- MyBatis가 실행할 실제 SQL을 작성하는 파일입니다.
- `namespace`는 연결할 Java Mapper의 전체 패키지 이름과 같아야 합니다.
- `<select id="ping">`의 `id`는 `DatabaseProbeMapper`의 `ping()` 메서드 이름과 같아야 합니다.

현재 연결 관계는 다음과 같습니다.

```text
namespace="com.example.demo.database.DatabaseProbeMapper"
                                      ↓
<select id="ping"> SELECT 1 </select>
                 ↓
DatabaseProbeMapper.ping()
```

### `database/init-postgres.sql`

- 프로젝트용 PostgreSQL 사용자와 데이터베이스를 최초 생성하는 SQL입니다.
- 새 PC나 새 PostgreSQL 환경을 구성할 때 사용하는 초기화 파일입니다.
- 애플리케이션 실행 중 반복적으로 사용하는 SQL은 아닙니다.

## 4. 설정 파일

### `src/main/resources/application.properties`

- 실제 애플리케이션의 공통 설정 파일입니다.
- PostgreSQL 접속 주소, 사용자, 비밀번호 환경변수를 읽습니다.
- JPA 설정과 MyBatis XML Mapper 위치를 설정합니다.

중요한 설정은 다음과 같습니다.

```properties
spring.datasource.*       # PostgreSQL 연결
spring.jpa.*              # JPA/Hibernate 설정
mybatis.mapper-locations  # MyBatis XML 위치
mybatis.configuration.*   # MyBatis 동작 설정
```

### `.env`

- 현재 PC에서 사용할 실제 DB 주소와 계정 정보를 보관합니다.
- 비밀번호가 들어 있으므로 `.gitignore`에 등록되어 GitHub에 올라가지 않습니다.
- 다른 사람에게 전달하거나 저장소에 커밋하면 안 됩니다.

### `.env.example`

- `.env` 작성 방법을 보여주는 예제 파일입니다.
- 실제 비밀번호 대신 예시 값만 들어갑니다.
- 새 개발자는 이 파일을 `.env`로 복사한 후 자기 환경에 맞게 수정합니다.

### `.gitignore`

- Git이 추적하지 않을 파일과 폴더를 지정합니다.
- `.env`, 빌드 결과, Gradle 캐시처럼 저장소에 올리면 안 되는 항목이 들어 있습니다.

### `compose.yaml`

- Docker로 PostgreSQL을 실행할 때 사용하는 설정입니다.
- 현재 PC처럼 PostgreSQL이 직접 설치된 환경에서는 반드시 사용할 필요가 없습니다.

## 5. Gradle 빌드 파일

### `build.gradle`

- 프로젝트에서 사용할 라이브러리와 빌드 방법을 정의합니다.
- Spring Web, JPA, PostgreSQL Driver, MyBatis, 테스트 라이브러리가 등록되어 있습니다.
- 새로운 라이브러리가 필요할 때 `dependencies`에 추가합니다.

### `settings.gradle`

- Gradle 프로젝트 이름과 플러그인 저장소를 설정합니다.
- 현재 프로젝트 이름은 `cyh-api-test`입니다.

### `gradlew` / `gradlew.bat`

- Gradle을 PC에 별도로 설치하지 않아도 빌드할 수 있게 해주는 실행 파일입니다.
- Windows에서는 `gradlew.bat`, macOS/Linux에서는 `gradlew`를 사용합니다.

```powershell
.\gradlew.bat test
.\gradlew.bat bootRun
```

### `gradle/wrapper/gradle-wrapper.properties`

- 프로젝트가 사용할 Gradle 버전과 다운로드 주소를 지정합니다.

### `gradle/wrapper/gradle-wrapper.jar`

- 지정된 Gradle을 내려받고 실행하는 Wrapper 프로그램입니다.
- 직접 수정하지 않습니다.

## 6. 테스트 파일

### `src/test/java/com/example/demo/DemoApplicationTests.java`

- Spring Boot, JPA, MyBatis가 함께 정상 실행되는지 자동으로 검사합니다.
- MyBatis `ping()` 쿼리 결과가 정상인지 확인합니다.

### `src/test/resources/application.properties`

- 테스트할 때만 사용하는 DB 설정입니다.
- 실제 PostgreSQL 대신 빠른 인메모리 H2 DB를 PostgreSQL 호환 모드로 사용합니다.
- 실제 PostgreSQL 비밀번호 없이도 자동 테스트를 실행할 수 있습니다.

## 7. 실행과 문서 파일

### `scripts/setup-postgres.ps1`

- Windows에 설치된 `psql.exe`를 찾아 `database/init-postgres.sql`을 실행합니다.
- PostgreSQL을 처음 구성할 때만 사용합니다.

### `README.md`

- 프로젝트 설치, DB 연결, 실행 방법을 설명하는 기본 사용 설명서입니다.

### `WORKLOG.md`

- 이전에 진행한 작업과 다음 작업 내용을 기록하는 문서입니다.

### `PROJECT_FILE_GUIDE.md`

- 지금 보고 있는 파일입니다.
- 프로젝트 파일들의 역할과 연결 관계를 설명합니다.

## 8. 새로운 기능을 만들 때 권장 파일 구성

예를 들어 회원 기능을 만든다면 다음과 같이 구성할 수 있습니다.

```text
src/main/java/com/example/demo/user/
├─ User.java                 JPA Entity
├─ UserRepository.java       JPA 기본 CRUD
├─ UserMapper.java           MyBatis 메서드 선언
├─ UserService.java          회원 관련 작업 처리
├─ UserController.java       회원 API 주소 처리
└─ UserRequest.java          요청 데이터 DTO

src/main/resources/mapper/
└─ UserMapper.xml            직접 작성하는 회원 SQL
```

Service에서는 필요한 방식에 따라 둘 다 주입할 수 있습니다.

```java
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }
}
```
