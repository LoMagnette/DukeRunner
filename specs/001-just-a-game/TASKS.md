# Tasks: QALY Runner

> Ticket: 001-just-a-game
> Date: 2026-04-30
> Total tasks: 10

## Task Order

Tasks are ordered by dependency. Complete each one before starting the next.

---

### Task 1: Project scaffold + TuiRunner hello world

**Type:** scaffold
**Files:**
- Create: `src/QalyRunner.java`

**Implementation:**
- [ ] Create `src/QalyRunner.java` with JBang directives (`//DEPS` for tamboui-tui, tamboui-widgets, tamboui-panama-backend)
- [ ] Set up TuiRunner with 33ms tick rate
- [ ] Render a static "QALY Runner" title text centered on screen using Paragraph widget
- [ ] Handle `q` key to quit via KeyEvent in the event callback
- [ ] Handle TickEvent (return false for now — no animation yet)

**Verify:**
- [ ] `jbang run src/QalyRunner.java` launches, shows title text, `q` quits cleanly
- [ ] No compilation errors

**Commit message:** `feat: scaffold JBang project with TuiRunner hello world`

---

### Task 2: Game state machine + title screen

**Depends on:** Task 1
**Type:** core
**Files:**
- Create: `src/Game.java`
- Create: `src/Season.java`
- Modify: `src/QalyRunner.java` (wire Game into event/render callbacks)

**Implementation:**
- [ ] Create `Season.java` — enum with SPRING, SUMMER, AUTUMN, WINTER; each has a `label()` and `groundColor()` method
- [ ] Create `Game.java` — enum `State { TITLE, PLAYING, GAME_OVER }`; fields for state, score, speed, current season
- [ ] `Game#handleEvent(Event)` — on TITLE state: SPACE transitions to PLAYING, Q quits
- [ ] `Game#tick()` — no-op on TITLE state
- [ ] Title screen rendering: "QALY Runner" title, "Press SPACE to start" prompt, "Q to quit" — centered text using Paragraph or Block widget
- [ ] Wire Game into QalyRunner's event and render callbacks

**Verify:**
- [ ] App starts on title screen, SPACE transitions (to blank playing state for now), Q quits
- [ ] State transitions work correctly

**Commit message:** `feat: game state machine with title screen`

---

### Task 3: Player + jump physics on Canvas

**Depends on:** Task 2
**Type:** core
**Files:**
- Create: `src/Player.java`
- Create: `src/Physics.java`
- Create: `src/Sprites.java`
- Create: `src/Renderer.java`
- Modify: `src/Game.java` (add Player, wire physics)

**Implementation:**
- [ ] Create `Player.java` — fields: x (fixed), y (float), verticalVelocity, grounded (boolean), barkCooldownTicks
- [ ] Create `Physics.java` — constants: GRAVITY, JUMP_VELOCITY, GROUND_Y; methods: `applyGravity(Player)`, `jump(Player)`, `isGrounded(Player)`
- [ ] Create `Sprites.java` — define braille dot coordinate arrays for Bouvier in running pose (simple blocky rectangle to start); method `bouvierRunning()` returns list of float coordinate pairs
- [ ] Create `Renderer.java` — sets up Layout (1-row HUD + fill game area); renders game area as Canvas with braille marker; paints ground line + player sprite
- [ ] In `Game.java`: create Player instance, on SPACE/UP in PLAYING state call `Physics.jump()`, on each tick call `Physics.applyGravity()`
- [ ] Canvas coordinate system: x range = [0, terminal_width*2], y range = [0, terminal_height*4] (braille resolution)

**Verify:**
- [ ] Player appears on screen as a braille shape on a ground line
- [ ] SPACE makes the player jump in a smooth parabolic arc and land back on ground
- [ ] Cannot double-jump while airborne

**Commit message:** `feat: player with jump physics rendered on braille canvas`

---

### Task 4: Ground scrolling + decorations

**Depends on:** Task 3
**Type:** feature
**Files:**
- Create: `src/Ground.java`
- Modify: `src/Renderer.java` (paint ground decorations)
- Modify: `src/Game.java` (update ground each tick)

**Implementation:**
- [ ] Create `Ground.java` — holds list of Decoration records (type, x position); types: FLOWER, STONE, GRASS_TUFT
- [ ] `Ground#scroll(float speed)` — moves all decorations left by speed; recycles off-screen items to right edge with random type
- [ ] Initial decoration placement: sparse random distribution across full width
- [ ] In `Renderer.java`: paint ground line (horizontal line at GROUND_Y), paint each decoration as small braille shape
- [ ] In `Sprites.java`: add decoration sprite data (2-3 dot patterns per type)
- [ ] In `Game.java`: create Ground instance, call `ground.scroll(speed)` on each tick during PLAYING

