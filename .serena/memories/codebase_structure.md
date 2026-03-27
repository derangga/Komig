# Codebase Structure

```
KomigSample/
├── komig/                          # KMP image compression library
│   └── src/
│       ├── commonMain/kotlin/com/komig/
│       │   ├── Komig.kt            # Entry point (object with suspend compress())
│       │   ├── CompressionConfig.kt # Builder DSL for quality, format, resize
│       │   ├── CompressionResult.kt # Data class with compressed output
│       │   ├── ImageCompressor.kt   # expect class for platform compression
│       │   ├── OutputFormat.kt      # Enum: JPEG, PNG, WEBP, AUTO
│       │   ├── ResizeMode.kt        # Sealed class: FitInside, ExactResize, Percentage
│       │   ├── KomigException.kt    # Sealed exception hierarchy
│       │   ├── FormatDetector.kt    # Magic-byte format detection
│       │   └── Platform.kt          # expect/actual platform info
│       ├── androidMain/kotlin/com/komig/
│       │   ├── ImageCompressor.android.kt  # Android actual implementation
│       │   └── Platform.android.kt
│       ├── iosMain/kotlin/com/komig/
│       │   ├── ImageCompressor.ios.kt      # iOS actual implementation
│       │   └── Platform.ios.kt
│       ├── androidHostTest/         # Android unit tests
│       └── androidDeviceTest/       # Android instrumented tests
├── composeApp/                     # Compose Multiplatform sample app
│   └── src/
│       ├── commonMain/kotlin/com/komig/sample/
│       │   ├── App.kt              # Root composable
│       │   ├── CompressScreen.kt   # Main UI screen
│       │   ├── CompressViewModel.kt # MVVM ViewModel with StateFlow
│       │   ├── ImagePicker.kt      # expect image picker abstraction
│       │   └── ByteArrayImage.kt   # expect ByteArray-to-image composable
│       ├── androidMain/            # Android actuals + MainActivity
│       └── iosMain/                # iOS actuals + MainViewController
├── iosApp/                         # iOS Xcode project wrapper
├── gradle/libs.versions.toml      # Version catalog
├── build.gradle.kts               # Root build file
└── settings.gradle.kts            # Includes :composeApp and :komig
```
