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

    // --- Obstacle rebalancing ---

    public void testWideHayBaleHeightIs6() {
        var bale = new Obstacle.HayBale(100, true);
        assertEquals(6.0f, bale.height(), "wide hay bale height should be 6");
    }

    public void testTallHayBaleHeightUnchanged() {
        var bale = new Obstacle.HayBale(100, false);
        assertEquals(18.0f, bale.height(), "tall hay bale height should remain 18");
    }

    public void testFenceHeightUnchanged() {
        var fence = new Obstacle.Fence(100);
        assertEquals(20.0f, fence.height(), "fence height should remain 20");
    }

    public void testPuddleHeightUnchanged() {
        var puddle = new Obstacle.Puddle(100);
        assertEquals(3.0f, puddle.height(), "puddle height should remain 3");
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
