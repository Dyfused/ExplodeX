plugins {
    kotlin("jvm")
}

group = "explode"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    api(project(":gateau"))
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}