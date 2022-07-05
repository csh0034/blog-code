# Spring Security OAuth2 Resource Server

## 1. Intro

Spring Security 를 활용하여 OAuth 2.0 에서의 **Resource Server** 를 구현해보도록 하겠습니다.

### Spring Security OAuth2

현재 Spring Security OAuth 프로젝트는 유지보수 지원까지 끝난 상태입니다.  
따라서 Spring Security OAuth 의존성이 아닌 Spring Security 의 의존성을 사용해야합니다. 

![img.png](images/01.png)

- [spring security oauth, support](https://spring.io/projects/spring-security-oauth#support)

```kotlin
// spring boot 와 deprecated 된 spring security oauth 의 통합을 지원합니다.
implementation("org.springframework.security.oauth.boot:spring-security-oauth2-autoconfigure:2.6.8")
```

물론 Deprecated 되었지만 `spring-security-oauth2-autoconfigure` 를 사용해도 됩니다.

- [Docs, OAuth2 Boot](https://docs.spring.io/spring-security-oauth2-boot/docs/current/reference/html5/)

### Spring Security OAuth2 Deprecated 이후

Spring Security OAuth2 가 Deprecated 되면서 Spring Security 에서 Resource Server 를 지원합니다.  

Authorization Server 의 경우 별도 프로젝트로 진행되고 있습니다.

- [GitHub, OAuth 2.0 Features Matrix](https://github.com/spring-projects/spring-security/wiki/OAuth-2.0-Features-Matrix)
- [GitHub, Spring Authorization Server](https://github.com/spring-projects/spring-authorization-server/)


## 2. 개발환경

- Spring Boot 2.7.1
- Java 11
- Kotlin 1.6.21
- Gradle 7.4.1
- Intellij IDEA 2022.1.3

### build.gradle.kts

```kotlin
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  id("org.springframework.boot") version "2.7.1"
  id("io.spring.dependency-management") version "1.0.11.RELEASE"
  kotlin("jvm") version "1.6.21"
  kotlin("plugin.spring") version "1.6.21"
}

group = "com.ask"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
  mavenCentral()
}

dependencies {
  implementation("org.springframework.boot:spring-boot-starter-web")
  implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
  implementation("org.jetbrains.kotlin:kotlin-reflect")
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("org.springframework.security:spring-security-test")
}

tasks.withType<KotlinCompile> {
  kotlinOptions {
    freeCompilerArgs = listOf("-Xjsr305=strict")
    jvmTarget = "11"
  }
}

tasks.withType<Test> {
  useJUnitPlatform()
}

```

## 3. 본론

### Jwt Signing Algorithm

Jwt 의 시그니처를 생성하기 위한 다양한 알고리즘이 있습니다.

- HS256: HMac + SHA256
- RS256: RSA + SHA256
- ECDSA(Elliptic Curve Digital Signature Algorithm): 타원곡선을 활용한 디지털 서명
- EdDSA(Edwards-curve Digital Signature Algorithm): Edwards-curve 를 활용한 디지털 서명 
- ...

위의 4가지중 `HS256` 만 HMac 을 사용하므로 대칭키 방식입니다.

AccessToken 을 발급해주는 Authorization Server 와 Resource Server 가 따로 있다면  
Authorization Server 에만 private key 가 존재하므로 공개키 방식이 보안에 더 좋습니다.

하지만 이 예제에선 두 서버를 같이 제공한다는 가정하에 HMac 을 사용하도록 하겠습니다.

### JwtDecoder

jwt 를 검증하기 위해선 JwtDecoder 를 지정해야 하며 기본적으로 RS256 을 사용할 경우  
하단과 같이 jwk 또는 내부에 public key 프로퍼티를 세팅하여 자동설정을 통해 등록할 수도 있습니다.

#### application.yml

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: https://idp.example.com/issuer # (1)
          jwk-set-uri: https://idp.example.com/oauth2/jwks # (2)
          public-key-location: classpath:public.pem # (3)
```

(1) Jwt public claim 중 `iss` 에 대한 값을 검증하며 해당 Authorization Server 에 요청하여 public key 를 찾습니다.

(2) 해당 url 을 통해 jwk (public key) 를 받습니다.

(3) 서버에서 접근 가능한 형태로 public key 의 위치를 지정합니다.

(추가) 1,2  프로퍼티를 동시에 지정 가능합니다.

### ResourceServerConfig.kt

```kotlin
@Configuration
class ResourceServerConfig {

  @Bean
  fun securityFilterChain(http: HttpSecurity): SecurityFilterChain = http
    .csrf() { it.disable() }
    .logout() { it.disable() }
    .sessionManagement() { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
    .oauth2ResourceServer { it.jwt() }  // (1)
    .authorizeHttpRequests {
      it.antMatchers(HttpMethod.GET, "/message/**").hasAuthority("SCOPE_message:read")  // (2)
        .antMatchers(HttpMethod.POST, "/message/**").hasAuthority("SCOPE_message:write")
        .anyRequest().authenticated()
    }
    .build()

  @Bean
  fun jwtDecoder(): JwtDecoder { // (3)
    val secretKey = SecretKeySpec(JWT_SECRET_KEY.toByteArray(), "HmacSHA256") 
    return NimbusJwtDecoder.withSecretKey(secretKey)
      .build()
  }

}
```

(1) OAuth2 bearer token 타입에 jwt 를 사용하도록 설정합니다. (Jwt, Opaque Token 을 지원합니다.)

(2) path 대해서 권한을 설정합니다, 인증을 할 경우 `SCOPE_` 가 추가되므로 prefix 를 지정해야합니다.

(3) `HS256` 방식의 경우 프로퍼티를 통한 자동설정을 지원하지 않으므로 의 JwtDecoder 를 직접 Bean 으로 등록합니다.

### MessageController.kt

ResourceServerConfig 에서 등록한 경로 테스트를 위해 API 를 추가하였습니다.

```kotlin
@RestController
class MessageController {

  @GetMapping("/message")
  fun message() = "GET message..."

  @PostMapping("/message")
  fun message(message: String) = "POST $message..."

}
```

### Constants.kt

HS256 이므로 32 byte(256bit) 의 secret key 가 필요하여 상수로 선언하였습니다.  
실제 운영 환경에선 암호화된 형태로 프로퍼티 또는 환경변수 등으로 주입받는걸 권장드립니다.

```kotlin
const val JWT_SECRET_KEY = "0123456789-0123456789-0123456789"
```

### JwtUtils.kt

Authorization Server 에서 발급되었다는 가정으로 Jwt (Access Token) 를 생성하기 위해  
하단과 같이 top level function 을 추가 하였습니다.

```kotlin
private val jwtEncoder = NimbusJwtEncoder(ImmutableSecret(JWT_SECRET_KEY.toByteArray()))

fun generateJwt(subject: String, scopes: Array<String>, expiresAt: Instant = Instant.now().plus(1, ChronoUnit.HOURS)): String {
  val header = JwsHeader.with(MacAlgorithm.HS256)
    .type("JWT")
    .build()

  val claims = JwtClaimsSet.builder()
    .subject(subject)
    .expiresAt(expiresAt)
    .claim("scope", scopes)
    .build()

  return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).tokenValue
}
```

```kotlin
internal class JwtUtilsKtTest {

  private val log = LoggerFactory.getLogger(javaClass)

  @Test
  fun generateJwt() {
    log.info("jwt: ${generateJwt("ASk", arrayOf("message:read", "message:write"))}")
    log.info("jwt: ${generateJwt("ASk", arrayOf("message:read", "message:write"), Instant.now())}")
  }

}
```

```text
19:46:46.549 [main] INFO com.ask.resourceserver.utils.JwtUtilsKtTest - jwt: eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJBU2siLCJleHAiOjE2NTcwMjE2MDYsInNjb3BlIjpbIm1lc3NhZ2U6cmVhZCIsIm1lc3NhZ2U6d3JpdGUiXX0.AXM6-Z56qIr38oHtG2rUrdrN1pJpVs8t2uN4rwo-xt4
19:46:46.550 [main] INFO com.ask.resourceserver.utils.JwtUtilsKtTest - jwt: eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJBU2siLCJleHAiOjE2NTcwMTgwMDYsInNjb3BlIjpbIm1lc3NhZ2U6cmVhZCIsIm1lc3NhZ2U6d3JpdGUiXX0.PMIZjdfkNGlK5rka10vrpx2RP8iArAZx33WwJyj3yHo
```

```text
Header
{
  "typ": "JWT",
  "alg": "HS256"
}

Payload
{
  "sub": "ASk",
  "exp": 1657021606,
  "scope": [
    "message:read",
    "message:write"
  ]
}
```

### 테스트 코드

```kotlin
@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false) // 항상 결과를 출력하도록 설정
internal class ResourceServerTest(
  @Autowired private val mockMvc: MockMvc
) {

  @Test
  fun `유효한 jwt 일 경우 200`() {
    // given
    val jwt = generateJwt("ASk", arrayOf("message:read"))

    // expect
    mockMvc.get("/message") {
      header(HttpHeaders.AUTHORIZATION, "Bearer $jwt")
    }.andExpect {
      status { isOk() }
      content {
        contentTypeCompatibleWith(MediaType.TEXT_PLAIN) // contentType 뒤에 charset 이 추가되므로 contentTypeCompatibleWith 사용
        string("GET message...")
      }
    }
  }

  @Test
  fun `헤더에 jwt 가 없을 경우 401`() {
    mockMvc.get("/message")
      .andExpect {
        status { isUnauthorized() }
      }
  }

  @Test
  fun `만료된 jwt 일 경우 401`() {
    // given
    val jwt = generateJwt("ASk", arrayOf("message:read", "message:write"), Instant.EPOCH) // 만료시간 과거로 설정

    // expect
    mockMvc.get("/message") {
      header(HttpHeaders.AUTHORIZATION, "Bearer $jwt")
    }.andExpect {
      status { isUnauthorized() }
      header {
        string(HttpHeaders.WWW_AUTHENTICATE, containsString("Bearer error=\"invalid_token"))
        string(HttpHeaders.WWW_AUTHENTICATE, containsString("Jwt expired at"))
      }
    }
  }

  @Test
  fun `권한이 없는 jwt 일 경우 403`() {
    // given
    val jwt = generateJwt("ASk", arrayOf("message:read"))

    // expect
    mockMvc.post("/message") {
      header(HttpHeaders.AUTHORIZATION, "Bearer $jwt")
    }.andExpect {
      status { isForbidden() }
      header {
        string(HttpHeaders.WWW_AUTHENTICATE, containsString("Bearer error=\"insufficient_scope"))
      }
    }
  }

}
```

### 실행 결과

![img.png](images/02.png)

## 4. 마무리

블로그에 사용된 코드는 [GitHub](https://github.com/csh0034/blog-code/blob/master/spring-security/spring-security-oauth2-resource-server/README.md) 에서 확인 하실 수 있습니다.

## 5. 참조

- [Docs, OAuth 2.0 Resource Server JWT](https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/jwt.html)
