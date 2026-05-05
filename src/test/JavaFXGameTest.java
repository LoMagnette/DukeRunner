package be.lomagnette.qaly.runner;

import be.lomagnette.qaly.runner.fx.*;
import static be.lomagnette.qaly.runner.TestRunner.*;

public class JavaFXGameTest {

    // --- SeasonFX ---

    public void testSeasonFXProperties() {
        assertEquals("Java 1", SeasonFX.JAVA_1.label(), "JAVA_1 label");
        assertEquals("Java 5", SeasonFX.JAVA_5.label(), "JAVA_5 label");
        assertEquals("Java 11", SeasonFX.JAVA_11.label(), "JAVA_11 label");
        assertEquals("Java 21+", SeasonFX.JAVA_21.label(), "JAVA_21 label");
        assertTrue(SeasonFX.JAVA_1.accentColor() != null, "JAVA_1 accent color not null");
        assertTrue(SeasonFX.JAVA_5.accentColor() != null, "JAVA_5 accent color not null");
        assertTrue(SeasonFX.JAVA_11.accentColor() != null, "JAVA_11 accent color not null");
        assertTrue(SeasonFX.JAVA_21.accentColor() != null, "JAVA_21 accent color not null");
    }

    public void testSeasonFXForScoreCycles() {
        assertEquals(SeasonFX.JAVA_1, SeasonFX.forScore(0), "score 0 → JAVA_1");
        assertEquals(SeasonFX.JAVA_1, SeasonFX.forScore(50), "score 50 → JAVA_1");
        assertEquals(SeasonFX.JAVA_5, SeasonFX.forScore(100), "score 100 → JAVA_5");
        assertEquals(SeasonFX.JAVA_11, SeasonFX.forScore(200), "score 200 → JAVA_11");
        assertEquals(SeasonFX.JAVA_21, SeasonFX.forScore(300), "score 300 → JAVA_21");
        assertEquals(SeasonFX.JAVA_1, SeasonFX.forScore(400), "score 400 → cycles back to JAVA_1");
    }

    // --- PlayerFX ---

    public void testPlayerFXInitialState() {
        var p = new PlayerFX();
        assertEquals(PhysicsFX.GROUND_Y, p.y, "player starts at GROUND_Y");
        assertTrue(p.grounded, "player starts grounded");
        assertTrue(p.canThrow(), "player can throw initially");
        assertEquals(0.0f, p.verticalVelocity, "no initial velocity");
    }

    public void testPlayerFXThrowCooldown() {
        var p = new PlayerFX();
        assertTrue(p.canThrow(), "can throw initially");
        p.performThrow();
        assertFalse(p.canThrow(), "cannot throw during cooldown");
        assertEquals(PlayerFX.THROW_COOLDOWN_TICKS, p.throwCooldownTicks, "cooldown set");

        // Tick down the full cooldown
        for (int i = 0; i < PlayerFX.THROW_COOLDOWN_TICKS; i++) {
            p.tickCooldown();
        }
        assertTrue(p.canThrow(), "can throw after cooldown expires");
    }

    // --- PhysicsFX ---

    public void testPhysicsFXJump() {
        var p = new PlayerFX();
        assertTrue(p.grounded, "starts grounded");
        PhysicsFX.jump(p);
        assertFalse(p.grounded, "not grounded after jump");
        assertEquals(PhysicsFX.JUMP_VELOCITY, p.verticalVelocity, "jump velocity set");

        // Second jump should be ignored
        float velAfterFirst = p.verticalVelocity;
        PhysicsFX.jump(p);
        assertEquals(velAfterFirst, p.verticalVelocity, "second jump ignored");
    }

    public void testPhysicsFXGravity() {
        var p = new PlayerFX();
        PhysicsFX.jump(p);
        float initialY = p.y;

        // Apply gravity once — player should move up (velocity > gravity)
        PhysicsFX.applyGravity(p);
        assertTrue(p.y > initialY, "player moves up initially");

        // Apply gravity many times — player should land
        for (int i = 0; i < 100; i++) {
            PhysicsFX.applyGravity(p);
        }
        assertEquals(PhysicsFX.GROUND_Y, p.y, "player lands at GROUND_Y");
        assertTrue(p.grounded, "player is grounded after landing");
        assertEquals(0.0f, p.verticalVelocity, "velocity reset on landing");
    }

    public void testPhysicsFXCollision() {
        var p = new PlayerFX();
        // Place obstacle exactly at player position
        var obstacle = new ObstacleFX.ConferenceStage(PlayerFX.X);
        assertTrue(PhysicsFX.collides(p, obstacle), "collision when overlapping");

        // Place obstacle far away
        var farObstacle = new ObstacleFX.ConferenceStage(PlayerFX.X + PlayerFX.WIDTH + 100);
        assertFalse(PhysicsFX.collides(p, farObstacle), "no collision when far away");
    }

    public void testPhysicsFXThrowRange() {
        var p = new PlayerFX();
        // Throwable obstacle in range
        var intern = new ObstacleFX.ConfusedIntern(PlayerFX.X + PlayerFX.WIDTH + 10);
        assertTrue(PhysicsFX.inThrowRange(p, intern), "intern in throw range");

        // Throwable obstacle out of range
        var farIntern = new ObstacleFX.ConfusedIntern(PlayerFX.X + PlayerFX.WIDTH + PhysicsFX.THROW_RANGE + 20);
        assertFalse(PhysicsFX.inThrowRange(p, farIntern), "far intern out of throw range");

        // Non-throwable obstacle in range — should not be in throw range
        var stage = new ObstacleFX.ConferenceStage(PlayerFX.X + PlayerFX.WIDTH + 10);
        assertFalse(PhysicsFX.inThrowRange(p, stage), "non-throwable not in throw range");
    }

