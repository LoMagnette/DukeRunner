package be.lomagnette.qaly.runner;

import static be.lomagnette.qaly.runner.TestRunner.*;

public class VariableJumpTest {

    // --- Player jump hold state ---

    public void testStartJumpHoldSetsFields() {
        var p = new Player();
        p.startJumpHold();
        assertTrue(p.jumpHeld, "jumpHeld should be true after startJumpHold");
        assertEquals(0, p.jumpHoldTicks, "jumpHoldTicks should be 0 after startJumpHold");
    }

    public void testTickJumpHoldIncrementsWhenHeld() {
        var p = new Player();
        p.startJumpHold();
        p.tickJumpHold();
        assertEquals(1, p.jumpHoldTicks, "jumpHoldTicks should be 1 after one tick");
        p.tickJumpHold();
        assertEquals(2, p.jumpHoldTicks, "jumpHoldTicks should be 2 after two ticks");
    }

    public void testTickJumpHoldNoOpWhenNotHeld() {
        var p = new Player();
        p.tickJumpHold();
        assertEquals(0, p.jumpHoldTicks, "jumpHoldTicks should stay 0 when not held");
    }

    public void testEndJumpHoldResetsFields() {
        var p = new Player();
        p.startJumpHold();
        p.tickJumpHold();
        p.tickJumpHold();
        p.endJumpHold();
        assertFalse(p.jumpHeld, "jumpHeld should be false after endJumpHold");
        assertEquals(0, p.jumpHoldTicks, "jumpHoldTicks should be 0 after endJumpHold");
    }

    // --- Physics.checkJumpCut ---

    public void testCheckJumpCutNoOpWhenNotInHold() {
        var p = new Player();
        Physics.jump(p);
        float velocityBefore = p.verticalVelocity;
        Physics.checkJumpCut(p, false);
        assertEquals(velocityBefore, p.verticalVelocity,
                "velocity should not change when not in jump hold");
    }

    public void testCheckJumpCutNoOpWhenPastPeak() {
        var p = new Player();
        Physics.jump(p);
        p.startJumpHold();
        // Simulate past peak: velocity already negative
        p.verticalVelocity = -1.0f;
        Physics.checkJumpCut(p, false);
        assertEquals(-1.0f, p.verticalVelocity,
                "velocity should not change when already past peak");
        assertFalse(p.jumpHeld, "jumpHeld should be cleared when past peak");
    }

    public void testCheckJumpCutZerosVelocityOnRelease() {
        var p = new Player();
        Physics.jump(p);
        p.startJumpHold();
        for (int i = 0; i < Physics.MIN_HOLD_TICKS; i++) {
            p.tickJumpHold();
        }
        // jumpKeyThisTick = false means released, past min hold
        Physics.checkJumpCut(p, false);
        assertEquals(0.0f, p.verticalVelocity,
                "velocity should be zeroed on key release after min hold");
        assertFalse(p.jumpHeld, "jumpHeld should be cleared on release");
    }

    public void testCheckJumpCutNoOpBeforeMinHold() {
        var p = new Player();
        Physics.jump(p);
        p.startJumpHold();
        p.tickJumpHold(); // 1 tick — below min
        float velocityBefore = p.verticalVelocity;
        Physics.checkJumpCut(p, false); // released, but under min hold
        assertEquals(velocityBefore, p.verticalVelocity,
                "velocity should not change before min hold ticks");
        assertTrue(p.jumpHeld, "jumpHeld should remain true before min hold");
    }

    public void testCheckJumpCutMaintainsVelocityWhileHolding() {
        var p = new Player();
        Physics.jump(p);
        p.startJumpHold();
        p.tickJumpHold(); // 1 tick
        float velocityBefore = p.verticalVelocity;
        // jumpKeyThisTick = true, under max ticks
        Physics.checkJumpCut(p, true);
        assertEquals(velocityBefore, p.verticalVelocity,
                "velocity should not change while holding under max ticks");
        assertTrue(p.jumpHeld, "jumpHeld should remain true while holding");
    }

    public void testCheckJumpCutZerosVelocityAtMaxHold() {
        var p = new Player();
        Physics.jump(p);
        p.startJumpHold();
        for (int i = 0; i < Physics.MAX_HOLD_TICKS; i++) {
            p.tickJumpHold();
        }
        // Still holding key, but at max ticks
        Physics.checkJumpCut(p, true);
        assertEquals(0.0f, p.verticalVelocity,
                "velocity should be zeroed at max hold ticks");
        assertFalse(p.jumpHeld, "jumpHeld should be cleared at max hold");
    }

    // --- Integration: hop height ---

    public void testQuickTapProducesShortHop() {
        var p = new Player();
        Physics.jump(p);
        p.startJumpHold();
        // Simulate 2 ticks of hold then release — velocity cut delayed until MIN_HOLD_TICKS
        Physics.applyGravity(p);
        p.tickJumpHold();
        Physics.checkJumpCut(p, true); // tick 1: holding
        Physics.applyGravity(p);
        p.tickJumpHold();
        Physics.checkJumpCut(p, false); // tick 2: released, but under min hold
        assertTrue(p.jumpHeld, "jumpHeld should still be true before min hold");
        // Continue ticking until min hold is reached and velocity is cut
        while (p.jumpHeld && p.verticalVelocity > 0) {
            Physics.applyGravity(p);
            p.tickJumpHold();
            Physics.checkJumpCut(p, false);
        }
        assertEquals(0.0f, p.verticalVelocity,
                "velocity should be 0 after min hold reached");
        float peakY = p.y;
        while (!p.grounded) {
            Physics.applyGravity(p);
        }
        // Peak should be below full jump peak but above trivial height
        float maxPeakY = calculateFullJumpPeak();
        assertTrue(peakY < maxPeakY * 0.6f,
                "quick tap peak (" + peakY + ") should be below full jump peak (" + maxPeakY + ")");
        assertTrue(peakY > Physics.GROUND_Y + 10,
                "quick tap peak (" + peakY + ") should clear low obstacles");
    }

