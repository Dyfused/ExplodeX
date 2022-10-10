plugins {
    java
    kotlin("jvm")
}

group = "explode"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    api(project(":labyrinth"))

    // providing logging
    api("org.slf4j:slf4j-api:2.0.3")
    api("org.apache.logging.log4j:log4j-api:2.19.0")
    implementation("org.apache.logging.log4j:log4j-core:2.19.0")
    implementation("org.apache.logging.log4j:log4j-slf4j2-impl:2.19.0")

    // jline & console
    api("org.jline:jline:3.21.0")
    api("org.jline:jline-terminal-jansi:3.21.0")
    api("net.minecrell:terminalconsoleappender:1.3.0")
    implementation("org.fusesource.jansi:jansi:2.4.0")

    // providing configuring
    api("com.electronwill.night-config:toml:3.6.6")

    // providing eventbus
    api("org.greenrobot:eventbus-java:3.3.1")

    // providing ktor/http-server
    api("io.ktor:ktor-server-core:2.1.2")
    api("io.ktor:ktor-server-netty:2.1.2")
    api("io.ktor:ktor-server-cors:2.1.2")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}