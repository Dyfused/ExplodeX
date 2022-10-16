plugins {
    java
    kotlin("jvm") version "1.7.20"
    application
}

group = "explode"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":booster"))
    implementation(project(":booster-graphql"))
    implementation(project(":booster-resource"))
    implementation(project(":labyrinth"))
    implementation(project(":labyrinth-mongodb"))
    implementation(project(":booster-maintain"))

    implementation(project(":gatekeeper"))

    testImplementation(kotlin("test"))
}

application {
    mainClass.set("explode2.booster.BoosterMainKt")
}

allprojects {
    repositories {
        mavenCentral()
        maven("https://jitpack.io")
        maven("https://libraries.minecraft.net") {
            mavenContent {
                includeGroup("com.mojang")
            }
        }
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
}