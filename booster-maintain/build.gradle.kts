plugins {
    kotlin("jvm")
}

group = "explode"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":booster"))
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}