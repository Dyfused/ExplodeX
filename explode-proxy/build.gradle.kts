plugins {
    kotlin("jvm")
    application
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "explode"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":booster"))
    implementation(project(":booster-graphql"))
    implementation(project(":booster-resource"))
    implementation(project(":labyrinth"))
    implementation(project(":gatekeeper"))

    // ktor client
    implementation("io.ktor:ktor-client-core:2.1.2")
    implementation("io.ktor:ktor-client-cio:2.1.2")

    // jackson kt support
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.4")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.13.4")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

application {
    mainClass.set("explode2.booster.BoosterMainKt")
}

tasks.shadowJar {
    mergeServiceFiles()
    transform(com.github.jengelman.gradle.plugins.shadow.transformers.Log4j2PluginsCacheFileTransformer::class.java)
}