package be.lomagnette.duke.runner.tui;

import dev.tamboui.style.Color;
import static be.lomagnette.duke.runner.TestRunner.*;

public class JavaReskinTest {

    // --- Task 1: Season → Java eras ---

    public void testJava1EraProperties() {
        var era = Season.JAVA_1;
        assertEquals("Java 1", era.label(), "JAVA_1 label");
        assertEquals(Color.GREEN, era.accentColor(), "JAVA_1 accent");
        assertEquals(Color.rgb(34, 139, 34), era.groundColor(), "JAVA_1 ground");
    }

    public void testJava5EraProperties() {
        var era = Season.JAVA_5;
        assertEquals("Java 5", era.label(), "JAVA_5 label");
        assertEquals(Color.YELLOW, era.accentColor(), "JAVA_5 accent");
        assertEquals(Color.rgb(200, 170, 50), era.groundColor(), "JAVA_5 ground");
    }

    public void testJava11EraProperties() {
        var era = Season.JAVA_11;
        assertEquals("Java 11", era.label(), "JAVA_11 label");
        assertEquals(Color.CYAN, era.accentColor(), "JAVA_11 accent");
        assertEquals(Color.rgb(100, 130, 170), era.groundColor(), "JAVA_11 ground");
    }

    public void testJava21EraProperties() {
        var era = Season.JAVA_21;
        assertEquals("Java 21+", era.label(), "JAVA_21 label");
        assertEquals(Color.MAGENTA, era.accentColor(), "JAVA_21 accent");
        assertEquals(Color.rgb(140, 100, 160), era.groundColor(), "JAVA_21 ground");
    }

    public void testForScoreCyclesEras() {
        assertEquals(Season.JAVA_1, Season.forScore(0), "score 0 → JAVA_1");
        assertEquals(Season.JAVA_5, Season.forScore(100), "score 100 → JAVA_5");
        assertEquals(Season.JAVA_11, Season.forScore(200), "score 200 → JAVA_11");
        assertEquals(Season.JAVA_21, Season.forScore(300), "score 300 → JAVA_21");
        assertEquals(Season.JAVA_1, Season.forScore(400), "score 400 → cycles back to JAVA_1");
    }

    // --- Task 2: Decoration type renames ---

    public void testDecoTypeEnumValues() {
        // Verify all new DecoType values exist
        var types = Ground.DecoType.values();
        assertEquals(6, types.length, "should have 6 deco types");
        assertEquals(Ground.DecoType.COFFEE_CUP, types[0], "first deco type");
        assertEquals(Ground.DecoType.TERMINAL, types[1], "second deco type");
        assertEquals(Ground.DecoType.GIT_BRANCH, types[2], "third deco type");
        assertEquals(Ground.DecoType.IDE_ICON, types[3], "fourth deco type");
        assertEquals(Ground.DecoType.DOCKER_WHALE, types[4], "fifth deco type");
        assertEquals(Ground.DecoType.CLOUD, types[5], "sixth deco type");
    }

    // --- Task 3: Obstacle type renames ---

    public void testObstacleTypeEnumValues() {
        var types = Obstacle.ObstacleType.values();
        assertEquals(6, types.length, "should have 6 obstacle types");
        assertEquals(Obstacle.ObstacleType.CONFERENCE_STAGE, types[0], "first obstacle type");
        assertEquals(Obstacle.ObstacleType.LAPTOP_STACK_WIDE, types[1], "second obstacle type");
        assertEquals(Obstacle.ObstacleType.LAPTOP_STACK_TALL, types[2], "third obstacle type");
        assertEquals(Obstacle.ObstacleType.COFFEE_SPILL, types[3], "fourth obstacle type");
        assertEquals(Obstacle.ObstacleType.CONFUSED_INTERN, types[4], "fifth obstacle type");
        assertEquals(Obstacle.ObstacleType.SLOW_BUILD_SERVER, types[5], "sixth obstacle type");
    }

    public void testConferenceStageProperties() {
        var o = new Obstacle.ConferenceStage(100);
        assertEquals(6.0f, o.width(), "conference stage width");
        assertEquals(26.0f, o.height(), "conference stage height");
        assertFalse(o.throwable(), "conference stage not throwable");
    }

    public void testLaptopStackWideProperties() {
        var o = new Obstacle.LaptopStack(100, true);
        assertEquals(18.0f, o.width(), "laptop stack wide width");
        assertEquals(9.0f, o.height(), "laptop stack wide height");
        assertFalse(o.throwable(), "laptop stack not throwable");
    }

    public void testLaptopStackTallProperties() {
        var o = new Obstacle.LaptopStack(100, false);
        assertEquals(8.0f, o.width(), "laptop stack tall width");
        assertEquals(25.0f, o.height(), "laptop stack tall height");
    }

    public void testCoffeeSpillProperties() {
        var o = new Obstacle.CoffeeSpill(100);
        assertEquals(22.0f, o.width(), "coffee spill width");
        assertEquals(6.0f, o.height(), "coffee spill height");
    }

