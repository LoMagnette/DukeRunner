package be.lomagnette.qaly.runner.fx;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public final class RendererFX {

    private RendererFX() {}

    static final double SCALE = SpritesFX.SCALE;
    static final Font HUD_FONT = Font.font("Monospaced", 16);
    static final Font TITLE_FONT = Font.font("Monospaced", 36);
    static final Font SUBTITLE_FONT = Font.font("Monospaced", 18);
    static final Font GAME_OVER_FONT = Font.font("Monospaced", 48);
    static final Font GAME_OVER_SUB_FONT = Font.font("Monospaced", 20);

    public static void render(GraphicsContext gc, GameFX game, double w, double h) {
        gc.save();
        switch (game.state) {
            case TITLE -> renderTitle(gc, game, w, h);
            case PLAYING -> renderGame(gc, game, w, h);
            case GAME_OVER -> renderGameOver(gc, game, w, h);
        }
        gc.restore();
    }

    private static void renderTitle(GraphicsContext gc, GameFX game, double w, double h) {
        // Background
        gc.setFill(Color.rgb(20, 20, 35));
        gc.fillRect(0, 0, w, h);

        // Duke sitting sprite centered
        Image duke = SpritesFX.dukeSitting();
        double dx = (w - duke.getWidth()) / 2;
        double dy = h * 0.15;
        gc.drawImage(duke, dx, dy);

        // Title text
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFont(TITLE_FONT);
        gc.setFill(Color.YELLOW);
        gc.fillText("D U K E   R U N N E R", w / 2, h * 0.6);

        // Instructions
        gc.setFont(SUBTITLE_FONT);
        gc.setFill(Color.WHITE);
        gc.fillText("Press SPACE to start", w / 2, h * 0.72);
        gc.setFill(Color.GRAY);
        gc.fillText("SPACE/UP = Jump  |  B/DOWN = Throw  |  Q = Quit", w / 2, h * 0.82);
    }

    private static void renderGame(GraphicsContext gc, GameFX game, double w, double h) {
        // Screen shake offset
        if (game.screenShake.isActive()) {
            gc.translate(game.screenShake.offsetX(), game.screenShake.offsetY());
        }

        // Parallax background (replaces flat sky)
        game.parallax.render(gc, game.era, w, h);

        // Ground with era transition blending
        double groundY = gameToScreenY(PhysicsFX.GROUND_Y, h);
        Color groundColor = game.era.groundColor();
        if (game.eraTransitionTicks > 0) {
            double t = (double) game.eraTransitionTicks / 30.0;
            groundColor = groundColor.interpolate(game.transitionFromEra.groundColor(), t);
        }
        gc.setFill(groundColor);
        gc.fillRect(0, groundY, w, h - groundY);

        // Ground line
        gc.setStroke(groundColor.brighter());
        gc.setLineWidth(2);
        gc.strokeLine(0, groundY, w, groundY);

        // Decorations
        renderDecorations(gc, game, h);

        // Obstacles
        renderObstacles(gc, game, h);

        // Player
        renderPlayer(gc, game, h);

        // Particles
        game.particles.render(gc, h);

        // Throw effect text
        if (game.throwTimer > 0) {
            gc.setFont(Font.font("Monospaced", 14));
            gc.setFill(Color.YELLOW);
            double tx = (PlayerFX.X + PlayerFX.WIDTH + 3) * SCALE;
            double ty = gameToScreenY(game.player.y + PlayerFX.HEIGHT + 2, h);
            gc.fillText("throw new Exception()!", tx, ty);
        }

        // HUD
        renderHud(gc, game, w);
    }

    private static void renderGameOver(GraphicsContext gc, GameFX game, double w, double h) {
        // Render game state underneath
        renderGame(gc, game, w, h);

        // Dark overlay
        gc.setFill(Color.rgb(0, 0, 0, 0.7));
        gc.fillRect(0, 0, w, h);

        // Game over text
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFont(GAME_OVER_FONT);
        gc.setFill(Color.RED);
        gc.fillText("BUILD FAILED", w / 2, h * 0.35);

        gc.setFont(TITLE_FONT);
        gc.setFill(Color.WHITE);
        gc.fillText("Score: " + game.score, w / 2, h * 0.5);

        gc.setFont(GAME_OVER_SUB_FONT);
        gc.setFill(Color.GRAY);
        gc.fillText("Press SPACE to restart  |  Q to quit", w / 2, h * 0.65);
    }

    private static void renderHud(GraphicsContext gc, GameFX game, double w) {
        gc.setFont(HUD_FONT);

        // Throw status (left)
        String throwStatus = game.player.canThrow()
                ? "[THROW READY]"
                : "[CATCHING " + "\u00b7".repeat(Math.max(1, game.player.throwCooldownTicks / 10)) + "]";
        gc.setFill(game.player.canThrow() ? Color.LIMEGREEN : Color.GRAY);
        gc.setTextAlign(TextAlignment.LEFT);
        gc.fillText(throwStatus, 10, 20);

        // Era label (center)
        gc.setFill(game.era.accentColor());
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText(game.era.label(), w / 2, 20);

        // Score (right)
        gc.setFill(Color.WHITE);
        gc.setTextAlign(TextAlignment.RIGHT);
        gc.fillText("Score: " + game.score, w - 10, 20);
    }

    private static void renderDecorations(GraphicsContext gc, GameFX game, double h) {
        for (var deco : game.ground.decorations()) {
            Image sprite = switch (deco.type()) {
                case COFFEE_CUP -> SpritesFX.coffeeCup();
                case TERMINAL -> SpritesFX.terminal();
                case GIT_BRANCH -> SpritesFX.gitBranch();
                case IDE_ICON -> SpritesFX.ideIcon();
                case DOCKER_WHALE -> SpritesFX.dockerWhale();
                case CLOUD -> SpritesFX.cloud();
            };
            double dx = deco.x() * SCALE;
            double dy = gameToScreenY(PhysicsFX.GROUND_Y + 1, h) - sprite.getHeight();
            gc.drawImage(sprite, dx, dy);
        }
    }

    private static void renderObstacles(GraphicsContext gc, GameFX game, double h) {
        for (var obstacle : game.obstacles) {
            Image sprite = switch (obstacle.type()) {
                case CONFERENCE_STAGE -> SpritesFX.conferenceStage();
                case LAPTOP_STACK_WIDE -> SpritesFX.laptopStackWide();
                case LAPTOP_STACK_TALL -> SpritesFX.laptopStackTall();
                case COFFEE_SPILL -> SpritesFX.coffeeSpill();
                case CONFUSED_INTERN -> SpritesFX.confusedIntern();
                case SLOW_BUILD_SERVER -> SpritesFX.slowBuildServer();
            };
            double dx = obstacle.x() * SCALE;
            double dy = gameToScreenY(obstacle.bottomY() + obstacle.height(), h);
            gc.drawImage(sprite, dx, dy);
        }
    }

    private static void renderPlayer(GraphicsContext gc, GameFX game, double h) {
        Image sprite;
        if (game.throwTimer > 5) {
            sprite = SpritesFX.dukeThrowing();
        } else if (!game.player.grounded) {
            sprite = SpritesFX.dukeJumping();
        } else {
            sprite = game.animationFrame % 16 < 8
                    ? SpritesFX.dukeRunning()
                    : SpritesFX.dukeRunning2();
        }
        double dx = (PlayerFX.X - 10) * SCALE;
        double dy = gameToScreenY(game.player.y + PlayerFX.HEIGHT, h);
        gc.drawImage(sprite, dx, dy);
    }

    static double gameToScreenY(double gameY, double screenH) {
        // Game: Y=0 is bottom, Y increases upward
        // Screen: Y=0 is top, Y increases downward
        // Ground is at gameY=8, which should be near the bottom of the screen
        return screenH - gameY * SCALE;
    }

}
