# Komig — Agent Instructions

## Why

Komig is a Kotlin Multiplatform (KMP) image compression library for Android and iOS. It provides a coroutine-based DSL for compressing, resizing, and converting images. The repo also contains a Compose Multiplatform sample app (`composeApp`) that demonstrates the library.

## What

Two modules:

- **`komig/`** — The library. Common API in `commonMain`, platform implementations in `androidMain` (BitmapFactory) and `iosMain` (UIImage/CGImage). Package: `com.komig`.
- **`composeApp/`** — Sample app using Compose Multiplatform with image picker + compression UI.

Key entry point: `Komig.compress(input) { quality(80); format(OutputFormat.WEBP) }` — see `komig/src/commonMain/kotlin/com/komig/Komig.kt`.

## How

### Build & Verify

```bash
./gradlew :komig:build                           # Build library
./gradlew :composeApp:assembleDebug              # Build sample app
```

### Tests

```bash
./gradlew :komig:testDebugUnitTest               # commonTest + androidHostTest (JVM)
./gradlew :komig:connectedDebugAndroidTest       # androidDeviceTest (requires emulator/device)
./gradlew :komig:iosSimulatorArm64Test           # iosTest (requires Xcode + simulator)
```

**Important**: `androidHostTest` cannot test real compression — `BitmapFactory` returns null on host JVM. Pure logic tests belong in `commonTest`, platform compression tests in `androidDeviceTest` or `iosTest`.

### Lint & Format

```bash
./gradlew :komig:lint                            # Android lint
```

## Project Conventions

- All library classes use the `com.komig` package
- `internal` visibility for platform details (`ImageCompressor`, `FormatDetector`)
- `expect`/`actual` pattern for platform-specific implementations
- Suspend functions for compression operations, dispatched on `Dispatchers.Default`
- Kotlin 2.3.0, AGP 8.11.2, Compose Multiplatform 1.10.0, Coroutines 1.10.1
- Android: minSdk 26, compileSdk 36
- iOS: iosX64, iosArm64, iosSimulatorArm64 (framework name: `komigKit`)

## Detailed Context

Before starting work, read the relevant file(s) from `docs/`:

| File | When to read |
|---|---|
| `docs/CONTEXT.md` | Full architecture, API reference, file tree, test structure, platform details |
| `docs/TASKS.md` | Phase-based task breakdown with completion status |
| `docs/ADR-001-komig-architecture.md` | Architecture decisions and rationale |
