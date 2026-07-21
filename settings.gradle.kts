/*
 * Copyright (C) 2026 Maria Taylor
 * SPDX-License-Identifier: GPL-3.0-only
 */

plugins {
    // Apply the foojay-resolver plugin to allow automatic download of JDKs
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "MVillageNamer"
include("src")
