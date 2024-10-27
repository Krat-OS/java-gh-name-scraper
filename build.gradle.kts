plugins {
    kotlin("jvm") version "2.0.0"
}

group = "krat_os.name_scraper"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.kohsuke:github-api:1.315")
    implementation("com.github.javaparser:javaparser-core:3.25.8")

    // Latest stable version compatible with Kotlin 2.0.0
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.8.0")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}