    // --- GroundFX ---

    public void testGroundFXInitHasDecorations() {
        var ground = new GroundFX();
        assertFalse(ground.decorations().isEmpty(), "ground should have decorations after init");
    }

    public void testGroundFXScrollMovesDecorations() {
        var ground = new GroundFX();
        float firstX = ground.decorations().getFirst().x();
        ground.scroll(2.0f);
        float afterX = ground.decorations().getFirst().x();
        assertTrue(afterX < firstX, "decoration x should decrease after scroll");
    }

    // --- SpawnerFX ---

    public void testSpawnerFXRespectsMinGap() {
        var spawner = new SpawnerFX();
        // At score 0, minGap = max(40, 80-0) = 80. No spawn should happen before 80 ticks.
        for (int i = 0; i < 40; i++) {
            assertTrue(spawner.maybeSpawn(0, 1.5f, 200).isEmpty(),
                    "should not spawn before minGap at tick " + i);
        }
    }

    public void testSpawnerFXNeverTwoThrowablesInRow() {
        var spawner = new SpawnerFX();
        boolean lastWasThrowable = false;
        // Force many spawns and check no two throwables in a row
        for (int i = 0; i < 10000; i++) {
            var result = spawner.maybeSpawn(500, 3.0f, 200);
            if (result.isPresent()) {
                boolean isThrowable = result.get().throwable();
                if (lastWasThrowable) {
                    assertFalse(isThrowable, "two throwables in a row at iteration " + i);
                }
                lastWasThrowable = isThrowable;
            }
        }
    }

    // --- GameFX ---

    public void testGameFXStartsInTitleState() {
        var game = new GameFX();
        assertEquals(GameFX.State.TITLE, game.state, "game starts in TITLE state");
    }

    public void testGameFXTickIncreasesScore() {
        var game = new GameFX();
        game.handleKeyPress(javafx.scene.input.KeyCode.SPACE); // start
        game.tick();
        game.tick();
        game.tick();
        assertTrue(game.score > 0, "score should increase after ticks");
    }

    public void testGameFXSpeedCapped() {
        var game = new GameFX();
        game.handleKeyPress(javafx.scene.input.KeyCode.SPACE);
        for (int i = 0; i < 5000; i++) {
            game.tick();
            if (game.state != GameFX.State.PLAYING) {
                // Reset if game over (collision)
                game.handleKeyPress(javafx.scene.input.KeyCode.SPACE);
            }
        }
        assertTrue(game.speed <= 5.0f, "speed should be capped at MAX_SPEED");
    }

    public void testGameFXEraCyclesWithScore() {
        var game = new GameFX();
        game.handleKeyPress(javafx.scene.input.KeyCode.SPACE);
        // Tick to score 100+ without collision (clear obstacles)
        for (int i = 0; i < 150; i++) {
            game.obstacles.clear(); // prevent collision
            game.tick();
        }
        assertTrue(game.score >= 100, "score should reach 100+");
        assertEquals(SeasonFX.JAVA_5, game.era, "era should be JAVA_5 at score 100+");
    }

    public void testGameFXCollisionEndsGame() {
        var game = new GameFX();
        game.handleKeyPress(javafx.scene.input.KeyCode.SPACE);
        game.tick(); // enter playing
        // Place obstacle directly on player
        game.obstacles.add(new ObstacleFX.ConferenceStage(PlayerFX.X));
        game.tick(); // should detect collision
        assertEquals(GameFX.State.GAME_OVER, game.state, "collision should trigger GAME_OVER");
    }

    // --- ObstacleFX ---

    public void testObstacleFXDimensions() {
        var cs = new ObstacleFX.ConferenceStage(100);
        assertEquals(6.0f, cs.width(), "conference stage width");
        assertEquals(26.0f, cs.height(), "conference stage height");
        assertFalse(cs.throwable(), "conference stage not throwable");

        var lsw = new ObstacleFX.LaptopStack(100, true);
        assertEquals(18.0f, lsw.width(), "laptop stack wide width");
        assertEquals(9.0f, lsw.height(), "laptop stack wide height");
        assertFalse(lsw.throwable(), "laptop stack not throwable");

        var lst = new ObstacleFX.LaptopStack(100, false);
        assertEquals(8.0f, lst.width(), "laptop stack tall width");
        assertEquals(25.0f, lst.height(), "laptop stack tall height");

        var csp = new ObstacleFX.CoffeeSpill(100);
        assertEquals(22.0f, csp.width(), "coffee spill width");
        assertEquals(6.0f, csp.height(), "coffee spill height");
        assertFalse(csp.throwable(), "coffee spill not throwable");

        var ci = new ObstacleFX.ConfusedIntern(100);
        assertEquals(14.0f, ci.width(), "confused intern width");
        assertEquals(14.0f, ci.height(), "confused intern height");
        assertTrue(ci.throwable(), "confused intern is throwable");

        var sbs = new ObstacleFX.SlowBuildServer(100);
        assertEquals(7.0f, sbs.width(), "slow build server width");
        assertEquals(10.0f, sbs.height(), "slow build server height");
        assertTrue(sbs.throwable(), "slow build server is throwable");
    }
}
