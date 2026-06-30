plugins {
    id("org.jetbrains.kotlin.jvm") version "2.2.0"
    id("com.github.johnrengelman.shadow") version "8.1.0"
}

group = "com.wuyumoom"
version = "2.0.2-SNAPSHOT"

repositories {
    mavenCentral()
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test")


    implementation(group = "org.java-websocket", name = "Java-WebSocket", version = "1.6.0")
    implementation(group = "com.alibaba", name = "fastjson", version = "2.0.60")
    implementation("org.jline:jline:3.25.0")
    implementation("org.yaml:snakeyaml:2.4")
    implementation("org.slf4j:slf4j-api:1.7.36")
    implementation("io.github.microutils:kotlin-logging-jvm:2.1.23")
    implementation("ch.qos.logback:logback-classic:1.5.13")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}

tasks.named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
    archiveBaseName.set("YuQQBot")
    archiveClassifier.set("")
    archiveVersion.set(version.toString()) // 可以去掉版本号
    mergeServiceFiles()
    manifest {
        attributes["Main-Class"] = "com.wuyumoom.yuqqbot.YuQQBot"
    }
}