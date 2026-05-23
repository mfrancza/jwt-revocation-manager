import java.net.URI

val ktor_version = "3.5.0"
val kotlin_version = "2.2.21"
val logback_version = "1.5.32"
val prometeus_version = "1.15.11"
val koin_version = "4.1.1"
val exposedVersion = "1.3.0"

plugins {
    application
    kotlin("jvm") version "2.2.21"
    id("io.ktor.plugin") version "3.5.0"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.2.21"
    id("maven-publish")
}

group = "io.github.mfrancza.jwtrevocationmanager"
version = "1.1.0-SNAPSHOT"

kotlin {
    jvmToolchain(21)
}

application {
    mainClass.set("com.mfrancza.jwtrevocation.manager.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}


repositories {
    mavenLocal()
    maven {
        url = uri("https://maven.pkg.github.com/mfrancza/jwt-revocation-rules")
        credentials {
            username = System.getenv("GITHUB_ACTOR")
            password = System.getenv("GITHUB_TOKEN")
        }
    }
    maven {
        url = uri("https://maven.pkg.github.com/mfrancza/jwt-revocation-ktor-server-auth")
        credentials {
            username = System.getenv("GITHUB_ACTOR")
            password = System.getenv("GITHUB_TOKEN")
        }
    }
    mavenCentral()
}

dependencies {
    implementation("io.github.mfrancza:jwt-revocation-rules-jvm:1.2.0")
    implementation("io.github.mfrancza:jwt-revocation-ktor-server-auth:1.2.0")

    implementation("io.ktor:ktor-server-core-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-auth-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-auth-jwt-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-conditional-headers-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-compression-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-caching-headers-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-default-headers-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-call-logging-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-call-id-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-metrics-micrometer-jvm:$ktor_version")
    implementation("io.micrometer:micrometer-registry-prometheus:$prometeus_version")
    implementation("io.ktor:ktor-server-netty-jvm:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")

    testImplementation("io.ktor:ktor-client-content-negotiation:$ktor_version")
    testImplementation("io.ktor:ktor-client-auth:$ktor_version")
    testImplementation("io.ktor:ktor-server-test-host-jvm:$ktor_version")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")

    // Koin
    implementation("io.insert-koin:koin-ktor:$koin_version")
    implementation("io.insert-koin:koin-logger-slf4j:$koin_version")
    testImplementation("io.insert-koin:koin-test:$koin_version")

    //JDBCRuleStore
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.postgresql:postgresql:42.7.11")
    testImplementation("com.h2database:h2:2.3.232")
}

publishing {
    publications {
        create<MavenPublication>("jwt-revocation-manager") {
            from(components["java"])
        }
    }
    repositories {
        maven {
            name = "GitHubPackages"
            url = URI("https://maven.pkg.github.com/mfrancza/jwt-revocation-manager")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}