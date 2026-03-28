# Komig — Project Context

## Project Overview

**Komig** is a Kotlin Multiplatform (KMP) image compression library targeting Android and iOS. It provides a coroutine-based DSL API for compressing, resizing, and converting images. The project includes a Compose Multiplatform sample app (`composeApp`) demonstrating the library.

- **Kotlin**: 2.3.0
- **AGP**: 8.11.2
- **Compose Multiplatform**: 1.10.0
- **Coroutines**: 1.10.1
- **Android**: minSdk 26, compileSdk 36
- **iOS Targets**: iosX64, iosArm64, iosSimulatorArm64
- **Framework name**: `komigKit`

---

## Module Structure

```
KomigSample/
├── komig/                          # Library module (KMP)
│   ├── build.gradle.kts
│   └── src/
│       ├── commonMain/kotlin/com/komig/
│       │   ├── CompressionConfig.kt    # DSL builder + validation
│       │   ├── CompressionResult.kt    # Output data class
│       │   ├── FormatDetector.kt       # Magic-byte format detection (internal)
│       │   ├── ImageCompressor.kt      # expect class (internal)
│       │   ├── KomigException.kt       # Sealed exception hierarchy
│       │   ├── Komig.kt               # Entry point singleton
│       │   ├── OutputFormat.kt         # JPEG, PNG, WEBP, AUTO enum
│       │   ├── Platform.kt            # expect fun platform()
│       │   └── ResizeMode.kt          # Sealed class: FitInside, ExactResize, Percentage
│       ├── commonTest/kotlin/com/komig/
│       │   ├── TestFixtures.kt         # Minimal 1x1 JPEG/PNG/WebP byte arrays
│       │   ├── CompressionConfigTest.kt
│       │   ├── FormatDetectorTest.kt
│       │   ├── KomigTest.kt           # Error path tests (validation before platform)
│       │   └── ResizeModeTest.kt
│       ├── androidMain/kotlin/com/komig/
│       │   ├── ImageCompressor.android.kt  # BitmapFactory + Bitmap.compress
│       │   └── Platform.android.kt
│       ├── androidHostTest/kotlin/com/komig/
│       │   └── ExampleUnitTest.kt      # Placeholder (2+2=4), to be deleted
│       ├── androidDeviceTest/kotlin/com/komig/
│       │   ├── AndroidTestImages.kt    # On-device image generator using Bitmap API
│       │   ├── CompressionRoundTripTest.kt
│       │   └── DeviceIntegrationTest.kt
│       ├── iosMain/kotlin/com/komig/
│       │   ├── ImageCompressor.ios.kt  # UIImage + CGImage + UIGraphics
│       │   └── Platform.ios.kt
│       └── iosTest/kotlin/com/komig/
│           └── IosCompressionTest.kt
├── composeApp/                     # Sample app module (Compose Multiplatform)
│   └── src/commonMain/composeResources/drawable/
│       └── frieren.jpg             # Test image (542 KB)
├── gradle/libs.versions.toml      # Version catalog
├── settings.gradle.kts
└── docs/
    ├── ADR-001-komig-architecture.md
    ├── TASKS.md                    # Phase-based task breakdown
    └── CONTEXT.md                  # This file
```

---

## API Classes (commonMain)

### `Komig` — Entry Point
```kotlin
object Komig {
    suspend fun compress(
        input: ByteArray,
        block: CompressionConfig.Builder.() -> Unit = {}
    ): CompressionResult
}
```
- Resolves `AUTO` format via `FormatDetector.detect()` (falls back to exception if undetectable)
- Dispatches compression on `Dispatchers.Default`
- Throws `KomigException` subtypes on errors

### `CompressionConfig` — Builder DSL
- `quality(value: Int)` — 0..100, default 80
- `format(value: OutputFormat)` — default AUTO
- `maxResolution(maxWidth, maxHeight)` — shorthand for `ResizeMode.FitInside`
- `resize(mode: ResizeMode)` — explicit resize mode
- `build()` is `internal` — validates all constraints, throws `InvalidConfigException`

### `OutputFormat` — Enum
`JPEG`, `PNG`, `WEBP`, `AUTO`

### `ResizeMode` — Sealed Class
- `FitInside(maxWidth: Int, maxHeight: Int)` — preserves aspect ratio, no upscaling
- `ExactResize(width: Int, height: Int)` — ignores aspect ratio
- `Percentage(factor: Float)` — e.g. 0.5 = half size

### `CompressionResult` — Data Class
`bytes: ByteArray`, `width: Int`, `height: Int`, `format: OutputFormat`, `inputSizeBytes: Long`, `outputSizeBytes: Long`
- Custom `equals`/`hashCode` for `ByteArray` content comparison

### `KomigException` — Sealed Exception
- `UnsupportedFormatException` — format not recognized
- `DecodingException` — platform codec decode failure
- `EncodingException` — platform codec encode failure
- `InvalidConfigException` — invalid config values

