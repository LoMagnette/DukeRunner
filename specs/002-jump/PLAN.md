# Implementation Plan: Variable Jump Height

> Ticket: 002-jump
> Date: 2026-04-30
> Based on: SPEC.md v2026-04-30

## Architecture Overview

The change is isolated to the jump/physics pipeline. No new files. No new abstractions.

```
KeyEvent (SPACE/UP)
     │
     ▼
Game.handleEvent()     ← set jumpKeyThisTick flag
     │
     ▼
Game.tick()
     │
     ├─ Physics.applyGravity()     (unchanged)
     ├─ Player.tickJumpHold()      (NEW: increment hold counter)
     └─ Physics.checkJumpCut()     (NEW: zero velocity on release/max hold)
```

**Key insight**: Terminal key input fires repeated KeyEvents while held. Each tick, Game checks whether a jump key event arrived. If the player is airborne and no jump key event arrived this tick → "released" → cut velocity.

## Technical Decisions

| Decision | Choice | Rationale |
|----------|--------|-----------|
| Hold tracking location | `Player` fields (`jumpHeld`, `jumpHoldTicks`) | Player already owns all jump state (`y`, `verticalVelocity`, `grounded`) |
| Release detection | Per-tick boolean flag in `Game` | Game already owns the event→tick bridge; adding a flag is minimal |
| Velocity cut logic | Static method in `Physics` | Physics already owns `jump()` and `applyGravity()` — same pattern |
| Max hold constant | `Physics.MAX_HOLD_TICKS = 15` | Alongside existing `JUMP_VELOCITY`, `GRAVITY` constants |

## Modified Files

| File | Change |
|------|--------|
| `Player.java` | Add `jumpHeld` (boolean), `jumpHoldTicks` (int) fields. Add `startJumpHold()`, `tickJumpHold()`, `endJumpHold()` methods. Reset fields in constructor. |
| `Physics.java` | Add `MAX_HOLD_TICKS = 15` constant. Add `checkJumpCut(Player, boolean jumpKeyThisTick)` method that zeroes velocity on release or max hold. |
| `Game.java` | Add `jumpKeyThisTick` boolean field. Set it in `handleEvent()` on SPACE/UP while airborne. In `tick()`, call `Physics.checkJumpCut()` then reset the flag. |
| `Obstacle.java` | Change `HayBale.height()` wide variant from `10` to `6`. |

## New Files

None.

## Risk Areas

- **Terminal key repeat rate**: Different terminals repeat at different rates. If a terminal is slow to repeat, the player might get unintended velocity cuts. The 1-tick timeout is aggressive but matches the spec. This can be tuned to a 2-tick grace period if playtesting reveals issues.
- **Event ordering**: `handleEvent()` fires before `tick()` in TuiRunner's loop (events are processed first, then tick fires). This means `jumpKeyThisTick` is set during event processing and read during tick — correct ordering.
