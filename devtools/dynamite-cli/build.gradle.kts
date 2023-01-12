plugins {
    kotlin("jvm") version "1.7.20"
}

group = "explode"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    val ktorVer = "2.2.1"
    fun ktor(moduleName: String) = implementation("io.ktor:${moduleName}:${ktorVer}")
    ktor("ktor-client-core")
    ktor("ktor-client-okhttp")
    ktor("ktor-client-content-negotiation")
    ktor("ktor-serialization-gson")
    implementation("com.google.code.gson:gson:2.10")

    testImplementation(kotlin("test"))
}