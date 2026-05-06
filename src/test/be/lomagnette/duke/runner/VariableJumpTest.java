package be.lomagnette.duke.runner;

import be.lomagnette.duke.runner.tui.Obstacle;
import be.lomagnette.duke.runner.tui.Physics;
import be.lomagnette.duke.runner.tui.Player;

import static be.lomagnette.duke.runner.TestRunner.*;

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

    public void testJumpClearanceWindowIsPlayable() {
        // Player.y must be above fence top for enough ticks that
        // the fence can scroll through with comfortable timing
        var p = new Player();
        Physics.jump(p);
        float fenceTop = Physics.GROUND_Y + 26; // tallest obstacle
        int clearanceTicks = 0;
        while (!p.grounded) {
            Physics.applyGravity(p);
            if (p.y >= fenceTop) clearanceTicks++;
        }
        // Fence width 6, base speed 1.5 → 4 ticks to cross.
        // Need at least 3x crossing time for playable timing at 60fps.
        assertTrue(clearanceTicks >= 12,
                "clearance window (" + clearanceTicks + " ticks) too tight, need >= 12");
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

    public void testWideLaptopStackHeight() {
        var stack = new Obstacle.LaptopStack(100, true);
        assertEquals(9.0f, stack.height(), "wide laptop stack height should be 9");
    }

    public void testTallLaptopStackHeight() {
        var stack = new Obstacle.LaptopStack(100, false);
        assertEquals(25.0f, stack.height(), "tall laptop stack height should be 25");
    }

    public void testConferenceStageHeight() {
        var stage = new Obstacle.ConferenceStage(100);
        assertEquals(26.0f, stage.height(), "conference stage height should be 26");
    }

    public void testCoffeeSpillHeight() {
        var spill = new Obstacle.CoffeeSpill(100);
        assertEquals(6.0f, spill.height(), "coffee spill height should be 6");
    }
}
