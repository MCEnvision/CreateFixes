# Technical Documentation

## Platform

CreateFixes targets Minecraft 1.20.1 with Forge 47.4.20 and Java 17. It applies focused mixins to Create 6.0.8 behavior.

## Dependency boundary

Create and Ponder are compile only dependencies resolved from the official Create Maven repository. The production environment supplies the compatible Create installation. This repository does not package either dependency.

## Verification

Run the complete build from a clean checkout.

```powershell
.\gradlew.bat compileJava build --no-daemon
```

```bash
./gradlew compileJava build --no-daemon
```

The pull request must also pass dependency review, CodeQL, secret scanning, and documentation checks.
