package be.lomagnette.qaly.runner;

import static be.lomagnette.qaly.runner.TestRunner.*;

public class VariableJumpTest {

    // --- Single jump physics ---

    public void testJumpClearsTallestObstacle() {
        var p = new Player();
        Physics.jump(p);
        float maxY = p.y;
        while (!p.grounded) {
            Physics.applyGravity(p);
            if (p.y > maxY) maxY = p.y;
        }
        float fenceTop = Physics.GROUND_Y + 26; // tallest obstacle
        assertTrue(maxY > fenceTop,
                "jump peak (" + maxY + ") should clear fence top (" + fenceTop + ")");
    }

    public void testJumpArcIsSnappy() {
        var p = new Player();
        Physics.jump(p);
        int ticks = 0;
        while (!p.grounded) {
            Physics.applyGravity(p);
            ticks++;
        }
        // At 60fps (16ms ticks), a snappy jump should complete under 30 ticks (0.5s)
        assertTrue(ticks < 30,
                "jump should be snappy, took " + ticks + " ticks (want < 30)");
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

    public void testJumpLandsBackOnGround() {
        var p = new Player();
        Physics.jump(p);
        while (!p.grounded) {
            Physics.applyGravity(p);
        }
        assertEquals(Physics.GROUND_Y, p.y,
                "player should land back at ground level");
        assertEquals(0.0f, p.verticalVelocity,
                "velocity should be 0 after landing");
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
}
