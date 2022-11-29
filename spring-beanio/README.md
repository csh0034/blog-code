# Spring BeanIO

## 1. Intro

Spring 환경에서 BeanIO 를 활용한 전문통신 처리

### 1.1 전문통신이란?

서로 주고 받을 데이터의 포맷을 정한 후 약속된 데이터 패킷을 전송하고 수신하는 방법이다.  
Fixed Length Format 을 사용하며 전문을 구성하는 필드들의 길이를 고정시키는 방식이다.

전문은 하단과 같이 두 가지로 나눠진다.

- header: 요청 및 body 의 대한 데이터
- body: 실제 해당 통신에 필요한 데이터

## 2. 개발환경

- Spring Boot 2.7.6
- Java 11
- Gradle 7.5.1
- Intellij IDEA 2022.2.2

### build.gradle

```groovy
plugins {
    id 'java'
    id 'org.springframework.boot' version '2.7.6'
    id 'io.spring.dependency-management' version '1.0.15.RELEASE'
}

group = 'com.ask'
version = '0.0.1-SNAPSHOT'
sourceCompatibility = '11'

configurations {
    compileOnly {
        extendsFrom annotationProcessor
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.beanio:beanio:2.1.0' // beanio lib
    compileOnly 'org.projectlombok:lombok'
    annotationProcessor 'org.projectlombok:lombok'
    testCompileOnly 'org.projectlombok:lombok'
    testAnnotationProcessor 'org.projectlombok:lombok'
    annotationProcessor 'org.springframework.boot:spring-boot-configuration-processor'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
}

tasks.named('test') {
    useJUnitPlatform()
}
```

## 3. 본론

### 3.1 mapping.xml

- BeanIO 는 xml 형식의 mapping file 을 사용하며 Bean Object 가 record 에 Binding 되는 방법을 나타낸다.
- Spring Boot 와 사용시 resources 폴더 application.yml 과 같은 위치에 추가

```xml
<?xml version='1.0' encoding='UTF-8' ?>
<beanio xmlns="http://www.beanio.org/2012/03" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://www.beanio.org/2012/03 http://www.beanio.org/2012/03/mapping.xsd">
  <stream name="request" format="fixedlength" strict="true"> (1)
    <parser>
      <property name="recordTerminator" value=""/> (2)
    </parser>
    <record name="sms" class="com.ask.springbeanio.config.BeanIODto" maxOccurs="1"> (3)
      <segment name="header" class="map" minOccurs="1" maxOccurs="1"> (4)
        <field name="TX_CODE" length="15" justify="left" padding=" "/>
        <field name="GUID" length="32" required="true" />
        <field name="LENGTH" length="5" justify="right" padding="0" default="00000" type="int"/> (5)
      </segment>
      <segment name="body" class="map" minOccurs="1" maxOccurs="1">
        <field name="MESSAGE" length="10" justify="left" padding=" " />
        <field name="TO" length="15" justify="left" padding=" "/>
      </segment>
      <field name="end" length="2" justify="left" padding=" " default="@@"/> (6)
    </record>
  </stream>
</beanio>
```

- (1) xml, csv, fixedlength 등의 format 을 사용 가능. 
  - 전문의 고정길이 포맷을 위해 fixedlength 를 사용한다. 
  - strict 의 true 경우 record 순서 및 record 길이를 계산하여 적용한다.
- (2) record 의 마지막 문자를 의미한다. 빈칸으로 할 경우 record 가 여러개더라도 한줄로 인식한다.
  - 현재는 record 의 maxOccurs(최대 record 수) 가 1 이므로 꼭 필요하진 않음.
- (3) 해당 record 의 이름과 mapping 할 class 의 fqcn 을 나타낸다.
- (4) segment 의 경우 record 에서 필드의 그룹을 나타낸다.
  - class 에 map 의 경우 별칭이며 구현체에 HashMap 을 사용한다.   
    (문서에는 LinkedHashMap 으로 나와있다.)
  - 반드시 한개의 segment 를 나타내기 위해 minOccurs, maxOccurs 를 1 로 설정 
- (5) field 의 정보를 나타낸다. type 도 지정 가능.
- (6) record 의 마지막을 나타내는 field 를 나타낸다.

### 3.2 Spring Configuration

#### 3.2.1 Properties, application.yml

- 생성자 방식의 Properties Binding

