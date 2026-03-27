# Code Style and Conventions

## General
- **Kotlin code style**: `official` (as declared in `gradle.properties`)
- **Language**: Kotlin (no Java source files in the project)
- **Build scripts**: Kotlin DSL (`.gradle.kts`)

## Naming Conventions
- Packages: lowercase dot-separated (`com.komig.sample`, `com.komig`)
- Classes: PascalCase (`Greeting`, `MainActivity`)
- Functions: camelCase (`greet()`, `getPlatform()`)
- Composable functions: PascalCase (`App()`, `AppAndroidPreview()`)
- Platform-specific files: `<Name>.<platform>.kt` (e.g., `Platform.android.kt`, `Platform.ios.kt`)

## Architecture Patterns
- **expect/actual** pattern for platform-specific implementations (see `Platform.kt` / `Platform.android.kt` / `Platform.ios.kt`)
- Compose Multiplatform shared UI in `commonMain`, platform entry points in `androidMain` / `iosMain`
- Single-Activity architecture on Android (`MainActivity` using `setContent`)

## Compose Conventions
- `@Composable` and `@Preview` annotations used together for preview-able composables
- Material3 theming via `MaterialTheme {}`
- Modifier chaining with trailing lambda style

## Dependencies
- Managed via Gradle Version Catalog (`gradle/libs.versions.toml`)
- Referenced as `libs.plugins.*` and `libs.*` in build scripts
