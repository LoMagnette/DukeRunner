# Spec: JavaFX Version of QALY Runner

> Ticket: 004
> Date: 2026-05-05
> Status: DRAFT

## Summary

Create a standalone JavaFX version of the QALY Runner game as a full copy of the existing terminal-based game. The JavaFX version lives in the same source tree under a `.fx` subpackage, uses PNG placeholder assets, enhanced visuals (parallax, particles, screen shake, smooth animations), programmatic sound effects, and supports both JBang execution and GraalVM native-image distribution.

## Motivation

The terminal version is limited by braille-character rendering and terminal capabilities. A JavaFX version enables richer visuals, smoother animations, sound effects, and native desktop distribution — making it suitable for demos, presentations, and standalone distribution as a desktop game.

## Detailed Requirements

### Functional Requirements

1. **[FR-1] Separate application**: New JBang entry point `src/MainFX.java` that launches the JavaFX version independently via `jbang run src/MainFX.java`.

2. **[FR-2] Full copy of game logic**: All game logic (Game, Physics, Spawner, Player, Obstacle, Ground, Season) is duplicated into `be.lomagnette.qaly.runner.fx` — no shared classes with the terminal version. The two codebases can diverge freely.

3. **[FR-3] Identical gameplay**: Same game mechanics as the terminal version:
   - Three states: TITLE, PLAYING, GAME_OVER
   - Score increments every tick, speed increases (base 1.5, +0.15 per 50 pts, max 5.0)
   - Era cycling every 100 points (Java 1 → 5 → 11 → 21)
   - Same 6 obstacle types with same dimensions/behavior (ConferenceStage, LaptopStack wide/tall, CoffeeSpill, ConfusedIntern, SlowBuildServer)
   - Same jump physics (gravity 0.5, jump velocity 7.0, no double-jump)
   - Same throw mechanic (range 40, cooldown 60 ticks, removes throwable obstacles)
   - Same spawning rules (gap shrinks with score, 60/40 jump/throw split)

4. **[FR-4] JavaFX Canvas rendering**: Render the game on a JavaFX `Canvas` within a fixed 800x400 window using `GraphicsContext`.

5. **[FR-5] PNG placeholder assets**: Use programmatically-drawn placeholder sprites (simple colored shapes rendered to `Image` or loaded from generated PNGs). Define a clear asset contract (filenames, expected dimensions) so placeholders can be swapped for real art later.
   - Duke (running, jumping, throwing, sitting poses)
   - 6 obstacle types
   - Ground decorations (coffee cup, terminal, git branch, IDE icon, Docker whale, cloud)

6. **[FR-6] AnimationTimer game loop**: Use JavaFX's `AnimationTimer` for a 60fps game loop. Game logic ticks should be decoupled from render frames if needed to maintain consistent game speed.

7. **[FR-7] Keyboard input**: Same key bindings as terminal version:
   - SPACE / UP: Jump
   - B / DOWN: Throw
   - Q: Quit
   - SPACE to start (TITLE), SPACE to restart (GAME_OVER)

8. **[FR-8] Enhanced visuals — Parallax background**: Multi-layer scrolling background (at minimum: sky gradient, distant layer, mid-ground layer) that scrolls at different speeds relative to game speed, creating depth.

9. **[FR-9] Enhanced visuals — Particle effects**:
   - Dust particles kicked up while running on ground
   - Sparkle/burst effect on score milestones (era transitions)
   - Explosion/impact effect on collision (game over)

10. **[FR-10] Enhanced visuals — Screen shake**: Camera shake effect on collision / game over. Brief, dampened shake that settles quickly.

11. **[FR-11] Enhanced visuals — Smooth animations**:
    - Animated sprite frames for Duke (running cycle, jump arc, throw wind-up)
    - Smooth obstacle scrolling (sub-pixel positioning)
    - Visual transitions on era changes (color fade/blend)

12. **[FR-12] Programmatic sound effects**: Synthesized audio (using `javax.sound.sampled` or JavaFX `AudioClip` with generated data) for:
    - Jump sound (short upward tone)
    - Throw sound (whoosh/impact)
    - Collision sound (crash/thud)
    - Score milestone sound (chime on era transition)

13. **[FR-13] HUD display**: Same information as terminal version, rendered as JavaFX text overlays:
    - Throw status (ready / cooldown indicator)
    - Current era label with era color
    - Score display

14. **[FR-14] Title screen**: Duke artwork/logo with "Press SPACE to start" — styled for the JavaFX window.

15. **[FR-15] Game over screen**: Final score display with "Press SPACE to restart" overlay.

