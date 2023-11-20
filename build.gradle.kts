import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    id("java")
    id("jacoco")
    id("checkstyle")
    id("com.github.spotbugs") version "6.0.0-beta.3"
    id("org.springframework.boot") version "3.0.6"
    id("io.spring.dependency-management") version "1.1.0"
}

group = "it.unibz.digidojo"
version = "1.0"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains:annotations:24.0.1")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-log4j2")
    implementation("org.apache.kafka:kafka-streams")
    implementation("org.springframework.kafka:spring-kafka")
    implementation("org.postgresql:postgresql")
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    implementation(project(":DigiDojoSharedModel"))
    testImplementation("com.h2database:h2")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.kafka:spring-kafka-test")
}

configurations {
    all {
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
    }
}

extra {
    val springCloudVersion = "2022.0.2"
    dependencyManagement {
        imports {
            mavenBom("org.springframework.cloud:spring-cloud-dependencies:${springCloudVersion}")
        }
    }
}

spotbugs {
    ignoreFailures.set(false)
//    excludeFilter.set(project.file("${project.projectDir}/configuration/spotbugs/spotbugs-filters.xml"))
}

val excludeFromCoverage = listOf(
        "it/unibz/digidojo/**/model/**",
        "it/unibz/digidojo/**/util/CRUD",
)

val codeCoverageFiles: FileTree = sourceSets.main.get().output.asFileTree.matching {
    exclude(excludeFromCoverage)
}

tasks {
    withType<JacocoReport> {
        classDirectories.setFrom(codeCoverageFiles)
    }

    jacocoTestCoverageVerification {
        /*
         Files to include in code coverage evaluations and reports
         */
        classDirectories.setFrom(codeCoverageFiles)

        /*
         Set minimum code coverage to fail build
        */
        violationRules {
            // TODO: Increase to 90% after implementing more tests
            rule { limit { minimum = BigDecimal.valueOf(0.8) } }
        }
    }

    check {
        dependsOn(jacocoTestCoverageVerification)
    }

    withType<Test> {
        useJUnitPlatform()

        testLogging {
            events = setOf(
                    TestLogEvent.FAILED,
                    TestLogEvent.SKIPPED
            )
            exceptionFormat = TestExceptionFormat.FULL
            showCauses = true
            showExceptions = true
            showStackTraces = true
            showStandardStreams = true
        }
    }
}