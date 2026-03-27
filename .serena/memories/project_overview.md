# KomigSample - Project Overview

## Purpose
KomigSample is a **Kotlin Multiplatform (KMP)** sample project targeting **Android** and **iOS**.
It demonstrates the use of Compose Multiplatform for shared UI across platforms.

## Modules
- **composeApp** — The main application module with shared Compose UI code and platform-specific entry points.
  - Package: `com.komig.sample`
  - Android namespace: `com.komig.sample`
- **komig** — A KMP library module (produces an Android library + iOS XCFramework named `komigKit`).
  - Package: `com.komig`
  - Android namespace: `com.komig`

## Tech Stack
- **Kotlin**: 2.3.0
- **Compose Multiplatform**: 1.10.0
- **Android Gradle Plugin (AGP)**: 8.11.2
- **Gradle**: with Kotlin DSL (`build.gradle.kts`), version catalog (`gradle/libs.versions.toml`)
- **Android**: compileSdk 36, minSdk 26, targetSdk 36, Java 11 compatibility
- **iOS targets**: iosArm64, iosSimulatorArm64 (composeApp), + iosX64 (komig)
- **Jetpack Compose** (Material3), AndroidX Activity, AndroidX Lifecycle (ViewModel + Runtime)

## Key Configuration
- `kotlin.code.style=official`
- Gradle configuration cache and build cache enabled
- `TYPESAFE_PROJECT_ACCESSORS` feature preview enabled
- Non-transitive R classes (`android.nonTransitiveRClass=true`)
