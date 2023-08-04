import io.gitlab.arturbosch.detekt.Detekt

val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val koin_version: String by project

plugins {
    application
    idea
    kotlin("jvm") version "1.9.0"
    id("io.ktor.plugin") version "2.3.2"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.8.0"
    id("io.gitlab.arturbosch.detekt") version "1.23.1"
}

group = "com.jicay.ranking"
version = "0.0.1"
application {
    mainClass.set("com.jicay.ranking.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

kotlin {
    jvmToolchain {
        this.languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenCentral()
    maven { setUrl("https://jitpack.io") }
}

sourceSets {
    create("testIntegration") {
        compileClasspath += sourceSets.main.get().output
        runtimeClasspath += sourceSets.main.get().output
    }

    create("testArchitecture") {
        compileClasspath += sourceSets.main.get().output
        runtimeClasspath += sourceSets.main.get().output
    }
}

val testIntegrationImplementation: Configuration by configurations.getting {
    extendsFrom(configurations.implementation.get())
}

val testArchitectureImplementation: Configuration by configurations.getting {
    extendsFrom(configurations.implementation.get())
}

dependencies {
    // Ktor
    implementation("io.ktor:ktor-server-core-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-netty-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
    implementation("io.ktor:ktor-server-cors:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("io.ktor:ktor-server-swagger:$ktor_version")
    implementation("io.ktor:ktor-server-openapi:$ktor_version")

    // Koin
    implementation("io.insert-koin:koin-ktor:$koin_version")
    implementation("io.insert-koin:koin-logger-slf4j:$koin_version")

    // Mongo
    implementation("org.litote.kmongo:kmongo:4.10.0")

    // Liquibase
    implementation("org.liquibase:liquibase-core:4.23.0")
    implementation("org.liquibase.ext:liquibase-mongodb:4.23.0")

    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
    testImplementation("io.mockk:mockk:1.13.5")
    testImplementation("org.assertj:assertj-core:3.18.0")

    testIntegrationImplementation("io.ktor:ktor-server-tests-jvm:$ktor_version")
    testIntegrationImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
    testIntegrationImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
    testIntegrationImplementation("org.assertj:assertj-core:3.18.0")
    testIntegrationImplementation("io.ktor:ktor-client-content-negotiation:$ktor_version")
    testIntegrationImplementation("org.testcontainers:mongodb:1.17.6")

    testArchitectureImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
    testArchitectureImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
    // ArchUnit
    testArchitectureImplementation("com.tngtech.archunit:archunit-junit5:1.0.1")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

task<Test>("testIntegration") {
    useJUnitPlatform()
    testClassesDirs = sourceSets["testIntegration"].output.classesDirs
    classpath = sourceSets["testIntegration"].runtimeClasspath
}

task<Test>("testArchitecture") {
    useJUnitPlatform()
    testClassesDirs = sourceSets["testArchitecture"].output.classesDirs
    classpath = sourceSets["testArchitecture"].runtimeClasspath
}


idea {
    module {
        sourceDirs.removeAll(sourceSets["testIntegration"].allJava.srcDirs)
        sourceDirs.removeAll(sourceSets["testIntegration"].resources.srcDirs)
        testSources.setFrom(sourceSets["testIntegration"].allJava.srcDirs)
        testSources.setFrom(sourceSets["testIntegration"].resources.srcDirs)

        sourceDirs.removeAll(sourceSets["testArchitecture"].allJava.srcDirs)
        sourceDirs.removeAll(sourceSets["testArchitecture"].resources.srcDirs)
        testSources.setFrom(sourceSets["testArchitecture"].allJava.srcDirs)
        testSources.setFrom(sourceSets["testArchitecture"].resources.srcDirs)
    }
}

detekt {
    buildUponDefaultConfig = true // preconfigure defaults
    allRules = false // activate all available (even unstable) rules.
    config.setFrom("$projectDir/config/detekt.yml") // point to your custom config defining rules to run, overwriting default behavior
    baseline = file("$projectDir/config/baseline.xml") // a way of suppressing issues before introducing detekt
}

tasks.withType<Detekt>().configureEach {
    reports {
        html.required.set(true) // observe findings in your browser with structure and code snippets
        xml.required.set(true) // checkstyle like format mainly for integrations like Jenkins
        txt.required.set(true) // similar to the console output, contains issue signature to manually edit baseline files
        sarif.required.set(true) // standardized SARIF format (https://sarifweb.azurewebsites.net/) to support integrations with GitHub Code Scanning
        md.required.set(true) // simple Markdown format
    }
}
