# Suggested Commands

## Build
```bash
./gradlew build                        # Build all modules
./gradlew :komig:build                 # Build the komig library only
./gradlew :composeApp:build            # Build the sample app only
./gradlew :composeApp:assembleDebug    # Build Android debug APK
```

## Test
```bash
./gradlew test                                    # Run all tests
./gradlew :komig:testDebugUnitTest                # Run komig Android unit tests
./gradlew :composeApp:testDebugUnitTest           # Run sample app unit tests
```

## Run (Android)
```bash
./gradlew :composeApp:installDebug     # Install debug APK on connected device/emulator
```

## Clean
```bash
./gradlew clean                        # Clean all build outputs
```

## Gradle
```bash
./gradlew dependencies                 # Show dependency tree
./gradlew :komig:dependencies          # Show komig dependency tree
```

## System Utilities (macOS/Darwin)
```bash
git status                             # Check git state
git log --oneline -10                  # Recent commits
```
