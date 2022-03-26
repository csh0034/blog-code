import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    val springBootVersion = "2.6.5"
    val dependencyManagementVersion = "1.0.11.RELEASE"
    val kotlinVersion = "1.6.10"

	id("org.springframework.boot") version springBootVersion
	id("io.spring.dependency-management") version dependencyManagementVersion
	kotlin("jvm") version kotlinVersion
    kotlin("kapt") version kotlinVersion
	kotlin("plugin.spring") version kotlinVersion
    kotlin("plugin.allopen") version kotlinVersion
	kotlin("plugin.jpa") version kotlinVersion
}

group = "com.ask"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	runtimeOnly("com.h2database:h2")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    kapt("org.springframework.boot:spring-boot-configuration-processor")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
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

allOpen {
    annotation("javax.persistence.Entity")
}
