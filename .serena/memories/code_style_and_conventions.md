# Code Style and Conventions

- **Kotlin code style**: Official (`kotlin.code.style=official` in gradle.properties)
- **Trailing commas**: Used consistently in function parameters, constructor parameters, and DSL blocks
- **KDoc**: Used on public API classes and functions in the `komig` library; sample app uses less documentation
- **Package structure**: `com.komig` for the library, `com.komig.sample` for the sample app
- **Expect/actual pattern**: Used for platform-specific implementations (`ImageCompressor`, `ImagePicker`, `ByteArrayImage`, `Platform`)
- **Sealed classes**: Used for type-safe hierarchies (`KomigException`, `ResizeMode`)
- **Enums**: Used for fixed sets (`OutputFormat`)
- **Data classes**: Used for value types (`CompressionResult`, `CompressUiState`)
- **Object**: Used for singleton entry points (`Komig`)
- **Builder DSL**: Configuration via builder pattern with DSL lambda (`CompressionConfig.Builder`)
- **Coroutines**: Suspend functions for async operations, `Dispatchers.Default` for CPU-bound work
- **State management**: `MutableStateFlow` / `StateFlow` with `.update {}` for atomic state changes
- **ViewModel**: Jetpack/KMP ViewModel with `viewModelScope`
- **Compose**: Material3 theming, `@Composable` functions in separate files per screen
- **Indentation**: 4 spaces
- **Internal visibility**: Used for implementation details (`internal expect class ImageCompressor`)
