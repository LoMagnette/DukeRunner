# Tasks: JavaFX Version of QALY Runner

> Ticket: 004
> Date: 2026-05-05
> Total tasks: 11

## Task Order

Tasks are ordered by dependency. Complete each one before starting the next.

---

### Task 1: Core game model — SeasonFX, PlayerFX, PhysicsFX, ObstacleFX

**Type:** model
**Files:**
- Create: `src/main/java/be/lomagnette/qaly/runner/fx/SeasonFX.java`
- Create: `src/main/java/be/lomagnette/qaly/runner/fx/PlayerFX.java`
- Create: `src/main/java/be/lomagnette/qaly/runner/fx/PhysicsFX.java`
- Create: `src/main/java/be/lomagnette/qaly/runner/fx/ObstacleFX.java`
- Create: `src/test/JavaFXGameTest.java`
- Modify: `src/test/TestRunner.java` — add `JavaFXGameTest.class` and `//SOURCES` for FX classes

**Test first (RED):**
- [ ] Write `JavaFXGameTest#testSeasonFXProperties` — verify all 4 eras have correct labels and non-null colors
- [ ] Write `JavaFXGameTest#testSeasonFXForScoreCycles` — verify `forScore()` cycles through eras correctly
- [ ] Write `JavaFXGameTest#testPlayerFXInitialState` — player starts grounded at GROUND_Y with canThrow=true
- [ ] Write `JavaFXGameTest#testPlayerFXThrowCooldown` — performThrow sets cooldown, tickCooldown decrements
- [ ] Write `JavaFXGameTest#testPhysicsFXJump` — jump sets velocity, not grounded; second jump ignored
- [ ] Write `JavaFXGameTest#testPhysicsFXGravity` — applyGravity moves player down, snaps to ground
- [ ] Write `JavaFXGameTest#testPhysicsFXCollision` — AABB collision detection matches terminal version
- [ ] Write `JavaFXGameTest#testPhysicsFXThrowRange` — inThrowRange checks horizontal distance and throwable flag
- [ ] Write `JavaFXGameTest#testObstacleFXDimensions` — all 6 obstacle types have correct width/height/throwable

**Implementation (GREEN):**
- [ ] Copy `Season.java` → `SeasonFX.java`, change package to `...fx`, replace `dev.tamboui.style.Color` with `javafx.scene.paint.Color`, map color values (GREEN→Color.GREEN, etc.)
- [ ] Copy `Player.java` → `PlayerFX.java`, change package. No other changes needed (no TamboUI deps)
- [ ] Copy `Physics.java` → `PhysicsFX.java`, change package, reference `PlayerFX`/`ObstacleFX`
- [ ] Copy `Obstacle.java` → `ObstacleFX.java`, change package, reference `PhysicsFX.GROUND_Y`

**Verify:**
- [ ] `jbang run src/test/TestRunner.java --filter=JavaFXGameTest` passes
- [ ] `jbang build src/test/TestRunner.java` compiles cleanly

**Commit message:** `feat(fx): add core game model classes — SeasonFX, PlayerFX, PhysicsFX, ObstacleFX`

---

### Task 2: World generation — GroundFX, SpawnerFX

**Depends on:** Task 1
**Type:** model
**Files:**
- Create: `src/main/java/be/lomagnette/qaly/runner/fx/GroundFX.java`
- Create: `src/main/java/be/lomagnette/qaly/runner/fx/SpawnerFX.java`
- Modify: `src/test/JavaFXGameTest.java` — add spawner and ground tests
- Modify: `src/test/TestRunner.java` — add `//SOURCES` for new files

**Test first (RED):**
- [ ] Write `JavaFXGameTest#testGroundFXInitHasDecorations` — new ground has decorations list populated
- [ ] Write `JavaFXGameTest#testGroundFXScrollMovesDecorations` — after scroll, decoration x values decrease
- [ ] Write `JavaFXGameTest#testSpawnerFXRespectsMinGap` — no spawn before minGap ticks
- [ ] Write `JavaFXGameTest#testSpawnerFXNeverTwoThrowablesInRow` — after throwable, next must be jump

