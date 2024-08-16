import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.3.0"
    id("io.spring.dependency-management") version "1.1.5"

    kotlin("jvm") version "1.9.24"
    kotlin("plugin.spring") version "1.9.24"
    kotlin("plugin.jpa") version "2.0.0"

    kotlin("kapt") version "1.9.24"

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
        plugin("kotlin-kapt")
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
        mainClass.set("com.mashup.dojo.DojoApplicationKt") // 메인 클래스를 설정합니다
    }
}

springBoot {
    mainClass.set("com.mashup.dojo.DojoApplicationKt")
}

project(":api") {
    dependencies {
        api(project(":service"))
        implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0")
        implementation("org.springframework.boot:spring-boot-starter-web")
        implementation("ca.pjer:logback-awslogs-appender:1.6.0")

        // for JWT based token
        implementation("io.jsonwebtoken:jjwt-api:0.12.6")
        runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
        runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")
    }
}

project(":service") {
    dependencies {
        api(project(":entity"))
        api(project(":common"))

        // for sse
        implementation("org.springframework.boot:spring-boot-starter-web")
    }
}

project(":entity") {
    apply(plugin = "org.jetbrains.kotlin.plugin.jpa")

    dependencies {
        api("org.springframework.boot:spring-boot-starter-data-jpa")
        api("com.mysql:mysql-connector-j:${properties["mysqlConnectorVersion"]}")
        runtimeOnly("com.h2database:h2:${properties["h2DatabaseVersion"]}") // todo : fade out

        // Jasypt
        implementation("com.github.ulisesbocchio:jasypt-spring-boot-starter:${properties["jasyptSpringBootStarterVersion"]}")

        // QueryDSL
        implementation("com.querydsl:querydsl-jpa:${properties["queryDslVersion"]}:jakarta")
        kapt("com.querydsl:querydsl-apt:${properties["queryDslVersion"]}:jakarta")
        kapt("jakarta.annotation:jakarta.annotation-api")
        kapt("jakarta.persistence:jakarta.persistence-api")

        // query 값 정렬
        implementation("com.github.gavlyukovskiy:p6spy-spring-boot-starter:${properties["p6spyVersion"]}")
    }

    val generated = file("src/main/generated")

    tasks.withType<JavaCompile> {
        options.generatedSourceOutputDirectory.set(generated)
    }

    sourceSets {
        main {
            kotlin.srcDirs += generated
        }
    }

    tasks.named("clean") {
        doLast {
            generated.deleteRecursively()
        }
    }

    kapt {
        generateStubs = true
    }
}

project(":common") {
    dependencies {
        implementation("com.amazonaws:aws-java-sdk-s3:1.12.752")
    }
}
