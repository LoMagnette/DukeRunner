package be.lomagnette.qaly.runner.fx;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.HashSet;
import java.util.Set;

public class QalyRunnerFX extends Application {

    private static final double WIDTH = 800;
    private static final double HEIGHT = 400;
    private static final long TICK_NS = 16_000_000L; // ~16ms per tick

    private final GameFX game = new GameFX();
    private final Set<KeyCode> pressedKeys = new HashSet<>();

    @Override
    public void start(Stage stage) {
        var canvas = new Canvas(WIDTH, HEIGHT);
        var gc = canvas.getGraphicsContext2D();

        var root = new Pane(canvas);
        var scene = new Scene(root, WIDTH, HEIGHT);

        scene.setOnKeyPressed(e -> {
            KeyCode code = e.getCode();
            if (!pressedKeys.contains(code)) {
                pressedKeys.add(code);
                game.handleKeyPress(code);
                if (game.quit) {
                    Platform.exit();
                }
            }
        });

        scene.setOnKeyReleased(e -> pressedKeys.remove(e.getCode()));

        stage.setTitle("Duke Runner");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();

        // Fixed-timestep game loop
        new AnimationTimer() {
            private long lastTime = 0;
            private long accumulator = 0;

            @Override
            public void handle(long now) {
                if (lastTime == 0) {
                    lastTime = now;
                    return;
                }

                long delta = now - lastTime;
                lastTime = now;
                accumulator += delta;

                // Cap at 5 ticks per frame to prevent spiral of death
                int ticks = 0;
                while (accumulator >= TICK_NS && ticks < 5) {
                    game.tick();
                    accumulator -= TICK_NS;
                    ticks++;
                }

                RendererFX.render(gc, game, WIDTH, HEIGHT);
            }
        }.start();
    }
}