**Implementation (GREEN):**
- [ ] Copy `Ground.java` → `GroundFX.java`, change package, reference `SeasonFX`
- [ ] Copy `Spawner.java` → `SpawnerFX.java`, change package, reference `ObstacleFX`

**Verify:**
- [ ] `jbang run src/test/TestRunner.java --filter=JavaFXGameTest` passes

**Commit message:** `feat(fx): add world generation — GroundFX, SpawnerFX`

---

### Task 3: Game state machine — GameFX

**Depends on:** Task 2
**Type:** model
**Files:**
- Create: `src/main/java/be/lomagnette/qaly/runner/fx/GameFX.java`
- Modify: `src/test/JavaFXGameTest.java` — add game state tests
- Modify: `src/test/TestRunner.java` — add `//SOURCES`

**Test first (RED):**
- [ ] Write `JavaFXGameTest#testGameFXStartsInTitleState` — initial state is TITLE
- [ ] Write `JavaFXGameTest#testGameFXTickIncreasesScore` — after starting + ticking, score > 0
- [ ] Write `JavaFXGameTest#testGameFXSpeedCapped` — after many ticks, speed <= MAX_SPEED
- [ ] Write `JavaFXGameTest#testGameFXEraCyclesWithScore` — era changes at score boundaries
- [ ] Write `JavaFXGameTest#testGameFXCollisionEndsGame` — placing obstacle at player position triggers GAME_OVER

**Implementation (GREEN):**
- [ ] Copy `Game.java` → `GameFX.java`, change package
- [ ] Replace `KeyEvent` handling: expose `handleKeyPress(javafx.scene.input.KeyCode)` method instead of TamboUI's `handleEvent(KeyEvent)`
- [ ] Remove `System.out.print("\007")` bell characters (audio handled by AudioFX later)
- [ ] Reference all FX model classes (PlayerFX, PhysicsFX, ObstacleFX, SpawnerFX, GroundFX, SeasonFX)

**Verify:**
- [ ] `jbang run src/test/TestRunner.java --filter=JavaFXGameTest` passes

**Commit message:** `feat(fx): add game state machine — GameFX`

---

### Task 4: Placeholder sprite system — SpritesFX

**Depends on:** Task 1
**Type:** service
**Files:**
- Create: `src/main/java/be/lomagnette/qaly/runner/fx/SpritesFX.java`
- Modify: `src/test/JavaFXGameTest.java` — add sprite generation tests
- Modify: `src/test/TestRunner.java` — add `//SOURCES`

**Test first (RED):**
- [ ] Write `JavaFXGameTest#testSpritesFXDukeRunningNotNull` — dukeRunning() returns non-null Image with expected dimensions
- [ ] Write `JavaFXGameTest#testSpritesFXAllObstacleSpritesExist` — all 6 obstacle sprite methods return non-null Images
- [ ] Write `JavaFXGameTest#testSpritesFXAllDecoSpritesExist` — all 6 decoration sprite methods return non-null Images

**Implementation (GREEN):**
- [ ] Create `SpritesFX` with static methods matching each entity type
- [ ] Duke sprites: `dukeRunning()`, `dukeJumping()`, `dukeThrowing()`, `dukeSitting()` — each returns `Image`. Draw Duke as a simple teardrop shape (white body, red nose circle) using `WritableImage` + `PixelWriter`
- [ ] Obstacle sprites: `conferenceStage()`, `laptopStackWide()`, `laptopStackTall()`, `coffeeSpill()`, `confusedIntern()`, `slowBuildServer()` — distinct colored geometric shapes matching terminal version dimensions
- [ ] Decoration sprites: `coffeeCup()`, `terminal()`, `gitBranch()`, `ideIcon()`, `dockerWhale()`, `cloud()` — small icons (~20x20px)
- [ ] All sprites cached in static fields (generated once on first access)
- [ ] Define scale factor constant to convert game-unit dimensions to pixel dimensions

**Verify:**
- [ ] `jbang run src/test/TestRunner.java --filter=JavaFXGameTest` passes
- [ ] Visual inspection: sprites are recognizable colored shapes

**Commit message:** `feat(fx): add placeholder sprite generation — SpritesFX`

---

### Task 5: JavaFX application shell + basic rendering — QalyRunnerFX, RendererFX, MainFX

