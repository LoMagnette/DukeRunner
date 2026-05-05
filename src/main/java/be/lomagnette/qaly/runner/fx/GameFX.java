package be.lomagnette.qaly.runner.fx;

import javafx.scene.input.KeyCode;

import java.util.ArrayList;
import java.util.List;

public class GameFX {

    public enum State { TITLE, PLAYING, GAME_OVER }

    public State state = State.TITLE;
    public int score;
    public float speed;
    public SeasonFX era = SeasonFX.JAVA_1;
    public PlayerFX player;
    public GroundFX ground;
    public List<ObstacleFX> obstacles = new ArrayList<>();
    public SpawnerFX spawner;
    public int throwTimer;
    public boolean quit;
    public int canvasWidth = 800;
    public int canvasHeight = 400;
    public int animationFrame;
    public ParallaxBackground parallax = new ParallaxBackground();

    static final float BASE_SPEED = 1.5f;
    static final float SPEED_INCREMENT = 0.15f;
    static final float MAX_SPEED = 5.0f;

    public GameFX() {
        player = new PlayerFX();
        ground = new GroundFX();
        spawner = new SpawnerFX();
    }

    private void reset() {
        score = 0;
        speed = BASE_SPEED;
        era = SeasonFX.JAVA_1;
        player = new PlayerFX();
        ground = new GroundFX();
        ground.setMaxX(canvasWidth);
        obstacles.clear();
        spawner = new SpawnerFX();
        throwTimer = 0;
        animationFrame = 0;
        parallax.reset();
    }

    public boolean handleKeyPress(KeyCode code) {
        return switch (state) {
            case TITLE -> {
                if (code == KeyCode.SPACE) {
                    state = State.PLAYING;
                    reset();
                    yield true;
                }
                if (code == KeyCode.Q) {
                    quit = true;
                    yield false;
                }
                yield false;
            }
            case PLAYING -> {
                if (code == KeyCode.SPACE || code == KeyCode.UP) {
                    PhysicsFX.jump(player);
                    yield true;
                }
                if (code == KeyCode.B || code == KeyCode.DOWN) {
                    if (player.canThrow()) {
                        player.performThrow();
                        throwTimer = 10;
                        obstacles.removeIf(o -> o.throwable() && PhysicsFX.inThrowRange(player, o));
                    }
                    yield true;
                }
                if (code == KeyCode.Q) {
                    quit = true;
                    yield false;
                }
                yield false;
            }
            case GAME_OVER -> {
                if (code == KeyCode.SPACE) {
                    state = State.PLAYING;
                    reset();
                    yield true;
                }
                if (code == KeyCode.Q) {
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
        animationFrame++;
        speed = Math.min(BASE_SPEED + (score / 50) * SPEED_INCREMENT, MAX_SPEED);
        era = SeasonFX.forScore(score);

        PhysicsFX.applyGravity(player);
        player.tickCooldown();

        parallax.scroll(speed);

        ground.setSeason(era);
        ground.setMaxX(canvasWidth);
        ground.scroll(speed);

        obstacles.forEach(o -> o.scroll(speed));
        obstacles.removeIf(o -> o.x() + o.width() < -10);

        for (var o : obstacles) {
            if (PhysicsFX.collides(player, o)) {
                state = State.GAME_OVER;
                return;
            }
        }

        spawner.maybeSpawn(score, speed, canvasWidth + 5)
                .ifPresent(obstacles::add);

        if (throwTimer > 0) throwTimer--;
    }
}
