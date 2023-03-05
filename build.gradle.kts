import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.10"
    kotlin("plugin.serialization") version "1.8.10"
    id("me.champeau.gradle.jmh") version "0.5.3"
    application
}

group = "io.github.criwen"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("io.kotest:kotest-assertions-api:5.5.5")
    testImplementation("io.kotest:kotest-assertions-core:5.5.5")
    testImplementation("io.kotest:kotest-common-jvm:5.5.5")
    testImplementation("io.ktor:ktor-client-apache:2.2.3")
    testImplementation("io.ktor:ktor-client-auth:2.2.3")
    testImplementation("io.ktor:ktor-client-cio:2.2.3")
    testImplementation("io.ktor:ktor-client-content-negotiation:2.2.3")
    testImplementation("io.ktor:ktor-serialization-kotlinx-json:2.2.3")
    testImplementation("io.ktor:ktor-client-core:2.2.3")
    testImplementation("io.ktor:ktor-client-logging:2.2.3")
    testImplementation(kotlin("test"))
}

jmh {
    warmupIterations = 1
    iterations = 10
    timeout = "10s"
    fork = 1
    jvmArgsAppend = listOf("-Djmh.separateClasspathJAR=true")
    benchmarkMode = listOf("thrpt", "avgt")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
