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
result.bytes          // compressed output as ByteArray
result.width          // output width
result.height         // output height
result.format         // output format
result.inputSizeBytes // original size
result.outputSizeBytes // compressed size
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

**Save to file (Android)**

```kotlin
val file = File(context.cacheDir, "compressed.webp")
file.writeBytes(result.bytes)
```

**Save to file (iOS — shared Kotlin code)**

```kotlin
import platform.Foundation.NSData
import platform.Foundation.writeToFile

val nsData = result.bytes.toNSData()
nsData.writeToFile("/path/to/compressed.webp", atomically = true)
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

TBD
