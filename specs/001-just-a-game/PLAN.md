# Implementation Plan: QALY Runner

> Ticket: 001-just-a-game
> Date: 2026-04-30
> Based on: SPEC.md v2026-04-30

## Architecture Overview

Single-threaded game built on TamboUI's TuiRunner (mid-level API). The runner provides a managed event loop with two callbacks: one for input/tick events, one for rendering. All game logic runs on the render thread.

```
QalyRunner.java (entry point, JBang deps)
       │
       ▼
   TuiRunner
       │
  ┌────┴────┐
  │ events  │ render
  ▼         ▼
Game ──► Renderer
  │         │
  ├─ Player │
  ├─ Spawner├─ Sprites
  ├─ Physics│
  └─ Season └─ Canvas (braille)
```

**Game state machine:**
```
TITLE ──[SPACE]──► PLAYING ──[collision]──► GAME_OVER
  ▲                                            │
  └──────────────[q]◄──────────[SPACE]─────────┘
```

**Rendering pipeline per frame:**
1. Layout splits terminal into HUD (top 1 row) + game area (remainder)
2. HUD renders as styled Text (score, bark indicator, season)
3. Game area renders via Canvas widget with braille markers
4. Canvas coordinate system maps to game world units (floating point)
5. Ground, decorations, player, obstacles all paint onto the canvas context

## Technical Decisions

| Decision | Choice | Rationale |
|----------|--------|-----------|
| API level | TuiRunner | Gives tick-based game loop + event handling without boilerplate; Canvas not available in Toolkit DSL easily |
| Rendering | Canvas with braille markers | 2x4 dots per terminal cell = 160x96 "pixels" in an 80x24 terminal — enough for smooth sprites |
| Coordinate system | Float-based game units | Decouple game logic from terminal size; Canvas maps game coords to braille dots |
| Tick rate | 33ms (~30fps) | Smooth enough for terminal animation; avoids excessive CPU |
| Game state | Enum state machine | Three states (TITLE, PLAYING, GAME_OVER) with clean transitions |
| Obstacle model | Sealed interface + records | Each obstacle type is a record implementing Obstacle; sealed for exhaustive matching |
| Physics | Simple tick-based | No real physics engine; gravity = constant downward velocity per tick, jump = initial upward velocity |
| File structure | Flat src/ with //SOURCES | JBang multi-source; all files in one package |
| Package | `be.lomagnette.qaly.runner` | As specified in project setup |

## New Files

| File | Purpose |
|------|---------|
| `src/QalyRunner.java` | Entry point — JBang directives (`//DEPS`, `//SOURCES`), creates TuiRunner, wires event + render callbacks |
| `src/Game.java` | Game state machine — holds all mutable state, processes events, advances simulation per tick |
| `src/Player.java` | Bouvier state — x/y position, vertical velocity, grounded flag, bark cooldown timer |
| `src/Obstacle.java` | Sealed interface + record types: `Fence`, `HayBale`, `Puddle`, `Sheep`, `Chicken` |
| `src/Spawner.java` | Obstacle factory — random selection, gap management, difficulty curve, "no consecutive bark-only" rule |
| `src/Physics.java` | Jump parabola, gravity, ground clamping, collision detection (AABB), bark range check |
| `src/Renderer.java` | All rendering — layout split, HUD text, Canvas setup, paints ground/player/obstacles/decorations/effects |
| `src/Sprites.java` | Braille dot coordinate arrays for: Bouvier (running, jumping, barking), fence, hay bale, puddle, sheep, chicken |
| `src/Season.java` | Enum (SPRING, SUMMER, AUTUMN, WINTER) — color palette, decoration types, particle style per season |
| `src/Ground.java` | Ground line + scrolling decorations — tracks decoration positions, wraps/recycles off-screen items |

## Dependencies

```java
//DEPS dev.tamboui:tamboui-tui:LATEST
//DEPS dev.tamboui:tamboui-widgets:LATEST
//DEPS dev.tamboui:tamboui-panama-backend:LATEST
```

No other external dependencies. No test framework (game logic tested via manual play; unit-testable pieces are pure functions).

For now keep it pure manual testing for this game project

## Database Changes

None.

## Risk Areas

- **Canvas/braille API surface**: TamboUI's Canvas API is documented at a high level but we may hit surprises with coordinate mapping or marker selection. Mitigation: Task 1 validates the full rendering pipeline before building game logic on top.
- **Braille sprite design**: Creating recognizable sprites in a 2x4-dots-per-cell grid is an art challenge. The Bouvier needs to be recognizable. Mitigation: start with simple blocky shapes, iterate visually.
- **Tick consistency**: Terminal rendering speed varies by terminal emulator. If rendering takes longer than 33ms, ticks back up. Mitigation: delta-time based movement if needed (but start simple with fixed timestep).
- **JBang `//SOURCES` with packages**: Multi-file JBang projects with package declarations need the directory structure to match. We'll keep all source in `src/` with the same package declaration and verify in Task 1.
