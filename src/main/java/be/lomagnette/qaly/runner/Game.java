package be.lomagnette.qaly.runner;

import dev.tamboui.tui.event.KeyEvent;

import java.util.ArrayList;
import java.util.List;

public class Game {

    public enum State { TITLE, PLAYING, GAME_OVER }

    State state = State.TITLE;
    int score;
    float speed;
    Season season = Season.SPRING;
    Player player;
    Ground ground;
    List<Obstacle> obstacles = new ArrayList<>();
    Spawner spawner;
    int woofTimer;
    boolean quit;
    boolean jumpKeyThisTick;
    int canvasWidth = 160;
    int canvasHeight = 80;

    static final float BASE_SPEED = 1.5f;
    static final float SPEED_INCREMENT = 0.15f;
    static final float MAX_SPEED = 5.0f;

    public Game() {
        player = new Player();
        ground = new Ground();
        spawner = new Spawner();
    }

    private void reset() {
        score = 0;
        speed = BASE_SPEED;
        season = Season.SPRING;
        player = new Player();
        ground = new Ground();
        ground.setMaxX(canvasWidth);
        obstacles.clear();
        spawner = new Spawner();
        woofTimer = 0;
        jumpKeyThisTick = false;
    }

    public boolean handleEvent(KeyEvent k) {
        return switch (state) {
            case TITLE -> {
                if (k.isChar(' ')) {
                    state = State.PLAYING;
                    reset();
                    yield true;
                }
                if (k.isQuit()) {
                    quit = true;
                    yield false;
                }
                yield false;
            }
            case PLAYING -> {
                if (k.isChar(' ') || k.isUp()) {
                    if (player.grounded) {
                        Physics.jump(player);
                        player.startJumpHold();
                        jumpKeyThisTick = true;
                    } else if (player.jumpHeld) {
                        jumpKeyThisTick = true;
                    }
                    yield true;
                }
                if (k.isCharIgnoreCase('b') || k.isDown()) {
                    if (player.canBark()) {
                        player.bark();
                        woofTimer = 10;
                        System.out.print("\007");
                        obstacles.removeIf(o -> o.barkable() && Physics.inBarkRange(player, o));
                    }
                    yield true;
                }
                if (k.isQuit()) {
                    quit = true;
                    yield false;
                }
                yield false;
            }
            case GAME_OVER -> {
                if (k.isChar(' ')) {
                    state = State.PLAYING;
                    reset();
                    yield true;
                }
                if (k.isQuit()) {
                    quit = true;
                    yield false;
                }
                yield false;
            }
        };
    }

    public void tick() {
        if (state != State.PLAYING) return;

        score++;
        speed = Math.min(BASE_SPEED + (score / 50) * SPEED_INCREMENT, MAX_SPEED);
        season = Season.forScore(score);

        Physics.applyGravity(player);
        player.tickCooldown();
        player.tickJumpHold();
        Physics.checkJumpCut(player, jumpKeyThisTick);
        jumpKeyThisTick = false;

        ground.setSeason(season);
        ground.setMaxX(canvasWidth);
        ground.scroll(speed);

        // Scroll obstacles and remove off-screen ones
        obstacles.forEach(o -> o.scroll(speed));
        obstacles.removeIf(o -> o.x() + o.width() < -10);

        // Check collisions
        for (var o : obstacles) {
            if (Physics.collides(player, o)) {
                state = State.GAME_OVER;
                System.out.print("\007");
                return;
            }
        }

        // Spawn new obstacles
        spawner.maybeSpawn(score, speed, canvasWidth + 5)
                .ifPresent(obstacles::add);

        if (woofTimer > 0) woofTimer--;
    }

    public void updateCanvasSize(int width, int height) {
        this.canvasWidth = width;
        this.canvasHeight = height;
    }
}
