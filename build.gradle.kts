plugins {
    id("java")
    id("org.graalvm.buildtools.native") version "0.9.9"
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

nativeBuild {
    imageName.set("my-native-app")
}

graalvmNative {
    binaries {
        named("main").configure {
            // Specific options for native-image can be set here
            buildArgs.addAll(listOf(
                "--no-fallback",
                "--no-server",
                "-O3",
                "-H:Name=my-native-app", // Desired name of the native executable
                "-H:Class=app.sagen.chatgptclient.UnixSocketServer", // Fully qualified name of your main class
                "-H:ReflectionConfigurationFiles=${projectDir.toPath().resolve("reflection-config.json")}", // Path to your reflection config file
                "-Duser.country=US",
                "-Duser.language=en",
                "-H:IncludeLocales=en"
            ))
        }
    }
}