**Verify:**
- [ ] Ground line visible with decorations scrolling left continuously
- [ ] Decorations wrap around and new ones appear from the right
- [ ] Scroll speed matches game speed

**Commit message:** `feat: scrolling ground with flower, stone, and grass decorations`

---

### Task 5: Obstacles + spawning

**Depends on:** Task 4
**Type:** feature
**Files:**
- Create: `src/Obstacle.java`
- Create: `src/Spawner.java`
- Modify: `src/Sprites.java` (add obstacle sprites)
- Modify: `src/Renderer.java` (paint obstacles)
- Modify: `src/Game.java` (manage obstacle list, spawner)

**Implementation:**
- [ ] Create `Obstacle.java` — sealed interface with: `float x()`, `float y()`, `float width()`, `float height()`, `boolean barkable()`; records: `Fence`, `HayBale`, `Puddle`, `Sheep`, `Chicken`
- [ ] Each record has fixed dimensions; Fence = tall/narrow, HayBale = two variants (low-wide, tall-narrow), Puddle = flat/wide, Sheep = medium, Chicken = small
- [ ] Create `Spawner.java` — tracks tick count since last spawn, minimum gap (shrinks with difficulty); `maybeSpawn(int score, float speed)` returns Optional<Obstacle>
- [ ] Spawner rule: never two consecutive bark-only obstacles; weighted random selection
- [ ] In `Sprites.java`: add braille sprite data for each obstacle type
- [ ] In `Renderer.java`: iterate active obstacles, paint each sprite at its position
- [ ] In `Game.java`: maintain `List<Obstacle>` active obstacles; each tick: scroll obstacles left, remove off-screen, call spawner

**Verify:**
- [ ] Obstacles appear from the right and scroll left
- [ ] Mix of jump and bark obstacles
- [ ] No two consecutive bark-only obstacles
- [ ] Obstacles have distinct visual shapes

**Commit message:** `feat: obstacle types with spawning and difficulty progression`

---

### Task 6: Collision detection + game over

**Depends on:** Task 5
**Type:** feature
**Files:**
- Modify: `src/Physics.java` (add collision detection)
- Modify: `src/Game.java` (check collisions, transition to GAME_OVER)
- Modify: `src/Renderer.java` (game over screen)

**Implementation:**
- [ ] In `Physics.java`: `collides(Player, Obstacle)` — axis-aligned bounding box overlap check using player and obstacle dimensions
- [ ] In `Game.java` tick: after scrolling obstacles, check collision of each obstacle with player; if collision → transition to GAME_OVER state
- [ ] Game over state: freeze all movement, store final score
- [ ] In `Game.java` event handler: on GAME_OVER state, SPACE restarts (reset score, speed, obstacles, player), Q quits
- [ ] In `Renderer.java`: on GAME_OVER, overlay centered "GAME OVER" text + score + "SPACE to restart" on top of frozen game frame
- [ ] Terminal bell: print `\007` on collision

**Verify:**
- [ ] Running into a fence/hay bale/puddle triggers game over
- [ ] Game over screen shows score and restart prompt
- [ ] Terminal bell sounds on collision
- [ ] SPACE restarts the game cleanly, Q quits
- [ ] Sheep/chickens also cause collision if not barked at

**Commit message:** `feat: collision detection and game over screen with restart`

---

### Task 7: Bark mechanic

**Depends on:** Task 6
**Type:** feature
**Files:**
- Modify: `src/Player.java` (bark cooldown tracking)
- Modify: `src/Physics.java` (bark range check)
- Modify: `src/Game.java` (bark action, scatter obstacles)
- Modify: `src/Renderer.java` (bark visual effect)

**Implementation:**
- [ ] In `Player.java`: `barkCooldownTicks` field; `canBark()` returns true if cooldown is 0; `bark()` sets cooldown to 60 ticks (~2 seconds at 30fps); `tickCooldown()` decrements if > 0
- [ ] In `Physics.java`: `inBarkRange(Player, Obstacle)` — checks if obstacle is within N game units ahead of player and is barkable
- [ ] In `Game.java`: on B/DOWN key in PLAYING state: if `player.canBark()`, call bark, remove all barkable obstacles in range from active list
- [ ] In `Game.java` tick: call `player.tickCooldown()` each tick
- [ ] In `Renderer.java`: when bark fires, render brief "WOOF!" text near player (visible for ~10 ticks)
- [ ] Terminal bell: print `\007` on bark

**Verify:**
- [ ] Pressing B scatters nearby sheep/chickens
- [ ] Bark has 2-second cooldown — pressing B during cooldown does nothing
- [ ] "WOOF!" visual appears briefly
- [ ] Terminal bell sounds on bark
- [ ] Fences/hay bales/puddles are NOT affected by bark

