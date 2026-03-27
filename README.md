# Komig

**Kotlin cOMpressor ImaGe** — A pure Kotlin Multiplatform image compression library targeting Android and iOS.

> **Status**: Work in Progress

## Overview

Komig provides a simple, idiomatic Kotlin API for compressing images with control over quality, format, and resolution. It uses platform-native codecs under the hood (`BitmapFactory`/`Bitmap.compress` on Android, `UIImage`/`UIKit` on iOS) while exposing a unified DSL in shared code.

### Supported Platforms

| Platform | Min Version |
|----------|-------------|
| Android  | API 26 (Oreo) |
| iOS      | 15+ |

### Supported Formats

JPEG, PNG, WebP

### Usage

```kotlin
// Minimal — sensible defaults (quality 80, preserve input format)
val result = Komig.compress(imageBytes)

// Full DSL
val result = Komig.compress(imageBytes) {
    quality(80)
    format(OutputFormat.WEBP)
    maxResolution(1920, 1080)
}

// Result
result.bytes          // compressed output
result.width          // output width
result.height         // output height
result.format         // output format
result.inputSizeBytes // original size
result.outputSizeBytes // compressed size
```

### Architecture

Pure Kotlin Multiplatform with `expect`/`actual` pattern. Shared logic (config, DSL, pipeline orchestration) lives in `commonMain`. Platform-specific codec work lives in `androidMain` and `iosMain`.

```
komig/src/
├── commonMain/    ← API, DSL, config, expect declarations
├── androidMain/   ← BitmapFactory + Bitmap.compress
└── iosMain/       ← UIImage + UIKit encoding
```

## License

TBD
