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
    implementation(project(":labyrinth-mongodb"))
    implementation(project(":gatekeeper"))
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