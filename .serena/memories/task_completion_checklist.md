# Task Completion Checklist

When completing a coding task in this project, ensure:

1. **Code compiles**: Run `./gradlew build` or at minimum the affected module's build task
2. **Tests pass**: Run relevant tests with `./gradlew test` or module-specific test tasks
3. **Trailing commas**: Maintain trailing comma style in multi-line parameter lists
4. **KDoc on public API**: Add KDoc for any new public classes/functions in the `komig` library
5. **Expect/actual consistency**: If adding a new common API that needs platform-specific code, add both `expect` declarations and all `actual` implementations (Android + iOS)
6. **Internal visibility**: Keep implementation details `internal`
7. **Sealed hierarchy**: Use sealed classes for new type-safe hierarchies
8. **Coroutine safety**: Use appropriate dispatchers; CPU work on `Dispatchers.Default`
9. **State updates**: Use `_state.update {}` for atomic StateFlow updates in ViewModels
