# Implementation Plan: JavaFX Version of QALY Runner

> Ticket: 004
> Date: 2026-05-05
> Based on: SPEC.md v2026-05-05

## Architecture Overview

The JavaFX version is a full copy of the terminal game in a new subpackage. No code is shared with the terminal version. The architecture mirrors the terminal version's structure, replacing TamboUI with JavaFX.

```
MainFX.java (JBang entry point)
    └── QalyRunnerFX (javafx.Application)
            ├── GameFX (state machine + logic)
            │     ├── PlayerFX
            │     ├── PhysicsFX
            │     ├── ObstacleFX (sealed, 5 subclasses)
            │     ├── SpawnerFX
            │     ├── GroundFX
            │     └── SeasonFX (javafx.scene.paint.Color)
            ├── RendererFX (Canvas + GraphicsContext)
            │     ├── SpritesFX (programmatic Image generation)
            │     ├── ParallaxBackground
            │     └── ParticleSystem
            ├── ScreenShake (camera offset)
            └── AudioFX (javax.sound.sampled synthesis)
```

**Key difference from terminal version**: The terminal version uses TamboUI's event-driven `TuiRunner` with braille-character Canvas. The JavaFX version uses `javafx.animation.AnimationTimer` for the game loop and `javafx.scene.canvas.Canvas` with `GraphicsContext` for rendering. Game logic tick rate is decoupled from the 60fps render loop using a fixed-timestep accumulator.

## Technical Decisions

| Decision | Choice | Rationale |
|----------|--------|-----------|
| Game loop | AnimationTimer with fixed timestep | Decouples game logic from render framerate. Accumulate delta time, tick at ~16ms intervals to match terminal version feel |
| Sprite format | Programmatic `javafx.scene.image.WritableImage` | No external files needed. Generated once at startup, cached. Clear contract for future PNG replacement |
| Color system | `javafx.scene.paint.Color` | Direct replacement for `dev.tamboui.style.Color`. SeasonFX stores JavaFX colors |
| Audio | `javax.sound.sampled` (AudioClip from byte arrays) | No JavaFX media module needed. Synthesize short PCM waveforms into `Clip` objects at startup |
| Parallax | 3 layers rendered to Canvas before game elements | Sky gradient (static), distant clouds (0.2x speed), mid-ground hills (0.5x speed) |
| Particles | Simple array-of-structs in ParticleSystem class | Cap at 100 particles, reuse slots. No allocations per frame |
| Screen shake | Translate offset on GraphicsContext | Apply dampened random offset during shake, decays exponentially |
| Native build | GraalVM native-image with Gluon GluonFX plugin | Standard approach for JavaFX native images. JBang can invoke GraalVM if available |
| Input handling | Scene.setOnKeyPressed / setOnKeyReleased | Standard JavaFX keyboard input. Track key state to avoid repeat events |

## Files to Modify

| File | Change Description |
|------|-------------------|
| `src/test/TestRunner.java` | Add `JavaFXGameTest.class` to test class list, add `//SOURCES` for FX classes |

## New Files

| File | Purpose |
|------|---------|
| `src/MainFX.java` | JBang entry point with `//DEPS` for JavaFX 25, `//SOURCES` for all FX classes |
| `src/main/java/be/lomagnette/qaly/runner/fx/SeasonFX.java` | Era enum with JavaFX Color values |
| `src/main/java/be/lomagnette/qaly/runner/fx/PlayerFX.java` | Player state (y, velocity, grounded, throw cooldown) |
| `src/main/java/be/lomagnette/qaly/runner/fx/PhysicsFX.java` | Gravity, jump, collision, throw range |
| `src/main/java/be/lomagnette/qaly/runner/fx/ObstacleFX.java` | Sealed obstacle hierarchy (6 types) |
| `src/main/java/be/lomagnette/qaly/runner/fx/SpawnerFX.java` | Random obstacle spawning with gap/probability logic |
| `src/main/java/be/lomagnette/qaly/runner/fx/GroundFX.java` | Ground decorations, scrolling, era-based deco types |
| `src/main/java/be/lomagnette/qaly/runner/fx/GameFX.java` | Game state machine (TITLE/PLAYING/GAME_OVER), tick logic, input dispatch |
| `src/main/java/be/lomagnette/qaly/runner/fx/SpritesFX.java` | Programmatic placeholder sprite generation as `Image` objects |
| `src/main/java/be/lomagnette/qaly/runner/fx/RendererFX.java` | JavaFX Canvas rendering: game world, HUD, title/game-over screens |
| `src/main/java/be/lomagnette/qaly/runner/fx/ParallaxBackground.java` | 3-layer parallax scrolling background |
| `src/main/java/be/lomagnette/qaly/runner/fx/ParticleSystem.java` | Dust, sparkle, and explosion particle effects |
| `src/main/java/be/lomagnette/qaly/runner/fx/ScreenShake.java` | Dampened screen shake effect |
| `src/main/java/be/lomagnette/qaly/runner/fx/AudioFX.java` | Programmatic PCM sound synthesis and playback |
| `src/main/java/be/lomagnette/qaly/runner/fx/QalyRunnerFX.java` | JavaFX Application: stage setup, AnimationTimer, input wiring |
| `src/test/JavaFXGameTest.java` | Tests for FX game logic (model, physics, spawning, state) |

## Database Changes

None

## Dependencies

New JBang dependencies in `src/MainFX.java`:
```
//DEPS org.openjfx:javafx-controls:25:${os.detected.jfxplatform}
//DEPS org.openjfx:javafx-graphics:25:${os.detected.jfxplatform}
```

<!-- DECISION NEEDED: Exact JavaFX version — 25 or latest LTS (21)? JBang's JavaFX support may need specific classifier syntax for platform detection. Will verify during Task 5 implementation. -->

## Risk Areas

- **JavaFX + JBang module path**: JavaFX requires modules on the module path. JBang handles this for `//DEPS org.openjfx:*` but the exact classifier syntax needs verification. Fallback: use `--module-path` in `//JAVA_OPTIONS`.
- **Fixed timestep drift**: If AnimationTimer's `handle(long now)` delta varies significantly, the accumulator may bunch multiple ticks or skip frames. Needs clamping (max 5 ticks per frame).
- **Audio thread safety**: `javax.sound.sampled.Clip` playback must not block the JavaFX Application Thread. Pre-synthesize clips and call `clip.start()` (non-blocking) from game thread.
- **GraalVM + JavaFX native-image**: Requires Gluon's substrate VM and reflection configuration. This is the highest-risk task — may need fallback to jpackage if GraalVM proves too complex for a JBang project.
- **Sprite placeholder quality**: Programmatic shapes need to be recognizable enough that the game is playable. Use distinct colors and simple geometric shapes per entity type.