    public void testFullHoldProducesFullJump() {
        var p = new Player();
        Physics.jump(p);
        p.startJumpHold();
        // Hold for all MAX_HOLD_TICKS
        for (int i = 0; i < Physics.MAX_HOLD_TICKS; i++) {
            Physics.applyGravity(p);
            p.tickJumpHold();
            Physics.checkJumpCut(p, true);
        }
        // On the tick after max, velocity gets zeroed
        Physics.applyGravity(p);
        p.tickJumpHold();
        Physics.checkJumpCut(p, true);
        float peakY = p.y;
        // Compare to natural arc peak (no cut at all)
        float naturalPeak = calculateFullJumpPeak();
        // Should be close to natural peak (within 20%)
        assertTrue(peakY > naturalPeak * 0.7f,
                "full hold peak (" + peakY + ") should be close to natural peak (" + naturalPeak + ")");
    }

    public void testNoDoubleJump() {
        var p = new Player();
        Physics.jump(p);
        assertFalse(p.grounded, "should not be grounded after jump");
        // Try to jump again mid-air
        Physics.jump(p);
        // Velocity should still be the same (jump only works when grounded)
        assertEquals(Physics.JUMP_VELOCITY, p.verticalVelocity,
                "velocity should not change from mid-air jump attempt");
    }

    // --- Key repeat delay regression ---

    public void testHoldJumpSurvivesKeyRepeatDelayGap() {
        // Reproduce: in a terminal, holding a key produces one event then
        // ~300-500ms of silence (OS key repeat delay) before repeats start.
        // At 30fps that's ~10 ticks with no key event. The jump must not
        // be cut short during this gap.
        var game = new Game();
        game.state = Game.State.PLAYING;

        // Simulate initial jump key press (as handleEvent would do)
        Physics.jump(game.player);
        game.player.startJumpHold();
        game.jumpKeyGraceTicks = Physics.JUMP_KEY_GRACE_TICKS;

        // Run 11 ticks: first tick has the grace active, remaining ticks
        // simulate the OS key repeat delay (no new key events arrive)
        for (int i = 0; i < 11; i++) {
            game.tick();
        }

        // With the bug: velocity cut at tick 5, player already falling, y ≈ 19
        // With fix: grace period bridges the gap, player still ascending, y ≈ 35
        assertTrue(game.player.y > Physics.GROUND_Y + 20,
                "held jump should maintain height through key repeat delay gap, y="
                + game.player.y + " (need >" + (Physics.GROUND_Y + 20) + ")");
    }

    public void testQuickTapStillProducesShortJump() {
        // A quick tap (one event, no repeats) should still produce a shorter
        // jump than a full hold, once the grace period expires.
        var tapGame = new Game();
        tapGame.state = Game.State.PLAYING;
        Physics.jump(tapGame.player);
        tapGame.player.startJumpHold();
        tapGame.jumpKeyGraceTicks = Physics.JUMP_KEY_GRACE_TICKS;

        // Let the grace expire and the jump get cut, then fall to ground
        float tapPeak = tapGame.player.y;
        while (!tapGame.player.grounded) {
            tapGame.tick();
            if (tapGame.player.y > tapPeak) tapPeak = tapGame.player.y;
        }

        // Full hold: refresh grace every few ticks (simulating key repeats)
        var holdGame = new Game();
        holdGame.state = Game.State.PLAYING;
        Physics.jump(holdGame.player);
        holdGame.player.startJumpHold();
        holdGame.jumpKeyGraceTicks = Physics.JUMP_KEY_GRACE_TICKS;

        float holdPeak = holdGame.player.y;
        int tick = 0;
        while (!holdGame.player.grounded) {
            // Simulate key repeat events arriving every ~3 ticks after initial delay
            if (tick >= 10 && tick % 3 == 0 && holdGame.player.jumpHeld) {
                holdGame.jumpKeyGraceTicks = Physics.JUMP_KEY_GRACE_TICKS;
            }
            holdGame.tick();
            if (holdGame.player.y > holdPeak) holdPeak = holdGame.player.y;
            tick++;
        }

        assertTrue(holdPeak > tapPeak + 2.0f,
                "full hold peak (" + holdPeak + ") should be taller than quick tap peak ("
                + tapPeak + ")");
    }

    // --- Obstacle dimensions ---

    public void testWideHayBaleHeight() {
        var bale = new Obstacle.HayBale(100, true);
        assertEquals(9.0f, bale.height(), "wide hay bale height should be 9");
    }

    public void testTallHayBaleHeight() {
        var bale = new Obstacle.HayBale(100, false);
        assertEquals(25.0f, bale.height(), "tall hay bale height should be 25");
    }

    public void testFenceHeight() {
        var fence = new Obstacle.Fence(100);
        assertEquals(26.0f, fence.height(), "fence height should be 26");
    }

    public void testPuddleHeight() {
        var puddle = new Obstacle.Puddle(100);
        assertEquals(6.0f, puddle.height(), "puddle height should be 6");
    }

    private float calculateFullJumpPeak() {
        // Simulate a full jump with no velocity cut
        var p = new Player();
        Physics.jump(p);
        float maxY = p.y;
        while (!p.grounded) {
            Physics.applyGravity(p);
            if (p.y > maxY) maxY = p.y;
        }
        return maxY;
    }
}
