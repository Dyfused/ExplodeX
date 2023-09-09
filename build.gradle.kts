import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.incremental.mkdirsOrThrow

plugins {
    java
    kotlin("jvm") version "1.7.20"
    application
    id("com.github.johnrengelman.shadow") version "7.1.2" apply false

	id("com.palantir.git-version") version "1.0.0"
}

group = "explode"
version = "3.0.0"

allprojects {
	if(!this.name.startsWith("explode")) {
		tasks.withType<ShadowJar> {
			dependencies {
				exclude(project(":booster"))
				exclude(project(":resource"))
				exclude(project(":labyrinth"))
				exclude(project(":labyrinth-mongodb"))
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
    implementation(project(":resource"))
    implementation(project(":labyrinth"))
    implementation(project(":labyrinth-mongodb"))

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
		maven {
			name = "Taskeren Repo Snapshot"
			url = uri("http://play.elytra.cn:31055/snapshots")
			isAllowInsecureProtocol = true
		}
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

	tasks.withType<Jar> {
		duplicatesStrategy = DuplicatesStrategy.EXCLUDE
	}
}

tasks.build {
	dependsOn(tasks["gatherArtifact"])
}

val gitVersion: groovy.lang.Closure<String> by extra

task("gatherArtifact") {
	dependsOn(":explode-all:shadowJar", ":explode-proxy:shadowJar")

	doLast {
		logger.info("building artifact")

		// ensure that the output dir is present
		val outputDir = file("build/gather-builds").apply(File::mkdirsOrThrow)

		val gitVer = runCatching { gitVersion() }.getOrElse { "NO-GIT-TAG" }

		subprojects.forEach { p ->
			println("collecting project: $p")
			runCatching {
				p.buildDir.resolve("libs").copyRecursively(outputDir.resolve(gitVer).resolve(p.name), true)
			}.onFailure {
				logger.warn("Unable to copy ${p.buildDir}/libs to ${outputDir.resolve(gitVer).resolve(p.name)}", it)
			}
		}
	}
}