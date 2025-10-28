pluginManagement {
    repositories {
        maven {
            name = "NeoForged"
            url = uri("https://maven.neoforged.net/releases")
        }
        maven {
            name = "ModPublisher"
            url = uri("https://maven.firstdarkdev.xyz/releases")
        }
        gradlePluginPortal()
    }
}

plugins {
    id ("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0"
}

rootProject.name = "ae2-pattern-encoding-access-terminal"
