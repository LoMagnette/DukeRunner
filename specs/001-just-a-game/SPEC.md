# Spec: QALY Runner — Bouvier des Flandres Terminal Runner Game

> Ticket: 001-just-a-game
> Date: 2026-04-30
> Status: DRAFT

## Summary

A terminal-based infinite runner game inspired by Chrome's Dino game, where a Bouvier des Flandres runs through the Flandres countryside. Built with JBang and TamboUI, rendered using braille dot characters for high-resolution terminal graphics.

## Motivation

Fun side project / demo showcasing JBang + TamboUI for building interactive terminal applications in Java 26.

## Detailed Requirements

### Functional Requirements

#### Game States

1. **[FR-1] Title Screen**: On launch, display a title screen with:
   - ASCII/braille art of a Bouvier des Flandres
   - Game name "QALY Runner"
   - "Press SPACE to start" prompt
   - Press `q` to quit

2. **[FR-2] Gameplay**: The core running game:
   - Bouvier runs from left to right (character fixed on left side of screen, world scrolls right to left)
   - Flat ground with scrolling decorations (flowers, stones, grass tufts)
   - Obstacles spawn from the right edge and scroll left
   - Score ticks up continuously during gameplay
   - Game speed gradually increases over time

3. **[FR-3] Game Over**: On collision with an obstacle:
   - Freeze the screen
   - Display current score
   - Terminal bell sound (`\a`)
   - "Press SPACE to restart" or `q` to quit
   - Restart resets score and speed to initial values

#### Player Actions

4. **[FR-4] Jump**: Press `SPACE` or `UP ARROW` to jump.
   - The Bouvier follows a parabolic arc (up then down)
   - Cannot double-jump (must land before jumping again)
   - Used to clear fences, hay bales, and puddles

5. **[FR-5] Bark**: Press `B` or `DOWN ARROW` to bark.
   - Scares away animal obstacles (sheep, chickens) within bark range
   - Scared animals scatter/disappear instantly
   - 2-second cooldown between barks
   - Visual bark indicator (e.g., "WOOF!" text or shockwave)
   - Terminal bell sound (`\a`) on bark

#### Obstacles

6. **[FR-6] Jump Obstacles** (must jump over):
   - **Fences**: Medium height, fixed width
   - **Hay bales**: Low and wide, or tall and narrow (variety)
   - **Puddles**: Ground-level, wide — must jump to clear

7. **[FR-7] Bark Obstacles** (must bark at to scatter):
   - **Sheep**: Medium size, move slightly slower than scroll speed
   - **Chickens**: Small, may appear in groups

8. **[FR-8] Obstacle Spawning**:
   - Random obstacle selection with minimum gap between obstacles
   - Mix of jump and bark obstacles — never two bark-only sequences in a row to ensure jump stays relevant
   - Difficulty increases: gaps shrink and speed increases over time

#### Progression & Milestones

9. **[FR-9] Scoring**: Score increases by 1 per game tick. Displayed in top-right corner.

10. **[FR-10] Speed Progression**: Game scroll speed increases every 50 points, with a cap to keep it playable.

11. **[FR-11] Season Cycle** (visual milestones every 100 points):
    - 0–99: **Spring** — flowers in ground decorations, green tones
    - 100–199: **Summer** — sun decorations, warm tones
    - 200–299: **Autumn** — falling leaves, orange/brown tones
    - 300–399: **Winter** — snow particles, cool tones
    - 400+: Cycle repeats

#### Rendering

12. **[FR-12] Braille Dot Rendering**: Use TamboUI's Canvas widget with braille characters (`⠁⠂⠄⡀⠈⠐⠠⢀` etc.) for higher-resolution sprite rendering.

13. **[FR-13] Responsive Layout**: Game should adapt to terminal size. Minimum playable size: 80x24. If terminal is smaller, show a "resize terminal" message.

14. **[FR-14] HUD**: Top bar showing:
    - Current score (right-aligned)
    - Bark cooldown indicator (left-aligned, e.g., `[BARK READY]` or `[BARK ··]`)
    - Current season label

### Acceptance Criteria

```
Given the game is launched
When the title screen appears
Then the Bouvier art, game title, and start prompt are visible

Given the game is running
When the player presses SPACE
Then the Bouvier jumps in a parabolic arc

Given a fence is approaching
When the Bouvier jumps over it
Then the score continues and no collision occurs

Given sheep are approaching
When the player presses B (bark is off cooldown)
Then the sheep scatter/disappear and no collision occurs

Given sheep are approaching
When the player presses B (bark is on cooldown)
Then nothing happens and the sheep keep approaching

Given the score reaches 100
When the milestone triggers
Then ground decorations change from Spring to Summer theme

Given the Bouvier collides with an obstacle
When collision is detected
Then the game freezes, score is shown, terminal bell rings, restart prompt appears

Given the terminal is smaller than 80x24
When the game tries to render
Then a "Please resize your terminal" message is shown instead of the game
```

### Edge Cases

| Scenario | Expected Behavior |
|----------|-------------------|
| Player holds SPACE | Single jump only, no repeat until landed |
| Jump + bark simultaneously | Both actions execute if both are valid |
| Obstacle spawns off-screen right | Normal — it scrolls into view |
| Terminal resized during gameplay | Re-render adapts to new size; pause if below minimum |
| Very fast speed + bark obstacle | Bark range must still be fair — obstacles shouldn't appear inside the bark range already |
| Player presses `q` during gameplay | Game exits immediately (no confirmation needed) |
| Multiple obstacles overlap | Each resolved independently; one bark can scatter multiple animals in range |

## Error Handling

| Error Condition | Response |
|----------------|----------|
| Terminal too small | Show resize message, pause game |
| Terminal doesn't support braille | Graceful degradation — not in scope for v1, document as known limitation |
| JBang/TamboUI initialization fails | Print error to stderr and exit with non-zero code |

## Anti-Requirements (What This Does NOT Do)

- **No network access**: Fully offline, no telemetry, no update checks, no external calls
- **No config files**: All settings hardcoded in source, no JSON/YAML/properties to maintain
- **No persistent storage**: No high score file, no save state — each run is fresh
- **No multiplayer**: Single player only
- **No custom keybindings**: Fixed controls only
- **No mouse input**: Keyboard only

## Performance Considerations

- Target frame rate: ~30fps (tick rate ~33ms) — sufficient for smooth terminal animation without excessive CPU
- TamboUI's diff-based rendering minimizes terminal output (only changed cells are redrawn)
- Braille rendering is CPU-light — just character mapping, no image processing
- Single-threaded game loop via TuiRunner — no concurrency concerns

## Technology Stack

- **Runtime**: Java 26
- **Build/Run**: JBang (single-file or multi-source with `//SOURCES`)
- **UI Framework**: TamboUI (`dev.tamboui:tamboui-tui` + `dev.tamboui:tamboui-panama-backend`)
- **API Level**: TuiRunner (mid-level) for managed game loop with tick events
- **Rendering**: Canvas widget with braille dot characters

## Affected Components

- **New**: All files are new (greenfield project)
  - `src/QalyRunner.java` — entry point, game loop, TuiRunner setup
  - Supporting source files (sprites, obstacles, game state) via `//SOURCES`
- **Unchanged**: `.claude/` configuration, `CLAUDE.md`

## Open Questions

- [ ] Exact braille sprite designs for the Bouvier (running, jumping, barking poses) — to be designed during implementation
- [ ] Exact bark range in game units — to be tuned during playtesting
- [ ] Precise speed curve formula — to be tuned during playtesting
