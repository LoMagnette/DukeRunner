# Spec: Variable Jump Height

> Ticket: 002-jump
> Date: 2026-04-30
> Status: DRAFT

## Summary

Replace the fixed-height jump with a variable-height jump where holding the jump key longer produces a higher jump. A quick tap gives a small hop (~30% of max height); holding for up to ~0.5 seconds gives the full jump. Releasing the key (or reaching max hold) immediately zeroes upward velocity, giving a snappy, precise feel.

## Motivation

The current all-or-nothing jump makes obstacle avoidance binary — you either jump max height or you don't jump. Variable jump adds skill expression: the player must judge how long to hold based on the obstacle height. This is a core platformer mechanic (Chrome Dino, Mario, Celeste).

## Detailed Requirements

### Functional Requirements

#### Variable Jump Mechanic

1. **[FR-1] Jump initiation**: On SPACE/UP press while grounded, the dog launches upward with full initial velocity (same as current `JUMP_VELOCITY`). The `grounded` flag is set to false. A "hold timer" starts counting ticks.

2. **[FR-2] Hold detection via tick timeout**: Each game tick, if no SPACE/UP key event was received since the last tick, the jump key is considered "released." While the key is held (key events received every tick), upward velocity is maintained normally (gravity still applies each tick).

3. **[FR-3] Velocity cut on release**: When the jump key is released (no key event received in a tick while airborne and hold timer < max), the dog's `verticalVelocity` is set to `0` immediately. The dog then falls under gravity. This produces a short hop for quick taps.

4. **[FR-4] Max hold duration**: If the player holds the jump key for 15 ticks (~0.5 seconds at 30fps), the velocity is automatically zeroed — same as releasing. Holding beyond 15 ticks has no additional effect.

5. **[FR-5] No velocity cut after natural peak**: If the dog's vertical velocity has already reached 0 or gone negative (natural arc peak) before the player releases, no cut is applied — the dog is already falling.

#### Obstacle Rebalancing

6. **[FR-6] Resize existing obstacles for height variety**: Adjust obstacle dimensions so that some obstacles clearly require only a short hop while others require a full hold jump:
   - **Puddles**: Stay low (height ~3) — clearable with a quick tap
   - **Wide hay bales**: Reduce height to ~6 — clearable with a short hold
   - **Fences**: Keep at height ~20 — require a full jump
   - **Tall hay bales**: Keep at height ~18 — require a full jump
   - No new obstacle types are added.

### Acceptance Criteria

```
Given the dog is on the ground
When the player taps SPACE briefly (1-2 ticks)
Then the dog performs a short hop (~30% of max height)

Given the dog is on the ground
When the player holds SPACE for the full 15 ticks
Then the dog jumps to full height (same as current behavior)

Given the dog is mid-jump and still ascending
When the player releases SPACE
Then verticalVelocity is immediately set to 0 and the dog starts falling

Given the dog is mid-jump and already falling (past the arc peak)
When the player releases SPACE
Then nothing changes — the dog continues falling normally

Given the player holds SPACE for more than 15 ticks
When tick 16 fires
Then verticalVelocity is zeroed (same as release), no further effect from holding

Given a puddle obstacle approaches
When the player taps SPACE briefly
Then the dog clears the puddle with a short hop

Given a fence obstacle approaches
When the player taps SPACE briefly
Then the dog does NOT clear the fence and collides
```

### Edge Cases

| Scenario | Expected Behavior |
|----------|-------------------|
| Player releases and re-presses SPACE while airborne | No double-jump — the re-press is ignored until grounded |
| Player holds SPACE through entire jump arc | Velocity cut at tick 15, then normal gravity descent |
| Terminal drops key repeat events (laggy terminal) | Treated as release — velocity zeroed. Safe default. |
| Player presses UP instead of SPACE | Same behavior — both keys are equivalent jump triggers |
| Bark (B/DOWN) pressed while holding jump | Bark executes independently; jump hold continues |

## Anti-Requirements

- **No double jump**: The variable hold does NOT enable jumping again mid-air
- **No visual change**: Same `dogJumping` sprite regardless of jump height
- **No new obstacle types**: Only resize existing obstacles for height variety
- **No charge mechanic**: The dog launches immediately on press, not on release

## Performance Considerations

- One boolean flag (`jumpHeld`) and one counter (`jumpHoldTicks`) per tick — negligible overhead
- No changes to render pipeline

## Affected Components

- **Modified**: `Player.java` — add `jumpHeld` flag and `jumpHoldTicks` counter
- **Modified**: `Physics.java` — add velocity cut logic, adjust constants if needed
- **Modified**: `Game.java` — track whether jump key was received this tick, call velocity cut logic
- **Modified**: `Obstacle.java` — adjust `HayBale` wide variant height from 10 to 6
- **Unchanged**: `Renderer.java`, `Sprites.java`, `Spawner.java`, `Ground.java`, `Season.java`, `QalyRunner.java`
