import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java
    kotlin("jvm") version "1.7.20"
    application
    id("com.github.johnrengelman.shadow") version "7.1.2" apply false
}

group = "explode"
version = "1.0-SNAPSHOT"

allprojects {
	if(!this.name.startsWith("explode")) {
		tasks.withType<ShadowJar> {
			dependencies {
				exclude(project(":booster"))
				exclude(project(":booster-graphql"))
				exclude(project(":booster-resource"))
				exclude(project(":labyrinth"))
				exclude(project(":labyrinth-mongodb"))
				exclude(project(":booster-maintain"))
				exclude(project(":gatekeeper"))

				exclude(dependency("org.jetbrains.kotlin:.*"))
			}
		}
	}
}

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