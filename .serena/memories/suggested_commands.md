# Suggested Commands

## Build
- **Build Android debug APK**: `./gradlew :composeApp:assembleDebug`
- **Build Android release APK**: `./gradlew :composeApp:assembleRelease`
- **Build komig library**: `./gradlew :komig:build`
- **Build all**: `./gradlew build`

## Test
- **Run common tests (composeApp)**: `./gradlew :composeApp:allTests`
- **Run common tests (komig)**: `./gradlew :komig:allTests`
- **Run komig Android host tests**: `./gradlew :komig:testDebugUnitTest`
- **Run komig Android device tests**: `./gradlew :komig:connectedAndroidTest`

## Clean
- **Clean build**: `./gradlew clean`

## Gradle
- **List dependencies**: `./gradlew :composeApp:dependencies`
- **Refresh dependencies**: `./gradlew --refresh-dependencies`

## iOS
- Open `iosApp/` directory in Xcode to build and run the iOS app.

## System Utilities (macOS / Darwin)
- `git` — version control
- `ls` — list directory contents
- `find` — search for files
- `grep` — search file contents
- `open` — open files/directories in default app (e.g. `open iosApp/` for Xcode)
