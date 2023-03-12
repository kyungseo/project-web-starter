# KYUNGSEO.PoC - Project Web Starter Template

## 개요
**Project Web Starter**는 중소규모의 개발프로젝트 실무에서 즉시 활용할 수 있는 시작 템플릿을 구현하는 것을 목적으로 합니다.

*※ 개인적 용도로 시작한 작업의 부족한 결과물이지만 Public Repo.에 공개를 결정하였습니다. 대신 개선과 정비가 필요한 부분들에 대해서는 초기 버전 공개 후에도 계속해서 버그 및 이슈에 대한 해결, 기능 보완 및 추가 등의 활동을 지속할 예정입니다.*

### 기본 컨셉
* **Structure 및 Module의 구성**
  * 실제 프로젝트에서 확장 가능하도록 Directory, Package 등의 기본 Structure를 **Simple**하게 구성 *- 처음 계획 시에는 Framework, Security, UI 등의 역할과 기능에 따라 각각의 Module로 분리하여 구성하려 하였으나, 최종적으로는 All-in-one 형태의 Module(Project) 하나로 구성함*
  * 개발프로젝트 수행 시 필수적이고 최소한의 기능 요소들로 Module을 구성
* **Implementation 고려 사항**
  * 현재의 개발 트렌드를 반영하여 `Spring Boot`, `JPA`(+ `MyBatis`) 등에 기반한 구현
  * 최근 순수 JSP는 잘 사용하지 않는 추세를 고려하여 Template Engine(`Thymeleaf`)을 활용하여 구현
  * Backend 및 Frontend 개발을 위한 기본 샘플 구현

## 적용 기술

### 기본 Spec.

| Technology | Spec |
| --- | --- |
| Java | java-11-openjdk-11.0.2 |
| Maven | apache-maven-3.8.7 |
| Database | H2 Database 2.1.214 (추가적으로 MariaDB 10.10.2에서 테스트) |

**Java**의 경우 11 버전을 사용했지만 1.8+ 버전도 사용 가능합니다. **Database**의 경우 기본적으로 H2 Database를 적용하고 있지만 선호하는 Database로 변경해도 무관합니다.

### Backend Dependencies

| Technology | Dependency |
| --- | --- |
| Spring Boot | Spring Boot 2.7.8 |
| Spring Framework | Spring Web 5.3.25, Spring Security 5.7.6, Spring Data JPA 2.7.7, etc. |
| Persistency Framework | Hibernate 5.6.14, QueryDSL 5.0.0, MyBatis 3.5.11 |
| Template engine | Thymeleaf 3.0.15 |
| Others | Lombok 1.18.24, Springdoc-openapi(API 문서 자동화) 1.6.14 |


**Spring Boot**는 우선 2.x 버전을 채택하였으며, 추후 3.x로 업그레이드할 계획입니다. **Persistence**는 인증/인가 등 기본 탑재되는 기능의 `jpa`로, 실제 프로젝트 업무시스템 구현은 `mybatis`로 구현할 수 있도록 하였습니다. *- 물론 어느쪽이든 선호하는 방식으로 구현 가능합니다.*

### Frontend Dependencies

