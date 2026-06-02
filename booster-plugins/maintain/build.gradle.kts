plugins {
    kotlin("jvm")
}

group = "explode"
version = "1.0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":booster"))
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}