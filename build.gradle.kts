plugins {
    kotlin("jvm") version "1.8.0"
    application
}

repositories {
    mavenCentral()
    maven("https://packages.confluent.io/maven")
    maven("https://jitpack.io")
}

dependencies {
    testImplementation(kotlin("test"))

    implementation("com.github.navikt:rapids-and-rivers:2023022210271677058038.ec07e03eceb6")
    implementation("io.github.microutils:kotlin-logging:3.0.4")
    implementation("com.natpryce:konfig:1.6.10.0")
}

tasks.test {
    useJUnitPlatform()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

application {
    mainClass.set("no.nav.dagpenger.lovme.MainKt")
}