| Technology | Dependency |
| --- | --- |
| JavaScript Libraries | jQuery 3.5.1, cryptojs-3.1.2, cryptojs-3.1.2, etc. |
|  | KSM JS(KyungSeo's Mini JavaScript Library) |
| CSS Library | Bootstrap 3.4.1 |

jQuery는 오래된 라이브러리이지만 여전히 가치 있고, 실무에도 많이 활용되고 있습니다. **jQuery와 Bootstrap의 조합**만으로도 모던한 UI 구현이 충분합니다.

※ `KSM`은 오래 전(2016 년)에 작성했던 JS 모듈인데, 일관된 Ajax 호출 및 표준화된 전후 처리를 위한 간략한 코드를 제공하는 용도입니다. 추가적으로 Dialog 및 Utility 등 몇몇 기능을 포함하고 있습니다. *- 참고로 저는 전문적인 Frontend 개발자가 아닙니다.*

## 주요 기능 및 특징

### Architecture 및 Mechanism

#### Backend
* **Layered Architecture**: Spring MVC, JPA(또는 MyBatis) 기반인 만큼 자연스럽게 Presentation, Service, Persistence 등의 레이어를 분리하여 개발할 수 있는 구조입니다.
* **Persistence 레이어**:
  *  **Multi-Datasource**: 실제 프로젝트는 단일 Database로 구성되지 않는 경우가 대부분입니다. 다양한 DB, MQ 등의 데이터소스들을 조합할 수 있도록 Multi Datasource에 대한 예시를 구조적으로 적용하였습니다. *- 예를 들어 `**.repository.ds1` 패키지와 `**.repository.ds2` 패키지 하위의 `JpaRepository`는 각각 DS1, DS2 데이터소스가 적용됩니다.*
  *  **Transaction Handling**: JPA(ORM Repository)와 MyBatis(SQL-Mapper)의 구현체들이 동일한 `TransactionManager`로 관리되도록 구성하였습니다.
* **Technical Mechanism**: 다음의 기본적인 필수 매커니즘에 대한 기반 구조를 적용하였습니다.
  * **Secure Properties**: `*.properties`에 대한 Utility 지원, 추가적으로 민감한 정보에 대해서는 `Jasypt`를 사용한 암호화 처리 가능
  * **Message Handling**: `i18n` 기반의 Message 처리
  * **Exception Handling**: `@Controller` 및 `@RestController`에 대한 Global Exception 처리
  * **Log Handling**: `@Aspect`, `HandlerInterceptor` 등을 활용한 기본 로깅, `log4jdbc`를 활용한 SQL 로깅 처리
  * **Validation Check**: `ConstraintValidator`, `@Constraint`, `javax.validation.constraints.*` 등을 활용한 기본 유효성 체크, 그외 CustomValidationService 예시
  * **Double Submit 방지**: Request 요청 시 고유한 ID를 생성함으로써 `ViolationInterceptor`을 활용한 이중요청 방지 처리

#### Frontend
* **다양한 Layout 구성**: `Thymeleaf Layout Dialect`를 활용하여 화면별로 상이한 페이지 레이아웃을 적용할 수 있습니다.
* **동적 UI 구성**: 로그인한 사용자의 역할 및 권한에 따라 메뉴를 동적으로 구성할 수 있으며, 특정 Link, Button 등의 요소들에 대한 접근을 제어할 수 있습니다.
* **Validation Check**: `validate.js`를 사용하여 API 요청 직전에 손쉽게 데이터에 대한 유효성 체크를 수행할 수 있습니다.
* **Ajax 호출 표준**: UI에서 API 호출 시 `Option`을 통해 Loading 효과를 주어 화면을 Blocking하거나 `callback` 수행 전,후에 공통의 기능을 처리할 수 있습니다.

### Authetication 및 Authorization

#### 사용자 등록 및 메일
* 간단한 폼(Form)을 사용하여 실제 사용자 등록
* 비밀번호 검증 및 강도 체크 지원
* 검증 Token을 사용하여 사용자 이메일의 유효성 확인: 사용자가 Confirm할 때까지 비활성화 상태로 유지
* Google OTP를 사용한 2FA 지원
* Remember Me 지원
* 비밀번호 분실 시 Token을 사용한 재설정 기능 지원

#### 인증 및 인가
* 사용자의 로그인 및 로그아웃 지원
* 중복 로그인 방지 지원
* 로그인 시도가 10회 이상 지속되면 자동 Blocking 지원
* 역할 기반의 권한 부여

#### Session 및 Token 관리
* Session 유효 시간 경과 시 Invalidate 처리
* 사용자가 로그인 후 아무 작업도 하지 않은 채 일정 시간 경과 시 자동 로그아웃 처리
* 사용자 등록, 비밀번호 재설정 등의 프로세스에서 발급되는 Token은 24시간만 유효
* Spring Task를 사용하여 무효 토큰 자동 삭제 처리

### 사용자 관리(Sample)
* **사용자 CRUD**: 사용자에 대한 CRUD 구현 예시
* **사용자 목록 Paging**: `Pageable` 및 `PagingRequestDTO`를 활용한 Paging 구현 및 목록 화면에 페이징 네이게이션 제공
* **다양한 예시**: Ajax 호출, Server-side/Client-side의 Validation 처리, Message 처리, Exception 처리, Data Formatting, Dialog 등을 포함한 다양한 샘플 코드 포함

## 'Project Web Starter' 셋업 절차

### Prerequisites
`project-web-starter`를 clone하기 전에 다음과 같은 기본적인 환경이 구성되어야 합니다.

* Java는 Default!
* Git, Maven은 필수!

### Clone 'Project Web Starter'

#### git clone
`git` 명령을 사용하여 project-web-starter 프로젝트를 `clone` 합니다.

```bash
$ git clone https://github.com/kyungseo/project-web-starter.git
$ cd project-web-starter
```

#### ※ Project Structure
참고로 `project-web-starter` 프로젝트의 구조는 다음과 같습니다.

```
project-web-starter/src/main
|
├─┬ java/kyungseo/poc/simple/web
│ ├── appcore                   → Core 모듈
│ ├── security                  → Spring Security & JWT 모듈
│ ├─┬ site                      → 사이트 Root (프로젝트의 타겟 시스템의 이름이라 생각하면...)
│ │ ├── admin                   → 사이트 관리자 모듈
│ │ ├── common                  → 사이트 공통 모듈
│ │ ├── sample                  → 사이트 샘플 모듈
│ │ └── user                    → 사이트 일반 사용자 모듈
| |
│ └── SimpleWebApplication.java → Spring Boot Application
|
├─┬ resources
│ ├── messages                  → message.properties 경로
│ ├── mybatis                   → mybatis mapper.xml 경로
│ ├── static                    → css, font, images, js, vendor 등의 Static Resource 경로
│ └─┬ templates                 → Thymeleaf Root
│   ├── layout                  → Layout 및 Fragments 경로
│   └─┬ view                    → 사이트 화면(뷰) Root
|     ├── admin                 → 관리자 화면
|     ├── common                → 공통 화면
│     ├── sample                → 샘플 화면
│     └── user                  → 사용자 화면
|
└── pom.xml                     → Maven pom

```

※ Eclipse의 Project Explorer

![image](/docs/project-structure.png)


### Database 구성

Database 구성은 Option 사항입니다. 기본적으로 `project-web-starter` 프로젝트는 내장된 H2 Database를 사용하도록 구성되어 있으므로 별도의 구성이 필요없습니다. 하지만 만약 H2 대신 MySQL을 사용하고자 한다면 다음과 같이 Database와 User를 생성해야 합니다.

```sql
CREATE DATABASE mydatabase DEFAULT CHARACTER SET utf8;
CREATE USER 'developer'@'%' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON mydatabase.* TO developer@'%';
FLUSH PRIVILEGES;
```

그리고 생성한 Database와 User 정보를 `project-web-starter` 프로젝트 내의 `src/main/resources/application.properties`에도 반영해야 합니다.

````properties
# [ DataSource 1 ]
spring.datasource.driver-class-name=org.mariadb.jdbc.Driver
spring.datasource.jdbc-url=jdbc:mariadb://localhost:3306/mydatabase
spring.datasource.ds2.username=developer
spring.datasource.ds2.password=password
````

### Mail 구성

`project-web-starter`를 구동하게 되면, 새로운 사용자를 등록하거나 비밀번호를 재설정하는 작업을 수행할 때 실제로 email을 전송합니다. 따라서 `src/main/resources/application.properties`에서 관련 설정을 수정해야합니다.

````properties
# [ JavaMail ]
support.email=developer@company.com

spring.mail.host=smtp.google.com
spring.mail.port=465
spring.mail.protocol=smtps
spring.mail.username=deveoper
spring.mail.password=password
````

### Build & Run

이제 모든 준비가 끝났습니다.

사용 중인 IDE에 Clone 또는 Zip 파일로 직접 다운로드 한 프로젝트를 Import하여 바로 실행하면 됩니다.

또한 IDE를 사용하지 않고 `project-web-starter` 프로젝트 디렉토리로 이동하여 다음과 같이 `mvn` 명령을 사용하면 프로젝트를 `build`할 수 있습니다.

```bash
mvn clean install

````

그리고 Spring Boot 모듈을 실행하려면 프로젝트 디렉토리에서 `mvn spring-boot:run` 명령을 실행하면 됩니다.

```bash
./mvnw spring-boot:run   # UNIX/Linux 기반 OS인 경우
mvnw.cmd spring-boot:run # Windows 기반 OS인 경우

```

### Browser 접속

마지막으로 Chrome 이나 Edge 등의 Browser를 열고 다음 URL들에 접속해보도록 합니다.

* [Project Web Starter Template (simple web)](http://localhost:8080)
  * Admin 유저: admin@company.com / password
  * 일반 유저: user001@company.com / password
* [KYUNGSEO.PoC - API (Swagger UI)](http://localhost:8080/swagger-ui/index.html)
* [H2 Console](http://localhost:8080/h2)
  * 유저: sa /


## 참고 화면들

대표적인 화면들의 Screeshot을 보여드립니다. 여기 표시되지 않은 화면들은 App을 실행하여 직접 확인하시기 바랍니다.

### 두 가지의 Layout 형태

현재 다음 두 가지 형태의 레이아웃을 적용하였습니다. 필요한 경우 추가적인 레이아웃을 손쉽게 추가/적용할 수 있습니다.

![image](/docs/home-main-01.png)
메인 화면의 레이아웃입니다.

![image](/docs/user-mgmt-lnb.png)
사용자 관리의 목록 화면에서 LNB가 표시된 레이아웃입니다.

### Authentication 관련 화면들

#### 회원 가입

![image](/docs/security-registration-form.png)
회원 가입 폼입니다. 이미 등록된 email인지와 비밀번호 강도를 체크합니다.

<details>
<summary>이후 절차들입니다.</summary>

![image](/docs/security-registration-succ.png)
회원 등록을 완료하면 사용자에게 확인 메일이 발송됩니다.

![image](/docs/security-registration-mail.png)
사용자는 메일 확인 후 링크를 클릭하여 컨펌을 진행합니다.

![image](/docs/security-registration-confirm.png)
최종 등록 완료 화면입니다.
</details>

#### 로그인 및 로그아웃

![image](/docs/security-login.png)
로그인 폼입니다.

2FA 사용으로 설정한 경우 Google OTP 코드를 입력해야합니다. 그렇지 않은 경우 이메일/비밀번호만 입력하면 됩니다.

<details>
<summary>2FA 설정 화면 및 로그아웃 화면</summary>

![image](/docs/security-2fa.png)
2FA 설정 화면입니다. 2FA를 활성화시킨 경우 Google Authenticator에서 QR 코드를 스캔하여 등록해야 로그인 가능합니다.

![image](/docs/security-logout.png)
사용자가 로그아웃하게 되면 표시되는 화면입니다.

</details>

#### 비밀번호 변경/갱신, 세션 관련 화면들

다음의 기타 화면 모음을 확인하세요.

<details>
<summary>기타 화면 모음</summary>

![image](/docs/security-pw-reset-form.png)

![image](/docs/security-pw-reset-mail.png)

![image](/docs/security-pw-reset-confirm.png)

![image](/docs/security-session-expired.png)

</details>

### 사용자 관리 화면

![image](/docs/user-mgmt-list.png)
사용자 목록 조회 화면입니다.

![image](/docs/user-mgmt-edit-validation.png)
사용자 정보 화면으로 validation 결과를 표시하고 있습니다.

![image](/docs/user-mgmt-edit-dialog.png)
사용자 정보 화면에서 수정 직후 다시 목록 화면으로 이동하기 전의 Dialog를 표시하고 있습니다.

### Swagger UI

![image](/docs/swagger-ui-01.png)
Swagger UI 화면입니다. 현재 게시된 API 목록을 확인할 수 있습니다.

### H2 Console

![image](/docs/h2-console.png)
H2 Console 화면입니다. Table 목록을 확인하고 SQL을 실행할 수 있습니다.

<details>
<summary>H2 Console 로그인 화면</summary>

![image](/docs/h2-console-login.png)
Console 로그인 화면입니다. Driver Class와 JDBC URL을 선택하고 'User Name'에 `sa`를 입력하면 됩니다.

</details>

## 차후 계획

* 현행 코드의 미비점 지속 업데이트 및 보완
  * 분산 환경 대응
    * 이중화 등 분산환경에서의 Session 공유 처리 (Redis 등)
    * 로그인 사용자의 location 및 device 정보 연계 (1 device 1 session)
    * 사용자가 Multi-Device를 사용하는 것을 고려하여 장비별 인증정보 매핑
    * 분산 트랜잭션 (2PC)
  * 샘플 추가
    * 파일업로드, 엑셀, etc
* 신규 프로젝트 개발
  * Vue.js 기반의 Stateless 애플리케이션 개발을 위한 표준 템플릿 구축

## References

* [Spring Security Registration Tutorial](http://www.baeldung.com/spring-security-registration)
* [Spring Data Jpa](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
* [Tutorial: Using Thymeleaf](https://www.thymeleaf.org/doc/tutorials/3.1/usingthymeleaf.html)
* [OpenAPI 3 Library for spring-boot](https://springdoc.org/)
* [VALIDATE.JS](https://validatejs.org/)

