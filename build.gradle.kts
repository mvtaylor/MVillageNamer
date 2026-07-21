/*
 * Copyright (C) 2026 Maria Taylor
 * SPDX-License-Identifier: GPL-3.0-only
 */

plugins {
    // Apply the java-library plugin for API and implementation separation.
    `java-library`
    `maven-publish`

    // run-paper gradle plugin
    id("xyz.jpenilla.run-paper") version "3.0.2"
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()

    maven {
    name = "papermc"
    url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {

    compileOnly("io.papermc.paper:paper-api:26.1.2.build.+")
}

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

group = "gay.viktoria"
version = "1.4.1"

tasks.jar {
  manifest {
    attributes["paperweight-mappings-namespace"] = "mojang"
  }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}


tasks {
    // run-paper plugin
    runServer {
        // Configure the Minecraft version for the task.
        minecraftVersion("26.1.2")
  }
}
