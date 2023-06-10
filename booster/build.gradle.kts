plugins {
    java
    kotlin("jvm")
}

group = "explode"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://jitpack.io") {
        mavenContent {
            includeGroup("com.github")
        }
    }
}

dependencies {
    // providing logging
    api("org.slf4j:slf4j-api:2.0.5")
    api("org.apache.logging.log4j:log4j-api:2.20.0")
    implementation("org.apache.logging.log4j:log4j-core:2.20.0")
    implementation("org.apache.logging.log4j:log4j-slf4j2-impl:2.20.0")

    // jline & console
    api("org.jline:jline:3.23.0")
    api("org.jline:jline-terminal-jansi:3.23.0")
    api("net.minecrell:terminalconsoleappender:1.3.0")
    implementation("org.fusesource.jansi:jansi:2.4.0")

    // providing configuring
    api("com.github.Taskeren:TConfig:1.2")

    // providing eventbus
    api("org.greenrobot:eventbus-java:3.3.1")

    // providing ktor/http-server
    api("io.ktor:ktor-server-core:2.1.2")
    api("io.ktor:ktor-server-netty:2.1.2")
    api("io.ktor:ktor-server-cors:2.1.2")
    api("io.ktor:ktor-server-auth:2.1.2")
    api("io.ktor:ktor-server-status-pages:2.1.2")
    api("io.ktor:ktor-server-content-negotiation:2.1.2")
    api("io.ktor:ktor-serialization-gson:2.1.2")

    // providing dependency injection
    api("io.insert-koin:koin-core:3.4.2")
    api("io.insert-koin:koin-ktor:3.4.1")
    api("io.insert-koin:koin-logger-slf4j:3.4.1")

    // providing classloading
    api("io.github.lxgaming:classloaderutils:1.0.1")

    // graphql
    api("com.expediagroup:graphql-kotlin-server:6.2.5")

    // graphql-scalars
    api("com.graphql-java:graphql-java-extended-scalars:20.0")

    // guava
    api("com.google.guava:guava:31.1-jre")

    // gson
    api("com.google.code.gson:gson:2.10.1")

    // project requirements
    api(project(":gateau"))
    api(project(":resource"))
    api(project(":labyrinth"))

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}