**Depends on:** Task 3, Task 4
**Type:** integration
**Files:**
- Create: `src/MainFX.java` — JBang entry point
- Create: `src/main/java/be/lomagnette/qaly/runner/fx/QalyRunnerFX.java`
- Create: `src/main/java/be/lomagnette/qaly/runner/fx/RendererFX.java`

**Test first (RED):**
- [ ] Manual test: `jbang run src/MainFX.java` opens an 800x400 window
- [ ] Manual test: title screen shows Duke placeholder and "Press SPACE to start"
- [ ] Manual test: pressing SPACE starts the game, Duke runs, obstacles scroll
- [ ] Manual test: jumping (SPACE/UP) works, throwing (B/DOWN) works
- [ ] Manual test: collision triggers game over screen with score
- [ ] Manual test: Q quits, window X button closes cleanly

**Implementation (GREEN):**
- [ ] Create `MainFX.java` with JBang directives: `//DEPS org.openjfx:javafx-controls:25:${os.detected.jfxplatform}`, `//DEPS org.openjfx:javafx-graphics:25:${os.detected.jfxplatform}`, `//SOURCES` for all FX classes. Main method calls `Application.launch(QalyRunnerFX.class)`
- [ ] Create `QalyRunnerFX extends Application`:
  - `start(Stage)`: create 800x400 Canvas, Scene, set stage title "Duke Runner", show stage
  - `AnimationTimer`: fixed-timestep accumulator (16ms tick), call `game.tick()` per step, then `RendererFX.render(gc, game, canvas)` once per frame
  - `scene.setOnKeyPressed`: dispatch `game.handleKeyPress(keyCode)`, if quit → `Platform.exit()`
  - Track pressed keys to avoid key-repeat stacking
- [ ] Create `RendererFX`:
  - `render(GraphicsContext gc, GameFX game, double w, double h)`: clear canvas, dispatch by state
  - `renderTitle()`: draw Duke sitting sprite centered, "DUKE RUNNER" title text, "Press SPACE to start"
  - `renderGame()`: draw ground line, decorations, obstacles, player sprite (pose based on state), HUD
  - `renderGameOver()`: render game underneath, overlay semi-transparent black, "BUILD FAILED", score, restart prompt
  - `renderHud()`: throw status (left), era label (center), score (right) — top bar
  - Coordinate mapping: game units → pixel coordinates (scale + flip Y axis since game Y=0 is bottom but Canvas Y=0 is top)

**Verify:**
- [ ] `jbang build src/MainFX.java` compiles
- [ ] `jbang run src/MainFX.java` launches and is playable end-to-end
- [ ] All three game states work (title → playing → game over → restart)

**Commit message:** `feat(fx): add JavaFX application shell with basic rendering`

---

### Task 6: Parallax background

**Depends on:** Task 5
**Type:** service
**Files:**
- Create: `src/main/java/be/lomagnette/qaly/runner/fx/ParallaxBackground.java`
- Modify: `src/main/java/be/lomagnette/qaly/runner/fx/RendererFX.java` — integrate parallax rendering

**Test first (RED):**
- [ ] Manual test: background has visible depth with multiple scrolling layers
- [ ] Manual test: layers scroll at different speeds (distant = slower)
- [ ] Manual test: background colors change with era transitions

**Implementation (GREEN):**
- [ ] Create `ParallaxBackground` with 3 layers:
  - Layer 1 (sky): static gradient fill, colors from SeasonFX era
  - Layer 2 (distant): cloud/mountain silhouettes at 0.2x game speed
  - Layer 3 (mid-ground): hill/building silhouettes at 0.5x game speed
- [ ] Each layer stores an x-offset that wraps when exceeding layer width
- [ ] `scroll(float speed)`: update offsets
- [ ] `render(GraphicsContext gc, SeasonFX era, double w, double h)`: paint layers back-to-front
- [ ] Era-aware color palette: each era defines sky gradient, distant color, mid-ground color
- [ ] Integrate into `RendererFX.renderGame()` — paint before ground/obstacles

**Verify:**
- [ ] `jbang run src/MainFX.java` shows parallax scrolling during gameplay
- [ ] Background changes color palette on era transitions