```java
@ConfigurationProperties("beanio")
@ConstructorBinding
@RequiredArgsConstructor
@Getter
@ToString
public class BeanIOProperties {

  private final String mapping; // mapping 파일 이름
  private final String streamName; // mapping 파일에서 사용할 stream 의 name
  private final String recordName; // stream 에서 사용할 record 의 name

}
```

```yaml
beanio:
  mapping: mapping.xml
  stream-name: request
  record-name: sms
```

#### 3.2.2 Mapping DTO

- 선언한 stream 의 record 에 맞게 Binding 할 DTO 선언

```java
@Getter
@Setter
@ToString
public class BeanIODto {

  private Map<String, Object> header;
  private Map<String, Object> body;
  private String end;

}
```

#### 3.2.3 Config

- BeanReader, BeanWriter, Marshaller, Unmarshaller 등을 생성하는 factory 를 Bean 으로 등록
- 위에 목록의 대상중 Marshaller, Unmarshaller 는 thread-safe 하지 않으므로 요청마다 객체를 생성해야한다.
- mapping file load
  - `factory.loadResource(String)`: application classpath 에 mapping file 을 load 한다.
  - `factory.load(InputStrem)`, `factory.load(File)`: mapping file 이 외부에 있을 경우 지정 가능.

```java
@Configuration
@RequiredArgsConstructor
public class BeanIOConfig {

  private final BeanIOProperties beanIOProperties;

  @Bean
  public StreamFactory streamFactory() {
    StreamFactory factory = StreamFactory.newInstance();
    factory.loadResource(beanIOProperties.getMapping());
    return factory;
  }

}
```

#### 3.2.4 Utils

- Marshaller: object 를 mapping.xml 의 형식에 맞게 String record 로 변환한다.
- Unmarshaller: String record 를 mapping.xml 의 형식에 맞게 object 로 변환한다.

```java
@Component
@RequiredArgsConstructor
public class BeanIOUtils {

  private final StreamFactory streamFactory;
  private final BeanIOProperties beanIOProperties;

  public String marshal(BeanIODto dto) {
    Marshaller marshaller = streamFactory.createMarshaller(beanIOProperties.getStreamName());
    return marshaller.marshal(beanIOProperties.getRecordName(), dto).toString();
  }

  public BeanIODto unmarshal(String record) {
    Unmarshaller unmarshaller = streamFactory.createUnmarshaller(beanIOProperties.getStreamName());
    return (BeanIODto) unmarshaller.unmarshal(record);
  }

}
```

### Test Code

```java
@SpringBootTest
@Slf4j
class BeanIOUtilsIntegrationTest {

  @Autowired
  private BeanIOUtils beanIOUtils;

  @Test
  void marshal() {
    // given
    BeanIODto dto = new BeanIODto();

    Map<String, Object> header = new HashMap<>();
    header.put("TX_CODE", "a12");
    header.put("GUID", UUID.randomUUID().toString());
    header.put("LENGTH", 45);
    dto.setHeader(header);

    Map<String, Object> body = new HashMap<>();
    body.put("MESSAGE", "HI !!");
    body.put("TO", "010-1234-5678");
    dto.setBody(body);

    // when
    String result = beanIOUtils.marshal(dto);

    // then
    assertThat(result).hasSize(79);
    log.info("result: {}", result); // result: a12            70affc50-46fd-4145-a2a1-d495f73200045HI !!     010-1234-5678  @@
  }

  @Test
  void unmarshal() {
    // given
    String record = "a12            b1ef6fcf-6371-4459-9ecb-b7e1d48a00045HI !!     010-1234-5678  @@";

    // when
    BeanIODto dto = beanIOUtils.unmarshal(record);

    // then
    assertAll(
        () -> assertThat(dto).isNotNull(),
        () -> assertThat(dto.getHeader()).hasSize(3),
        () -> assertThat(dto.getBody()).hasSize(2),
        () -> assertThat(dto.getEnd()).isEqualTo("@@")
    );
    log.info("dto: {}", dto); // dto: BeanIODto(header={LENGTH=45, TX_CODE=a12, GUID=b1ef6fcf-6371-4459-9ecb-b7e1d48a}, body={MESSAGE=HI !!, TO=010-1234-5678}, end=@@)
  }

}
```

## 4. 마무리

블로그에 사용된 코드는 [GitHub](https://github.com/csh0034/blog-code/tree/master/spring-beanio) 에서 확인 하실 수 있습니다.

## 5. 참조

- [BeanIO](http://beanio.org/)
