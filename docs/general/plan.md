# CreateFixes Plan

## Current maintenance phase

The current phase makes clean checkout builds reproducible after the repository transfer.

### Scope

- Replace machine local Create and Ponder JAR paths with canonical Maven dependencies.
- Preserve Minecraft 1.20.1, Forge 47.4.20, Java 17, and Create 6.0.8 compatibility.
- Keep Create and Ponder compile only because the user supplies them at runtime.

### Acceptance criteria

- No build dependency relies on an untracked `CreateSRC` directory.
- `gradlew.bat compileJava build --no-daemon` succeeds.
- Every required deterministic pull request check passes.