**Commit message:** `feat(fx): add parallax scrolling background`

---

### Task 7: Particle system

**Depends on:** Task 5
**Type:** service
**Files:**
- Create: `src/main/java/be/lomagnette/qaly/runner/fx/ParticleSystem.java`
- Modify: `src/main/java/be/lomagnette/qaly/runner/fx/RendererFX.java` — integrate particle rendering
- Modify: `src/main/java/be/lomagnette/qaly/runner/fx/GameFX.java` — emit particles on events

**Test first (RED):**
- [ ] Manual test: dust particles appear behind Duke while running on ground
- [ ] Manual test: sparkle burst appears on era transition (score 100, 200, 300)
- [ ] Manual test: explosion effect appears on collision
- [ ] Manual test: no visible lag or stutter from particles

**Implementation (GREEN):**
- [ ] Create `ParticleSystem` with fixed-size particle array (max 100):
  - Particle struct: x, y, vx, vy, life, maxLife, color, size
  - `emitDust(float x, float y)`: small brown particles, low velocity, short life
  - `emitSparkle(float x, float y, Color color)`: burst of colored dots radiating outward
  - `emitExplosion(float x, float y)`: large red/orange burst
  - `tick()`: update positions, decrement life, recycle dead particles
  - `render(GraphicsContext gc)`: draw each live particle as a filled circle, alpha fades with life
- [ ] In `GameFX.tick()`: call `particles.emitDust()` each tick when player is grounded
- [ ] In `GameFX.tick()`: detect era change, call `particles.emitSparkle()` at player position
- [ ] In `GameFX.tick()`: on collision, call `particles.emitExplosion()` at player position
- [ ] In `RendererFX.renderGame()`: call `particles.render(gc)` after player

**Verify:**
- [ ] All three particle types visible during gameplay
- [ ] Particles don't accumulate unboundedly (capped at 100)

**Commit message:** `feat(fx): add particle effects — dust, sparkle, explosion`

---

### Task 8: Screen shake

**Depends on:** Task 5
**Type:** service
**Files:**
- Create: `src/main/java/be/lomagnette/qaly/runner/fx/ScreenShake.java`
- Modify: `src/main/java/be/lomagnette/qaly/runner/fx/RendererFX.java` — apply shake offset
- Modify: `src/main/java/be/lomagnette/qaly/runner/fx/GameFX.java` — trigger shake on collision

**Test first (RED):**
- [ ] Manual test: screen visibly shakes on collision before game over screen appears
- [ ] Manual test: shake dampens quickly (settles within ~0.5 seconds)

**Implementation (GREEN):**
- [ ] Create `ScreenShake`:
  - `trigger(float intensity)`: start shake with given intensity (e.g., 8.0 for collision)
  - `tick()`: decay intensity by multiplying by 0.85 each tick, randomize offset
  - `offsetX()`, `offsetY()`: current shake displacement
  - `isActive()`: intensity > 0.1
- [ ] In `GameFX`: on collision, call `screenShake.trigger(8.0f)`, tick shake each frame
- [ ] In `RendererFX.renderGame()`: apply `gc.translate(shake.offsetX(), shake.offsetY())` before drawing, restore after

**Verify:**
- [ ] Collision produces visible shake effect
- [ ] Shake doesn't persist indefinitely

**Commit message:** `feat(fx): add screen shake on collision`

---

### Task 9: Smooth animations

**Depends on:** Task 5
**Type:** enhancement
**Files:**
- Modify: `src/main/java/be/lomagnette/qaly/runner/fx/SpritesFX.java` — add running animation frames
- Modify: `src/main/java/be/lomagnette/qaly/runner/fx/RendererFX.java` — animate Duke + era transitions
- Modify: `src/main/java/be/lomagnette/qaly/runner/fx/GameFX.java` — track animation frame counter

**Test first (RED):**
- [ ] Manual test: Duke has a visible running animation (legs alternate or body bobs)
- [ ] Manual test: era transition has a smooth color blend (not an instant snap)
- [ ] Manual test: obstacles scroll smoothly (no visible jitter)

