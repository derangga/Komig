# Task Completion Checklist

When a coding task is completed, ensure the following:

1. **Build check**: Run `./gradlew build` to verify the project compiles without errors.
2. **Tests**: Run `./gradlew :composeApp:allTests` and `./gradlew :komig:allTests` to make sure no tests are broken.
3. **Code style**: Kotlin official code style is enforced — ensure new code follows the conventions described in `code_style_and_conventions`.
4. **Platform parity**: If adding platform-specific code via `expect/actual`, ensure all target platforms (Android + iOS) have corresponding `actual` implementations.
5. **Version catalog**: When adding new dependencies, add them to `gradle/libs.versions.toml` and reference via `libs.*`.
