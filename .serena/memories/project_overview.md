# KomigSample - Project Overview

**KomigSample** is a Kotlin Multiplatform (KMP) project that serves as both a library and a sample application for image compression.

## Modules

1. **`komig`** — A Kotlin Multiplatform library for image compression. Provides a DSL-based API (`Komig.compress`) supporting JPEG, PNG, and WEBP formats with configurable quality, resizing (FitInside, ExactResize, Percentage), and auto-format detection from magic bytes. Uses `expect/actual` for platform-specific `ImageCompressor` implementations (Android uses Android bitmap APIs, iOS uses native APIs).

2. **`composeApp`** — A Compose Multiplatform sample app demonstrating the `komig` library. Targets Android and iOS (iosArm64, iosSimulatorArm64). Uses Material3, Compose resources, ViewModel + StateFlow for state management, and a custom image picker (expect/actual).

## Tech Stack
- **Language**: Kotlin 2.3.0
- **Build system**: Gradle with Kotlin DSL, version catalogs (`libs.versions.toml`)
- **UI**: Compose Multiplatform 1.10.0 with Material3
- **Architecture**: MVVM (ViewModel + StateFlow)
- **Concurrency**: Kotlin Coroutines 1.10.1
- **Platforms**: Android (minSdk 26, compileSdk/targetSdk 36), iOS (arm64, simulatorArm64; iosX64 for library only)
- **AGP**: 8.11.2
- **JVM Target**: 11

## Key API
```kotlin
val result = Komig.compress(imageBytes) {
    quality(80)
    format(OutputFormat.WEBP)
    maxResolution(1920, 1080)
}
```
Returns `CompressionResult` with compressed bytes, dimensions, format, and size info.
