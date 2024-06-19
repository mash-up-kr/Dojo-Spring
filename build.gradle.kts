import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.3.0"
    id("io.spring.dependency-management") version "1.1.5"

    kotlin("jvm") version "1.9.24"
    kotlin("plugin.spring") version "1.9.24"
    kotlin("plugin.jpa") version "2.0.0"

    // lint
    id("org.jlleitschuh.gradle.ktlint") version "12.1.1"

    idea
}

val properties = project.properties

java {
    sourceCompatibility = JavaVersion.toVersion(properties["javaVersion"] as String)
}

allprojects {
    group = "com.mashup"
    version = "1.0.0"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply {
        plugin("kotlin")
        plugin("kotlin-spring")
        plugin("org.springframework.boot")
        plugin("io.spring.dependency-management")
        plugin("org.jlleitschuh.gradle.ktlint")
        plugin("idea")
    }

    dependencies {
        implementation("org.jetbrains.kotlin:kotlin-reflect")
        implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
        implementation("org.springframework:spring-context") // spring-web 의존성 낮추려고 core를 넣었는데 괜찮을지

        testImplementation("org.springframework.boot:spring-boot-starter-test")
        testRuntimeOnly("org.junit.platform:junit-platform-launcher")
        // logging
        implementation("io.github.oshai:kotlin-logging-jvm:${properties["kotlinLoggingJvmVersion"]}")
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs += "-Xjsr305=strict"
            jvmTarget = properties["javaVersion"] as String
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    tasks.withType<org.springframework.boot.gradle.tasks.bundling.BootJar> {
        mainClass.set("com.mashup.dojo.DojoApplication") // 메인 클래스를 설정합니다
    }
}

springBoot {
    mainClass.set("com.mashup.dojo.DojoApplication")
}

project(":api") {
    dependencies {
        api(project(":service"))
        implementation("org.springframework.boot:spring-boot-starter-web")
    }
}

project(":service") {
    dependencies {
        api(project(":entity"))
        api(project(":common"))
    }
}

project(":entity") {
    apply(plugin = "org.jetbrains.kotlin.plugin.jpa")

    dependencies {
        api("org.springframework.boot:spring-boot-starter-data-jpa")
        runtimeOnly("com.mysql:mysql-connector-j:${properties["mysqlConnectorVersion"]}")
        // runtimeOnly("com.h2database:h2:${properties["h2DatabaseVersion"]}") // todo : fade out

        // Jasypt
        implementation("com.github.ulisesbocchio:jasypt-spring-boot-starter:${properties["jasyptSpringBootStarterVersion"]}")

        // Querydsl
        implementation("io.github.openfeign.querydsl:querydsl-core:${properties["queryDslVersion"]}")
        implementation("io.github.openfeign.querydsl:querydsl-jpa:${properties["queryDslVersion"]}")
        annotationProcessor("io.github.openfeign.querydsl:querydsl-apt:${properties["queryDslVersion"]}:jpa")
        annotationProcessor("jakarta.annotation:jakarta.annotation-api")
        annotationProcessor("jakarta.persistence:jakarta.persistence-api")

        // query 값 정렬
        implementation("com.github.gavlyukovskiy:p6spy-spring-boot-starter:${properties["p6spyVersion"]}")
    }

    allOpen {
        annotation("jakarta.persistence.Entity")
        annotation("jakarta.persistence.Embeddable")
        annotation("jakarta.persistence.MappedSuperclass")
    }

    noArg {
        annotation("jakarta.persistence.Entity")
    }
}

project(":common") {
    dependencies {
    }
}
