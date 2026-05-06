# Duke Runner

A 2D endless runner game built in modern Java — available as a terminal app (TUI) and a graphical JavaFX version. As your score climbs, the game repaints itself through Java's history: Java 1 → Java 5 → Java 11 → Java 21+.

## Requirements

- Java 26+
- [JBang](https://www.jbang.dev/documentation/guide/latest/installation.html)

## Run

**Terminal (TUI):**
```bash
jbang run src/main/java/be/lomagnette/duke/runner/tui/DukeRunner.java
```

**JavaFX (GUI):**
```bash
jbang run src/main/java/be/lomagnette/duke/runner/fx/MainFX.java
```

## How to play

| Key | Action |
|-----|--------|
| `Space` / `↑` | Jump (hold for higher jump) |
| `Shift` | Throw |
| `Q` | Quit |

Jump over obstacles. The game speeds up and changes Java era every few hundred points.

## Build & test

```bash
# Compile check
jbang build src/main/java/be/lomagnette/duke/runner/tui/DukeRunner.java

# Run tests
jbang run src/test/be/lomagnette/duke/runner/TestRunner.java

# Run a single test class
jbang run src/test/be/lomagnette/duke/runner/TestRunner.java --filter=VariableJumpTest
```

## Project structure

```
src/
├── main/java/be/lomagnette/duke/runner/
│   ├── tui/          # Terminal implementation (Tamboui)
│   └── fx/           # JavaFX implementation (parallax, particles, audio)
└── test/be/lomagnette/duke/runner/
    ├── TestRunner.java
    ├── VariableJumpTest.java
    ├── JavaReskinTest.java
    └── JavaFXGameTest.java
```

Both implementations share the same game model (state machine, physics, spawner, collision) and differ only in rendering.

## Tech stack

- **Java 26** — records, sealed interfaces, pattern matching
- **JBang** — zero-config build and dependency management via `//DEPS` directives
- **Tamboui** — terminal UI framework (TUI version)
- **JavaFX 25** — GUI framework (FX version)
- **GraalVM native-image** — optional ahead-of-time compilation (config files included)