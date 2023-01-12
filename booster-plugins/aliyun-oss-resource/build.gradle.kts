plugins {
	kotlin("jvm")
	id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "explode"
version = "1.0.0"

repositories {
	mavenCentral()
}

dependencies {
	compileOnly(project(":booster-resource"))

	implementation("com.aliyun.oss:aliyun-sdk-oss:3.16.0")
}