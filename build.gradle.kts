import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.2.51"
    id("jacoco")
}

group = "BridgeTree"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

jacoco {
    toolVersion = "0.7.9"
}

tasks.withType<JacocoReport> {
    reports {
        xml.apply {
            isEnabled = true

        }
        html.apply {
            isEnabled = true
        }
        executionData(tasks.withType<Test>())
    }
}


dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("com.google.guava", "guava", "22.0")
    implementation("io.github.microutils", "kotlin-logging", "1.6.10")
    implementation("org.slf4j", "slf4j-simple", "1.6.2")


    testImplementation("org.junit.jupiter", "junit-jupiter-api", "5.0.0")
    testImplementation("org.junit.jupiter", "junit-jupiter-params", "5.0.0")

    testRuntime("org.junit.jupiter", "junit-jupiter-engine", "5.0.0")
    testRuntime("org.junit.platform", "junit-platform-console", "1.0.0")
}

val test by tasks.getting(Test::class) {
    useJUnitPlatform()
    testLogging {
        events("PASSED", "FAILED", "SKIPPED")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}