16. **[FR-16] GraalVM native-image support**: The application must be compilable to a native executable via GraalVM native-image with JavaFX support (using Gluon's GraalVM JavaFX plugin or equivalent).

### Acceptance Criteria

Given the JavaFX app is launched via `jbang run src/MainFX.java`
When the game window opens
Then an 800x400 window appears with the title screen showing Duke and "Press SPACE to start"

Given the player is on the title screen
When SPACE is pressed
Then the game begins with Duke running, parallax background scrolling, and score incrementing

Given the player is playing
When SPACE or UP is pressed while Duke is on the ground
Then Duke jumps with a smooth arc animation, dust particles appear, and a jump sound plays

Given the player is playing
When B or DOWN is pressed and throw is ready
Then the throw animation plays, a whoosh sound plays, and nearby throwable obstacles are removed

Given Duke collides with an obstacle
When collision is detected
Then the screen shakes, a crash sound plays, and the game over screen appears with the final score

Given the score crosses an era boundary (100, 200, 300)
When the era changes
Then the background colors transition smoothly, a chime plays, and sparkle particles appear

Given the game over screen is showing
When SPACE is pressed
Then the game resets and a new run begins

### Edge Cases

| Scenario | Expected Behavior |
|----------|-------------------|
| Window close button (X) clicked | Application exits cleanly |
| Window loses focus during gameplay | Game continues running (no pause) |
| Rapid key presses | Input is debounced appropriately; no stacking of jumps |
| Very high scores (1000+) | Era cycling continues (wraps around), speed stays capped at 5.0 |
| Multiple obstacles on screen | All render and collide correctly |

## Error Handling

| Error Condition | Response |
|----------------|----------|
| JavaFX not available | JBang should pull JavaFX deps automatically; fail with clear error if platform unsupported |
| Audio synthesis fails | Game continues without sound — audio is non-blocking |
| Native-image build fails | Documented as a known limitation with troubleshooting steps |

## Anti-Requirements (What This Does NOT Do)

- **No network access**: No online leaderboards, analytics, update checks, or any network calls. Purely offline.
- **No shared code with terminal version**: This is a full copy. Changes to one version do not affect the other.
- **No settings UI**: No configuration menus. All values are hardcoded constants.
- **No save/load**: No persistent high scores or save state between sessions.
- **No window resizing**: Fixed 800x400 window, no dynamic scaling.
- **No mouse/touch input**: Keyboard only.

## Performance Considerations

- Target 60fps on modern hardware via AnimationTimer
- Particle systems should cap particle count to avoid GC pressure
- Placeholder sprites should be pre-rendered to `Image` objects at startup, not regenerated each frame
- Sound effects should be pre-synthesized and cached, not generated on each play

## Security Considerations

- No network access — no attack surface
- No file I/O beyond loading bundled assets — no path traversal risk
- Input is keyboard-only from JavaFX events — no injection risk

## Affected Components

- **New**: 
  - `src/MainFX.java` — JBang entry point for JavaFX version
  - `src/main/java/be/lomagnette/qaly/runner/fx/GameFX.java` — game state and logic
  - `src/main/java/be/lomagnette/qaly/runner/fx/PhysicsFX.java` — physics engine
  - `src/main/java/be/lomagnette/qaly/runner/fx/PlayerFX.java` — player state
  - `src/main/java/be/lomagnette/qaly/runner/fx/ObstacleFX.java` — obstacle types
  - `src/main/java/be/lomagnette/qaly/runner/fx/SpawnerFX.java` — obstacle spawning
  - `src/main/java/be/lomagnette/qaly/runner/fx/GroundFX.java` — ground and decorations
  - `src/main/java/be/lomagnette/qaly/runner/fx/SeasonFX.java` — era/season definitions
  - `src/main/java/be/lomagnette/qaly/runner/fx/RendererFX.java` — JavaFX Canvas rendering
  - `src/main/java/be/lomagnette/qaly/runner/fx/SpritesFX.java` — placeholder sprite generation
  - `src/main/java/be/lomagnette/qaly/runner/fx/AudioFX.java` — programmatic sound synthesis
  - `src/main/java/be/lomagnette/qaly/runner/fx/ParticleSystem.java` — particle effects
  - `src/main/java/be/lomagnette/qaly/runner/fx/ScreenShake.java` — screen shake effect
  - `src/main/java/be/lomagnette/qaly/runner/fx/QalyRunnerFX.java` — JavaFX Application class
- **Unchanged**: All existing terminal version files — no modifications to any file in `be.lomagnette.qaly.runner`

## Open Questions

- [ ] What specific Duke poses / animation frames are needed for smooth sprite animations? (Can be refined during design)
- [ ] Should the native-image build use Gluon's GraalVM JavaFX substrate, or another approach?
- [ ] Exact parallax layer design (how many layers, what visual theme per layer)?
