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

## Installation

Add the dependency to your `build.gradle.kts`:

**Kotlin Multiplatform (commonMain)**

```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("io.github.derangga:komig:0.1.0")
        }
    }
}
```

**Android only**

```kotlin
dependencies {
    implementation("io.github.derangga:komig:0.1.0")
}
```

Make sure you have Maven Central in your repositories:

```kotlin
repositories {
    mavenCentral()
}
```

## Usage

### Compress from ByteArray

```kotlin
// Minimal — sensible defaults (quality 80, preserve input format)
val result = Komig.compress(imageBytes)

// Full DSL
val result = Komig.compress(imageBytes) {
    quality(80)
    format(OutputFormat.WEBP)
    maxResolution(1920, 1080)
}
```

### Compress from file path

Works on both Android and iOS — pass the absolute path to the image file:

```kotlin
// Minimal
val result = Komig.compress("/path/to/photo.jpg")

// Full DSL
val result = Komig.compress("/path/to/photo.jpg") {
    quality(80)
    format(OutputFormat.WEBP)
    maxResolution(1920, 1080)
}
```

### Result properties

```kotlin
result.bytes           // compressed output as ByteArray
result.width           // output width
result.height          // output height
result.format          // output format
result.inputSizeBytes  // original size in bytes
result.outputSizeBytes // compressed size in bytes
result.cachedPath      // file path if cacheResult() was called, null otherwise
result.isBytesAvailable // false after cacheResult() is called
```

### Caching the result

By default, the compressed image is kept in memory as `result.bytes`. If you want to persist it to disk and free the memory, call `cacheResult()`:

```kotlin
val result = Komig.compress("/path/to/photo.jpg") {
    quality(80)
    format(OutputFormat.WEBP)
}

// Write to disk and release bytes from memory
val cachedPath = result.cacheResult(
    directory = "/path/to/cache/dir",
    fileName = "photo_compressed",   // extension is appended automatically, e.g. "photo_compressed.webp"
)

// Retrieve the cached file path later
val path = result.cachedPath // "/path/to/cache/dir/photo_compressed.webp"
```

> [!WARNING]
> Calling `cacheResult()` **permanently releases** `result.bytes` from memory to reduce heap pressure.
> Any subsequent access to `result.bytes` after caching will throw an `IllegalStateException`.
> Use `result.isBytesAvailable` to check whether bytes are still in memory before accessing them.

```kotlin
val result = Komig.compress(imageBytes)

result.isBytesAvailable   // true
result.cacheResult("/cache/dir", "output")
result.isBytesAvailable   // false

result.bytes              // throws IllegalStateException: "Bytes have been released after caching. Use the cached file at: ..."
```

### What to do with the result

`result.bytes` is a standard `ByteArray` containing the compressed image. Here are common use cases:

**Display in Compose Multiplatform (Android)**

```kotlin
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.asImageBitmap

val bitmap = BitmapFactory.decodeByteArray(result.bytes, 0, result.bytes.size)
val imageBitmap = bitmap.asImageBitmap()

Image(bitmap = imageBitmap, contentDescription = "Compressed image")
```

**Display in Compose Multiplatform (iOS)**

```kotlin
import org.jetbrains.skia.Image
import androidx.compose.ui.graphics.toComposeImageBitmap

val imageBitmap = Image.makeFromEncoded(result.bytes).toComposeImageBitmap()

Image(bitmap = imageBitmap, contentDescription = "Compressed image")
```

**Save to file (cross-platform)**

```kotlin
// Works on both Android and iOS
val path = result.cacheResult(
    directory = context.cacheDir.absolutePath, // Android example; use NSCachesDirectory path on iOS
    fileName = "compressed",
)
```

**Upload to a server**

```kotlin
// result.bytes can be sent directly as a request body
httpClient.post("https://api.example.com/upload") {
    setBody(result.bytes)
    contentType(ContentType.Image.Any)
}
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

[MIT License](LICENSE)
