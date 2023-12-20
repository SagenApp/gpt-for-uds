plugins {
    id("java")
    id("org.graalvm.buildtools.native") version "0.9.28"
}

group = "app.sagen"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.code.gson:gson:2.10.1")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

// pull the version from the 'version' property or default to '0.0.1-SNAPSHOT'
val versionFromProperties = findProperty("version")?.toString() ?: "0.0.1-SNAPSHOT"

// Set the project version
version = versionFromProperties

tasks.withType<Jar> {
    archiveVersion.set(versionFromProperties)
    // Other Jar configuration settings
}

graalvmNative {
    binaries {
        named("main") {
            imageName.set("gpt-for-uds")
            mainClass.set("app.sagen.chatgptclient.UnixSocketServer")
            buildArgs.add("-O4")
            buildArgs.add("--no-fallback")
            buildArgs.add("--no-server")
            buildArgs.add("-H:ReflectionConfigurationFiles=${projectDir.toPath().resolve("reflection-config.json")}")
            debug.set(true)
            verbose.set(true)
            richOutput.set(true)

            javaLauncher.set(javaToolchains.launcherFor {
                languageVersion.set(JavaLanguageVersion.of(17))
            })
        }
    }
}