**Implementation (GREEN):**
- [ ] Add `dukeRunning2()` to `SpritesFX` — second frame of running cycle (slightly different leg/body position)
- [ ] In `GameFX`: add `animationFrame` counter that increments each tick, used to select running sprite frame (toggle every 8 ticks)
- [ ] In `RendererFX`: select Duke sprite based on `game.animationFrame % 16 < 8` for running cycle
- [ ] Era color transition: track `currentBgColor` and `targetBgColor`, lerp between them over ~30 ticks when era changes
- [ ] Sub-pixel rendering: ensure obstacle x positions are rendered as doubles, not rounded to integers

**Verify:**
- [ ] Duke visibly animates while running
- [ ] Era transitions are gradual, not instant
- [ ] No rendering jitter on obstacles

**Commit message:** `feat(fx): add smooth sprite animations and era transitions`

---

### Task 10: Programmatic sound effects — AudioFX

**Depends on:** Task 3
**Type:** service
**Files:**
- Create: `src/main/java/be/lomagnette/qaly/runner/fx/AudioFX.java`
- Modify: `src/main/java/be/lomagnette/qaly/runner/fx/GameFX.java` — play sounds on events

**Test first (RED):**
- [ ] Manual test: jump produces a short ascending tone
- [ ] Manual test: throw produces a whoosh sound
- [ ] Manual test: collision produces a crash/thud sound
- [ ] Manual test: era transition produces a chime
- [ ] Manual test: game doesn't freeze or stutter when sounds play

**Implementation (GREEN):**
- [ ] Create `AudioFX` with pre-synthesized `Clip` objects using `javax.sound.sampled`:
  - `init()`: generate all clips at startup. If audio system unavailable, set `enabled = false` silently
  - `playJump()`: short sine wave, frequency rises 400→800Hz over 100ms
  - `playThrow()`: noise burst filtered low→high over 150ms (whoosh)
  - `playCollision()`: low-frequency burst 100Hz, 200ms with fast decay
  - `playMilestone()`: two-tone chime (C5→E5), 100ms each
  - Helper: `synthesize(float durationSec, FrequencyFunction fn)` — generates PCM byte array
- [ ] Each `play*()` method: if enabled, reset clip to frame 0 and start (non-blocking)
- [ ] In `GameFX`: call `audio.playJump()` on jump, `audio.playThrow()` on throw, `audio.playCollision()` on collision, `audio.playMilestone()` on era change

**Verify:**
- [ ] All 4 sound effects audible during gameplay
- [ ] No audio-related freezes or exceptions
- [ ] Game still works if audio system is unavailable (e.g., headless)

**Commit message:** `feat(fx): add programmatic sound effects — AudioFX`

---

### Task 11: GraalVM native-image support

**Depends on:** Task 5
**Type:** build
**Files:**
- Create: `src/main/resources/META-INF/native-image/fx/resource-config.json`
- Create: `src/main/resources/META-INF/native-image/fx/reflect-config.json`

**Test first (RED):**
- [ ] Manual test: document the native-image build command
- [ ] Manual test: if GraalVM is available, native binary launches and runs the game

**Implementation (GREEN):**
- [ ] Create `reflect-config.json` with entries for QalyRunnerFX (JavaFX Application requires reflection for launch)
- [ ] Create `resource-config.json` if any resources are bundled
- [ ] Document the build command in a comment in `MainFX.java` or a `BUILD.md` file
- [ ] Test with `native-image` if GraalVM + GluonFX substrate is available; document known limitations if not

**Verify:**
- [ ] `jbang build src/MainFX.java` still compiles
- [ ] Native-image build steps documented
- [ ] If native build works: binary launches and plays correctly

**Commit message:** `feat(fx): add GraalVM native-image configuration`

---

## Completion Checklist

- [ ] All tasks implemented and committed
- [ ] Full test suite passes: `jbang run src/test/TestRunner.java`
- [ ] JavaFX game is playable end-to-end: `jbang run src/MainFX.java`
- [ ] No modifications to existing terminal version files (except TestRunner.java)
- [ ] No TODO/FIXME left in new code
- [ ] All enhanced visuals working: parallax, particles, screen shake, animations
- [ ] Sound effects audible on jump, throw, collision, era change
- [ ] Native-image build documented
