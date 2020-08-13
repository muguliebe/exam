import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.3.2.RELEASE"
    id("io.spring.dependency-management") version "1.0.9.RELEASE"
    kotlin("jvm") version "1.3.72"
    kotlin("plugin.spring") version "1.3.72"
}

group = "com.exam"
version = "0.1"
java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
    mavenCentral()
    jcenter()
}

dependencies {

    // spring
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    // kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlin:kotlin-noarg")

    // database: embedded postgresql
    runtimeOnly("org.postgresql:postgresql")
    implementation("com.opentable.components:otj-pg-embedded:0.13.3")
    implementation("org.flywaydb:flyway-core:6.5.3")

    // mybatis
    implementation("org.mybatis.spring.boot:mybatis-spring-boot-starter:2.1.0")
    implementation("org.mybatis:mybatis:3.5.1")
    implementation("org.mybatis:mybatis-spring:2.0.1")
    implementation("org.mybatis:mybatis-typehandlers-jsr310:1.0.2")

    // swagger
    implementation("io.springfox:springfox-swagger2:2.9.2")
    implementation("io.springfox:springfox-swagger-ui:2.9.2")

    // etc
    implementation("com.google.guava:guava:23.0")
    implementation("khttp:khttp:1.0.0")
    implementation("com.auth0:java-jwt:3.8.1")
    implementation("com.google.code.gson:gson:2.8.5")


    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}
