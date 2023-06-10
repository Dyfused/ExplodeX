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
    implementation(project(":resource"))
    implementation(project(":labyrinth"))
    implementation(project(":labyrinth-mongodb"))
    implementation(project(":gatekeeper"))
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

application {
    mainClass.set("explode2.booster.BoosterMainKt")

    tasks.run.get().apply {
        // set working directory of task "run" to "[rootProjectDir]/run".
        workingDir = rootProject.projectDir.resolve("run").apply { this.mkdirs() }
        // set input for console
        standardInput = System.`in`
    }
}

tasks.shadowJar {
    mergeServiceFiles()
    transform(com.github.jengelman.gradle.plugins.shadow.transformers.Log4j2PluginsCacheFileTransformer::class.java)
}