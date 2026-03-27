# Codebase Structure

```
KomigSample/
├── build.gradle.kts              # Root build script (plugin declarations)
├── settings.gradle.kts           # Includes :composeApp and :komig modules
├── gradle.properties             # Kotlin/Gradle/Android config
├── gradle/libs.versions.toml     # Version catalog
├── composeApp/                   # Main application module (KMP + Compose)
│   ├── build.gradle.kts
│   └── src/
│       ├── commonMain/kotlin/com/komig/sample/
│       │   ├── App.kt            # Main Composable UI
│       │   ├── Greeting.kt       # Greeting class
│       │   └── Platform.kt       # expect declarations
│       ├── commonTest/kotlin/com/komig/sample/
│       │   └── ComposeAppCommonTest.kt
│       ├── androidMain/kotlin/com/komig/sample/
│       │   ├── MainActivity.kt   # Android entry point
│       │   └── Platform.android.kt
│       └── iosMain/kotlin/com/komig/sample/
│           ├── MainViewController.kt  # iOS entry point
│           └── Platform.ios.kt
├── komig/                        # KMP library module
│   ├── build.gradle.kts
│   └── src/
│       ├── commonMain/kotlin/com/komig/
│       │   └── Platform.kt       # expect fun platform(): String
│       ├── androidMain/kotlin/com/komig/
│       │   └── Platform.android.kt
│       ├── iosMain/kotlin/com/komig/
│       │   └── Platform.ios.kt
│       ├── androidHostTest/      # Unit tests
│       └── androidDeviceTest/    # Instrumented tests
└── iosApp/                       # iOS app (Xcode project / SwiftUI entry)
```