    public void testConfusedInternProperties() {
        var o = new Obstacle.ConfusedIntern(100);
        assertEquals(14.0f, o.width(), "confused intern width");
        assertEquals(14.0f, o.height(), "confused intern height");
        assertTrue(o.throwable(), "confused intern is throwable");
    }

    public void testSlowBuildServerProperties() {
        var o = new Obstacle.SlowBuildServer(100);
        assertEquals(7.0f, o.width(), "slow build server width");
        assertEquals(10.0f, o.height(), "slow build server height");
        assertTrue(o.throwable(), "slow build server is throwable");
    }

    // --- Task 4: bark → throw renames ---

    public void testPlayerThrowCooldown() {
        var p = new Player();
        assertTrue(p.canThrow(), "player should be able to throw initially");
        p.performThrow();
        assertFalse(p.canThrow(), "player should not throw during cooldown");
        assertEquals(Player.THROW_COOLDOWN_TICKS, p.throwCooldownTicks, "cooldown should be set");
    }

    public void testThrowRange() {
        var p = new Player();
        var intern = new Obstacle.ConfusedIntern(Player.X + Player.WIDTH + 10);
        assertTrue(Physics.inThrowRange(p, intern), "intern in throw range");

        var farIntern = new Obstacle.ConfusedIntern(Player.X + Player.WIDTH + Physics.THROW_RANGE + 10);
        assertFalse(Physics.inThrowRange(p, farIntern), "far intern out of throw range");
    }

    public void testConferenceStageNotThrowable() {
        var o = new Obstacle.ConferenceStage(100);
        assertFalse(o.throwable(), "conference stage not throwable");
    }

    // --- Task 5: Duke sprite + CharSprite record ---

    public void testCharSpriteRecord() {
        var duke = Sprites.dukeRunning();
        assertTrue(duke instanceof Sprites.CharSprite, "should be CharSprite");
        assertTrue(duke.body().length > 0, "duke running should have body points");
    }

    public void testDukeSpritesMethods() {
        var running = Sprites.dukeRunning();
        var jumping = Sprites.dukeJumping();
        var throwing = Sprites.dukeThrowing();
        var sitting = Sprites.dukeSitting();

        assertTrue(running.body().length > 0, "running body");
        assertTrue(jumping.body().length > 0, "jumping body");
        assertTrue(throwing.body().length > 0, "throwing body");
        assertTrue(sitting.body().length > 0, "sitting body");
        assertTrue(throwing.accent().length > 0, "throwing should have accent (red nose)");
    }

    // --- Task 6: New obstacle + decoration sprite shapes ---

    public void testObstacleSpriteShapes() {
        assertTrue(Sprites.conferenceStage().length > 0, "conference stage sprite");
        assertTrue(Sprites.laptopStackWide().length > 0, "laptop stack wide sprite");
        assertTrue(Sprites.laptopStackTall().length > 0, "laptop stack tall sprite");
        assertTrue(Sprites.coffeeSpill().length > 0, "coffee spill sprite");
        assertTrue(Sprites.confusedIntern().length > 0, "confused intern sprite");
        assertTrue(Sprites.slowBuildServer().length > 0, "slow build server sprite");
    }

    public void testDecorationSpriteShapes() {
        assertTrue(Sprites.coffeeCup().length > 0, "coffee cup sprite");
        assertTrue(Sprites.terminal().length > 0, "terminal sprite");
        assertTrue(Sprites.gitBranch().length > 0, "git branch sprite");
        assertTrue(Sprites.ideIcon().length > 0, "IDE icon sprite");
        assertTrue(Sprites.dockerWhale().length > 0, "docker whale sprite");
        assertTrue(Sprites.cloud().length > 0, "cloud sprite");
    }

    // --- Task 7: Visual polish verification ---

    public void testGameTitleIsDukeRunner() {
        assertEquals("D U K E   R U N N E R", Renderer.TITLE_TEXT, "launch title text");
    }

    // --- Task 8: Launch screen explains how to play ---

    public void testLaunchScreenExplainsJump() {
        var hint = Renderer.JUMP_HINT;
        assertTrue(hint.contains("SPACE"), "jump hint should name the SPACE key: " + hint);
        assertTrue(hint.toLowerCase().contains("jump"), "jump hint should say 'jump': " + hint);
    }

    public void testLaunchScreenExplainsThrow() {
        var hint = Renderer.THROW_HINT;
        assertTrue(hint.contains("B"), "throw hint should name the B key: " + hint);
        assertTrue(hint.toLowerCase().contains("throw"), "throw hint should say 'throw': " + hint);
    }

    public void testLaunchScreenExplainsStart() {
        assertTrue(Renderer.START_HINT.contains("SPACE to start"),
                "start hint should tell the player to press SPACE to start: " + Renderer.START_HINT);
    }
}