**Commit message:** `feat: bark mechanic with cooldown and visual feedback`

---

### Task 8: Scoring + speed progression

**Depends on:** Task 7
**Type:** feature
**Files:**
- Modify: `src/Game.java` (score tracking, speed curve)
- Modify: `src/Renderer.java` (HUD rendering)

**Implementation:**
- [ ] In `Game.java`: increment score by 1 each tick during PLAYING; speed formula: `BASE_SPEED + (score / 50) * SPEED_INCREMENT`, capped at `MAX_SPEED`
- [ ] Pass current speed to `ground.scroll()` and obstacle scrolling
- [ ] In `Renderer.java`: render HUD in top row using styled Text/Paragraph — score right-aligned, bark cooldown indicator left-aligned (`[BARK READY]` in green or `[BARK ··]` in dim), season label centered
- [ ] Use Layout with `Constraint.length(1)` for HUD + `Constraint.fill()` for game area

**Verify:**
- [ ] Score ticks up continuously during gameplay
- [ ] Game speed visibly increases over time
- [ ] Speed caps out at a playable maximum
- [ ] HUD displays score, bark status, and season correctly
- [ ] HUD does not overlap game area

**Commit message:** `feat: scoring system, speed progression, and HUD display`

---

### Task 9: Season cycle + visual milestones

**Depends on:** Task 8
**Type:** feature
**Files:**
- Modify: `src/Season.java` (color palettes, decoration config per season)
- Modify: `src/Game.java` (season transitions)
- Modify: `src/Ground.java` (season-aware decorations)
- Modify: `src/Renderer.java` (season-themed colors)

**Implementation:**
- [ ] In `Season.java`: each season defines — foreground color, ground color, decoration types, optional particle type (e.g., WINTER has snow dots, AUTUMN has falling leaves)
- [ ] `Season.forScore(int score)` — returns season based on `(score / 100) % 4`
- [ ] In `Game.java` tick: update current season from score; when season changes, update ground decorations
- [ ] In `Ground.java`: `setSeason(Season)` — swaps decoration types to match season palette
- [ ] In `Renderer.java`: use season colors for ground line, decorations, and optional falling particles (small dots drifting down for snow/leaves)
- [ ] Season transitions: immediate swap (no fade — keep it simple)

**Verify:**
- [ ] At score 100, visuals shift from Spring to Summer
- [ ] At score 200, Autumn with falling leaves
- [ ] At score 300, Winter with snow particles
- [ ] At score 400, cycle back to Spring
- [ ] Each season has visually distinct colors and decorations
- [ ] Season label in HUD updates

**Commit message:** `feat: season cycle with themed decorations and particles`

---

### Task 10: Terminal resize handling + sprite polish

**Depends on:** Task 9
**Type:** polish
**Files:**
- Modify: `src/Renderer.java` (resize check, sprite refinement)
- Modify: `src/Sprites.java` (final sprite designs)
- Modify: `src/QalyRunner.java` (resize event handling)

**Implementation:**
- [ ] In `Renderer.java`: at start of render, check `frame.area()` dimensions; if width < 80 or height < 24, render centered "Please resize your terminal (min 80x24)" message instead of game
- [ ] In `QalyRunner.java`: handle ResizeEvent in event callback — request redraw
- [ ] In `Sprites.java`: refine all braille sprites for visual quality:
  - Bouvier running: recognizable dog shape with legs, ear, tail (~8x6 braille dots)
  - Bouvier jumping: legs tucked, arcing pose
  - Bouvier barking: mouth open, "woof" lines
  - Fence: vertical posts with horizontal bar
  - Hay bale: rounded rectangle
  - Puddle: flat wavy shape
  - Sheep: fluffy body with legs
  - Chicken: small bird shape
- [ ] Title screen: large braille art Bouvier for the title screen

**Verify:**
- [ ] Shrinking terminal below 80x24 shows resize message
- [ ] Enlarging terminal back resumes game
- [ ] All sprites are visually recognizable and distinct
- [ ] Game feels complete and playable end-to-end

**Commit message:** `feat: terminal resize handling and polished braille sprites`

---

## Completion Checklist

- [ ] All 10 tasks implemented and committed
- [ ] `jbang run src/QalyRunner.java` launches and is playable
- [ ] Title screen → gameplay → game over → restart loop works
- [ ] Jump and bark mechanics work correctly
- [ ] Obstacle variety and spawning rules are correct
- [ ] Score, speed progression, and season cycle work
- [ ] HUD displays all required info
- [ ] Terminal resize shows appropriate message
- [ ] No TODO/FIXME left in code
