# Spring Configuration Properties

## 개요

`@ConfigurationProperties` 를 사용할때 `@ConstructorBinding` 과 `Lombok` 을 사용하면  
`metadata.json` 에 프로퍼티가 안생기는 이슈가 있음. Maven, Gradle 동일

서버 실행 및 기능동작에는 영향이 없지만 IntelliJ 사용시  
노란색으로 `Cannot resolve configuration property` 라는 warning 표시함

medata.json
- target/classes/META-INF/spring-configuration-metadata.json

## 증상

### Lombok 미사용

하단과 같이 lombok 을 사용하지 않을 경우 생성된 metadata.json 을 보면 properties 부분 잘 생성되어있음

```java
@ConfigurationProperties("custom")
@ConstructorBinding
public class CustomProperties {

  private final String content;

  public CustomProperties(String content) {
    this.content = content;
  }

  public String getContent() {
    return content;
  }

  @Override
  public String toString() {
    return "CustomProperties{" +
        "content='" + content + '\'' +
        '}';
  }

}
```

```json
{
  "groups": [
    {
      "name": "custom",
      "type": "com.ask.properties.config.CustomProperties",
      "sourceType": "com.ask.properties.config.CustomProperties"
    }
  ],
  "properties": [
    {
      "name": "custom.content",
      "type": "java.lang.String",
      "sourceType": "com.ask.properties.config.CustomProperties"
    }
  ],
  "hints": []
}
```

### Lombok 사용

하지만 하단과 같이 lombok 을 사용할 경우 properties 부분이 생성되지 않음.

```java
@ConfigurationProperties("custom2")
@ConstructorBinding
@RequiredArgsConstructor
@Getter
@ToString
public class Custom2Properties {

  private final String content;

}
```

```json
{
  "groups": [
    {
      "name": "custom2",
      "type": "com.ask.properties.config.Custom2Properties",
      "sourceType": "com.ask.properties.config.Custom2Properties"
    }
  ],
  "properties": [],
  "hints": []
}
```

## 조치

spring-boot-configuration-processor 의존성이 Lombok 의존성보다 뒤에 있어야함.  
정확하진 않으나 어노테이션 프로세서가 동작할때 Lombok 을 먼저 처리해야 하는것 같음.

### Maven

```xml
<!-- before -->
<dependencies>
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-configuration-processor</artifactId>
    <optional>true</optional>
  </dependency>
  <dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
  </dependency>
</dependencies>

<!-- after -->
<dependencies>
  <dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
  </dependency>
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-configuration-processor</artifactId>
    <optional>true</optional>
  </dependency>
</dependencies>
```

### Gradle

```groovy
// before
annotationProcessor 'org.springframework.boot:spring-boot-configuration-processor'
compileOnly 'org.projectlombok:lombok'
annotationProcessor 'org.projectlombok:lombok'

// before
compileOnly 'org.projectlombok:lombok'
annotationProcessor 'org.projectlombok:lombok'
annotationProcessor 'org.springframework.boot:spring-boot-configuration-processor'
```

## 결과

의존성 순서 변경후 Lombok 을 사용한 `Custom2Properties` 의 경우에도 정상적으로 생성됨.

```json
{
  "groups": [
    {
      "name": "custom",
      "type": "com.ask.properties.config.CustomProperties",
      "sourceType": "com.ask.properties.config.CustomProperties"
    },
    {
      "name": "custom2",
      "type": "com.ask.properties.config.Custom2Properties",
      "sourceType": "com.ask.properties.config.Custom2Properties"
    }
  ],
  "properties": [
    {
      "name": "custom.content",
      "type": "java.lang.String",
      "sourceType": "com.ask.properties.config.CustomProperties"
    },
    {
      "name": "custom2.content",
      "type": "java.lang.String",
      "sourceType": "com.ask.properties.config.Custom2Properties"
    }
  ],
  "hints": []
}
```

## 참조

- [Spring Boot Reference, Configuration Metadata
  ](https://docs.spring.io/spring-boot/docs/current/reference/html/configuration-metadata.html)
- [Spring Boot GitHub Issue, Property names are not available with Lombok and constructor binding](https://github.com/spring-projects/spring-boot/issues/18730)
