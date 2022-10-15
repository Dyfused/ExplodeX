plugins {
    kotlin("jvm")
}

group = "explode"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    api(project(":booster"))
    api(project(":labyrinth"))

    // graphql
    api("com.expediagroup:graphql-kotlin-server:6.2.5")

    // graphql-scalars
    api("com.graphql-java:graphql-java-extended-scalars:19.0")

    // guava
    api("com.google.guava:guava:31.1-jre")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}