### `FormatDetector` — Internal Object
Detects format from magic bytes:
- JPEG: `FF D8 FF` (3 bytes)
- PNG: `89 50 4E 47` (4 bytes)
- WebP: `RIFF` + 4-byte size + `WEBP` (12 bytes)
- Returns `null` for unknown

---

## Platform Implementations

### Android (`ImageCompressor.android.kt`, 160 lines)
1. `decodeBounds()` — reads dimensions via `BitmapFactory.Options.inJustDecodeBounds`
2. `computeSampleSize()` — optimal power-of-2 subsample for `FitInside`
3. `decodeBitmap()` — full decode with `inSampleSize` + `ARGB_8888`
4. `applyResize()` — `Bitmap.createScaledBitmap` for all resize modes
5. `encode()` — `Bitmap.compress(format, quality, stream)`
6. `mapFormat()` — `WEBP_LOSSY` for API >= R, deprecated `WEBP` below
7. Memory: `Bitmap.recycle()` in `finally` blocks

### iOS (`ImageCompressor.ios.kt`, 135 lines)
1. Decode: `UIImage.imageWithData(NSData)`
2. Dimensions: `CGImageGetWidth/Height`
3. `computeTargetSize()` — same logic as Android for all resize modes
4. `resizeUIImage()` — `UIGraphicsBeginImageContextWithOptions` + `drawInRect`
5. Encode JPEG: `UIImageJPEGRepresentation(image, quality/100.0)`
6. Encode PNG: `UIImagePNGRepresentation(image)`
7. **WebP: falls back to JPEG** (no built-in iOS WebP encoder)
8. `ByteArray.toNSData()` / `NSData.toByteArray()` via `usePinned` + `memcpy`

---

## Build Configuration

### `komig/build.gradle.kts`
- Plugins: `kotlinMultiplatform`, `androidKotlinMultiplatformLibrary`, `androidLint`
- Compiler flag: `-Xexpect-actual-classes`
- `withHostTestBuilder {}` — androidHostTest (JVM-based, no real Android runtime)
- `withDeviceTestBuilder { sourceSetTreeName = "test" }` — androidDeviceTest (real device/emulator)
- Instrumentation runner: `androidx.test.runner.AndroidJUnitRunner`

### Source Set Dependencies
| Source Set | Dependencies |
|---|---|
| commonMain | kotlin-stdlib 2.3.0, kotlinx-coroutines-core 1.10.1 |
| commonTest | kotlin-test, kotlinx-coroutines-test |
| androidMain | androidx-core-ktx |
| androidDeviceTest | androidx-runner 1.5.2, androidx-core 1.5.0, androidx-testExt-junit 1.3.0 |
| iosMain | (none beyond common) |

---

## Test Architecture

### Key Constraint: androidHostTest is NOT useful
`BitmapFactory` returns `null` on host JVM without Robolectric. All pure-logic tests go to `commonTest`, all platform compression tests go to `androidDeviceTest`.

### commonTest (pure logic, no platform)
- **TestFixtures.kt** — Minimal valid 1x1 images as `ByteArray` constants (JPEG ~230 bytes, PNG ~83 bytes, WebP ~34 bytes), plus corrupt/empty/unknown helpers
- **CompressionConfigTest.kt** — 18 tests: defaults, custom values, boundary values, validation errors
- **FormatDetectorTest.kt** — 9 tests: detect JPEG/PNG/WebP, null for unknown/empty/short/corrupt
- **KomigTest.kt** — 3 tests: invalid quality, AUTO with unknown/empty bytes (error paths before platform)
- **ResizeModeTest.kt** — 12 tests: construction, equality, copy, type checking

### androidDeviceTest (real Android runtime)
- **AndroidTestImages.kt** — Helper that generates valid encoded images on-device using `Bitmap.createBitmap` + `Bitmap.compress`. Creates JPEG, PNG, WebP, and large JPEG (1920x1080).
- **CompressionRoundTripTest.kt** — 13 tests: JPEG/PNG/WebP round-trip, AUTO format preservation, quality affects size, format conversion, all resize modes, result field correctness
- **DeviceIntegrationTest.kt** — 5 tests: large image FitInside, corrupt data, explicit format skips detection, quality edge cases (1 and 100)

### iosTest (iOS simulator)
- **IosCompressionTest.kt** — 8 tests: JPEG round-trip, AUTO detection, FitInside/ExactResize/Percentage, corrupt data, WebP fallback behavior. Uses `TestFixtures.MINIMAL_JPEG` since there's no on-device Bitmap API equivalent.

### Test Commands
```bash
./gradlew :komig:testDebugUnitTest              # commonTest + androidHostTest on JVM
./gradlew :komig:connectedDebugAndroidTest       # androidDeviceTest on emulator/device
./gradlew :komig:iosSimulatorArm64Test           # iOS tests on simulator (requires Xcode)
```
