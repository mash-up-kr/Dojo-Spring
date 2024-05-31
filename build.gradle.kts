import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.3.0"
    id("io.spring.dependency-management") version "1.1.5"

    kotlin("jvm") version "1.9.24"
    kotlin("plugin.spring") version "1.9.24"

    // lint
    id("org.jlleitschuh.gradle.ktlint") version "11.0.0"

    idea
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
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
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs += "-Xjsr305=strict"
            jvmTarget = "21"
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
    dependencies {
        // 실제 DB 연결할 때 디펜던시 추가할 것
        implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    }
}

project(":common") {
    dependencies {
    }
}
