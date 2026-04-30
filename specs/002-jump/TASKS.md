# Tasks: Variable Jump Height

> Ticket: 002-jump
> Date: 2026-04-30
> Total tasks: 2

## Task Order

---

### Task 1: Variable jump mechanic

**Type:** feature
**Files:**
- Modify: `src/main/java/be/lomagnette/qaly/runner/Player.java`
- Modify: `src/main/java/be/lomagnette/qaly/runner/Physics.java`
- Modify: `src/main/java/be/lomagnette/qaly/runner/Game.java`

**Implementation:**
- [ ] In `Player.java`: add `boolean jumpHeld` and `int jumpHoldTicks` fields (default `false`/`0`)
- [ ] In `Player.java`: add `startJumpHold()` — sets `jumpHeld = true`, `jumpHoldTicks = 0`
- [ ] In `Player.java`: add `tickJumpHold()` — if `jumpHeld`, increment `jumpHoldTicks`
- [ ] In `Player.java`: add `endJumpHold()` — sets `jumpHeld = false`, `jumpHoldTicks = 0`
- [ ] In `Physics.java`: add `static final int MAX_HOLD_TICKS = 15`
- [ ] In `Physics.java`: add `static void checkJumpCut(Player p, boolean jumpKeyThisTick)`:
  - If `!p.jumpHeld` → return (not in a hold sequence)
  - If `p.verticalVelocity <= 0` → `p.endJumpHold()`, return (already past peak)
  - If `!jumpKeyThisTick || p.jumpHoldTicks >= MAX_HOLD_TICKS` → set `p.verticalVelocity = 0`, call `p.endJumpHold()`
- [ ] In `Game.java`: add `boolean jumpKeyThisTick` field
- [ ] In `Game.java` `handleEvent()` PLAYING state: when SPACE/UP pressed:
  - If grounded → call `Physics.jump(player)` then `player.startJumpHold()`, set `jumpKeyThisTick = true`
  - If not grounded and `player.jumpHeld` → set `jumpKeyThisTick = true` (continue hold)
- [ ] In `Game.java` `tick()`: after `Physics.applyGravity(player)` and `player.tickCooldown()`:
  - Call `player.tickJumpHold()`
  - Call `Physics.checkJumpCut(player, jumpKeyThisTick)`
  - Reset `jumpKeyThisTick = false`

**Verify:**
- [ ] `jbang build` compiles cleanly
- [ ] Quick tap SPACE: dog does a short hop (~30% height), lands quickly
- [ ] Hold SPACE for full duration: dog jumps to full height (same as before)
- [ ] Release mid-ascent: dog immediately stops rising and falls
- [ ] Hold past 15 ticks: dog stops rising at ~0.5s mark
- [ ] Cannot double-jump: pressing SPACE while airborne does nothing extra
- [ ] Bark while jumping: bark works independently of jump hold

**Commit message:** `feat: variable jump height based on hold duration`

---

### Task 2: Obstacle rebalancing for height variety

**Depends on:** Task 1
**Type:** feature
**Files:**
- Modify: `src/main/java/be/lomagnette/qaly/runner/Obstacle.java`

**Implementation:**
- [ ] In `Obstacle.HayBale.height()`: change wide variant return from `10` to `6`

**Verify:**
- [ ] `jbang build` compiles cleanly
- [ ] Puddles (height 3): clearable with a quick tap
- [ ] Wide hay bales (height 6): clearable with a short hold (~5-8 ticks)
- [ ] Tall hay bales (height 18): require near-full jump
- [ ] Fences (height 20): require full jump
- [ ] Quick tap does NOT clear fences or tall hay bales

**Commit message:** `feat: rebalance obstacle heights for variable jump`

---

## Completion Checklist

- [ ] Both tasks implemented and committed
- [ ] `jbang build` passes
- [ ] Short tap = short hop, long hold = full jump
- [ ] Obstacle height variety feels right during play
- [ ] No regressions: bark, scoring, seasons, game over all still work
