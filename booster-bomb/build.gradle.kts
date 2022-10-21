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
    implementation(project(":labyrinth"))

    // ktor shits
    implementation("io.ktor:ktor-server-cors:2.1.2")
    implementation("io.ktor:ktor-server-auth:2.1.2")
    implementation("io.ktor:ktor-server-status-pages:2.1.2")
    implementation("io.ktor:ktor-server-content-negotiation:2.1.2")
    implementation("io.ktor:ktor-serialization-gson:2.1.2")

    // gson (the most lenient json serializer, fuck off kotlinx.serialization!)
    implementation("com.google.code.gson:gson:2.9.0")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}