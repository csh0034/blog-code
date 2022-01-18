# Spring Integration MQTT

## 1. Intro
Spring Integration 을 이용하여 MQTT Broker 연동에 대하여 알아보겠습니다.   

본 게시글에서는 Kotlin 설정 및 Integration 에 대하여는 깊게 다루지 않습니다.

## 2. MQTT

### MQTT 란?
MQTT(Message Queue for Telemetry Transport)는 M2M 또는 IoT를 위한 경량 프로토콜로서   
저전력 장비에서도 운용 가능하며 network bandwidth가 작은 곳에서도 충분히 운용 가능하도록 설계된 프로토콜입니다.

### MQTT 특징

#### Connection Oriented
MQTT 브로커와 연결을 요청하는 클라이언트는 TCP/IP 소켓 연결을 한 후 명시적으로 연결을 끊거나   
네트워크 사정에 의해 연결이 끊어질 때까지 상태를 유지합니다.

Live 라는 하트비트와 Topic에 발행되는 메시지를 통해 연결을 유지하고 메시지 송수신을 하게 됩니다.

#### Broker
MQTT 프로토콜을 사용하여 Publisher 와 Subscriber 사이에서 메시지를 관리하며 전송해주는 역할을 합니다.

다양한 MQTT Broker
- [Mosquitto](https://mosquitto.org/)
- [Mosca](http://www.mosca.io/)
- [HiveMQ](https://www.hivemq.com/)
- [RabbitMQ, MQTT Plugin](https://www.rabbitmq.com/mqtt.html)
- ...

[MQTT GitHub](https://github.com/mqtt/mqtt.org/wiki/server-support) 에서 다양한 브로커에 대한 기능 지원 목록을 확인 가능합니다.

#### Pub/Sub Model
브로커를 통한 발행/구독 메세징 패턴으로서 발행측은 브로커에게 메세지를 전송하며 브로커는 구독하고있는  
클라이언트에게 메세지를 전송합니다.

따라서 일대일 혹은 일대다 통신이 가능합니다.

#### QoS(Quality of Service)
MQTT 는 3가지의 QoS Level 이 존재합니다.
- At most once (0) : **최대 한번 전송**하며 메시지를 전달 할뿐 구독하는 클라이언트가 받는걸 보장하지 않음
- At least once (1) : **최소 1회 전송**하며 구독하는 클라이언트가 메시지를 받았는지 불확실하면 정해진 횟수만큼 재전송
- Exactly once (2) : 구독하는 클라이언트가 요구된 메시지를 **정확히 한 번** 수신할 수 있도록 보장한다

#### Topic
메시지를 발행/구독하는 행위는 채널 단위로 일어나며 이를 MQTT 에서는 토픽이라고 하며 토픽은 슬래시(/)로 구분되는 계층 구조를 갖습니다.

최상위 토픽은 슬래시(/)로 시작되지 않아야 하며 와일드 카드 문자를 사용할 수 있습니다.
- `+` : One-Level Wild Card
- `#` : Multi-Level Wild Card

> a/b/c/d  
> a/b/+/d  
> a/b/#

#### LTW (Last will and testament)
MQTT 는 신뢰할 수 없는 네트워크를 포함하는 경우에 자주 사용되기 때문에 비정상적으로 연결이 끊어질 수 있다고 가정하는 것이 합리적입니다.  

LTW 는 유언, 유언장이라는 의미로서 브로커와 클라이언트가 연결이 끊어지면 자동으로 다른 구독자들에게 메세지가 전송되는 기능입니다.

일반적으로 브로커에 연결을 시도하는 시점에 지정되며 will topic, will message, will qos 등을 지정합니다.

## 3. 개발환경
- Spring Boot 2.6.2
- Java 11
- Kotlin 1.6.10
- Gradle 7.3.2
- Intellij IDEA 2021.3.1

### build.gradle.kts
```kotlin
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    val springBootVersion = "2.6.2"
    val dependencyManagementVersion = "1.0.11.RELEASE"
    val kotlinVersion = "1.6.10"

    id("org.springframework.boot") version springBootVersion
    id("io.spring.dependency-management") version dependencyManagementVersion
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
    kotlin("kapt") version kotlinVersion
}

group = "com.ask"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-integration")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.springframework.integration:spring-integration-mqtt")
    implementation("org.springframework.integration:spring-integration-jmx")
    kapt("org.springframework.boot:spring-boot-configuration-processor")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.integration:spring-integration-test")
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

## 4. 마무리

## 5. 참조
- [Spring Integration, MQTT Support](https://docs.spring.io/spring-integration/docs/current/reference/html/mqtt.html)
- [Introduction-mqtt-qos](https://www.emqx.com/en/blog/introduction-to-mqtt-qos)
- [MQTT 적용을 통한 중계시스템 개선](https://techblog.woowahan.com/2540/)
