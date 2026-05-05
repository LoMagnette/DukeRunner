package be.lomagnette.qaly.runner.tui;

import dev.tamboui.layout.Constraint;
import dev.tamboui.layout.Layout;
import dev.tamboui.layout.Rect;
import dev.tamboui.style.Color;
import dev.tamboui.style.Style;
import dev.tamboui.terminal.Frame;
import dev.tamboui.text.Text;
import dev.tamboui.widgets.canvas.Canvas;
import dev.tamboui.widgets.canvas.Marker;
import dev.tamboui.widgets.canvas.shapes.Points;
import dev.tamboui.widgets.paragraph.Paragraph;

import java.util.Random;

public final class Renderer {

    private Renderer() {}

    public static void render(Frame frame, Game game) {
        var area = frame.area();

        // Check minimum terminal size
        if (area.width() < 80 || area.height() < 24) {
            renderResizeMessage(frame, area);
            return;
        }

        switch (game.state) {
            case TITLE -> renderTitle(frame, area, game);
            case PLAYING -> renderGame(frame, area, game);
            case GAME_OVER -> renderGameOver(frame, area, game);
        }
    }

    private static void renderResizeMessage(Frame frame, Rect area) {
        var rows = Layout.vertical()
                .constraints(Constraint.fill(), Constraint.length(3), Constraint.fill())
                .split(area);
        var msg = Paragraph.builder()
                .text(Text.from("Please resize your terminal\n(minimum 80x24)"))
                .centered()
                .build();
        frame.renderWidget(msg, rows.get(1));
    }

    private static void renderTitle(Frame frame, Rect area, Game game) {
        var rows = Layout.vertical()
                .constraints(
                        Constraint.fill(),
                        Constraint.length(14),
                        Constraint.length(1),
                        Constraint.length(3),
                        Constraint.fill()
                )
                .split(area);

        // Duke art on Canvas
        int artWidth = Math.min(area.width(), 40);
        var artCols = Layout.horizontal()
                .constraints(Constraint.fill(), Constraint.length(artWidth), Constraint.fill())
                .split(rows.get(1));

        int cw = artCols.get(1).width() * 2;
        int ch = artCols.get(1).height() * 4;
        var artCanvas = Canvas.builder()
                .xBounds(0, cw)
                .yBounds(0, ch)
                .marker(Marker.BRAILLE)
                .paint(ctx -> {
                    var sitting = Sprites.dukeSitting();
                    double offsetX = Math.max(0, (cw - 38) / 2.0);
                    double offsetY = Math.max(0, (ch - 24) / 2.0);
                    ctx.draw(Points.of(
                            Sprites.translate(sitting.body(), offsetX, offsetY),
                            Color.WHITE));
                    if (sitting.accent().length > 0) {
                        ctx.draw(Points.of(
                                Sprites.translate(sitting.accent(), offsetX, offsetY),
                                Color.RED));
                    }
                })
                .build();
        frame.renderWidget(artCanvas, artCols.get(1));

        // Title text
        var titleText = Paragraph.builder()
                .text(Text.from("D U K E   R U N N E R")
                        .fg(Color.YELLOW))
                .centered()
                .build();
        frame.renderWidget(titleText, rows.get(2));

        // Instructions
        var instructions = Paragraph.builder()
                .text(Text.from("\nPress SPACE to start\nPress Q to quit"))
                .centered()
                .build();
        frame.renderWidget(instructions, rows.get(3));
    }

    private static void renderGame(Frame frame, Rect area, Game game) {
        var rows = Layout.vertical()
                .constraints(Constraint.length(1), Constraint.fill())
                .split(area);

        renderHud(frame, rows.get(0), game);
        renderGameCanvas(frame, rows.get(1), game);
    }

    private static void renderGameOver(Frame frame, Rect area, Game game) {
        var rows = Layout.vertical()
                .constraints(Constraint.length(1), Constraint.fill())
                .split(area);

        renderHud(frame, rows.get(0), game);
        renderGameCanvas(frame, rows.get(1), game);

        // Overlay game over text
        var overlayRows = Layout.vertical()
                .constraints(Constraint.fill(), Constraint.length(5), Constraint.fill())
                .split(rows.get(1));
        var overlayCols = Layout.horizontal()
                .constraints(Constraint.fill(), Constraint.length(30), Constraint.fill())
                .split(overlayRows.get(1));

        var gameOverText = Paragraph.builder()
                .text(Text.from("BUILD FAILED\n\nScore: " + game.score
                        + "\n\nSPACE to restart | Q to quit")
                        .fg(Color.RED))
                .centered()
                .style(Style.create().fg(Color.RED).bold())
                .build();
        frame.renderWidget(gameOverText, overlayCols.get(1));
    }

    private static void renderHud(Frame frame, Rect area, Game game) {
        // Use three-column layout for HUD: [bark] [season] [score]
        var cols = Layout.horizontal()
                .constraints(Constraint.fill(), Constraint.fill(), Constraint.fill())
                .split(area);

        String throwStatus = game.player.canThrow()
                ? "[THROW READY]"
                : "[CATCHING " + "\u00b7".repeat(Math.max(1, game.player.throwCooldownTicks / 10)) + "]";
        var throwColor = game.player.canThrow() ? Color.GREEN : Color.DARK_GRAY;

        var throwWidget = Paragraph.builder()
                .text(Text.from(throwStatus).fg(throwColor))
                .style(Style.create().bold())
                .build();
        frame.renderWidget(throwWidget, cols.get(0));

        var seasonWidget = Paragraph.builder()
                .text(Text.from(game.era.label()).fg(game.era.accentColor()))
                .centered()
                .build();
        frame.renderWidget(seasonWidget, cols.get(1));

        var scoreWidget = Paragraph.builder()
                .text(Text.from("Score: " + game.score).fg(Color.WHITE))
                .style(Style.create().bold())
                .right()
                .build();
        frame.renderWidget(scoreWidget, cols.get(2));
    }

    private static void renderGameCanvas(Frame frame, Rect gameArea, Game game) {
        int cw = gameArea.width() * 2;
        int ch = gameArea.height() * 4;
        game.updateCanvasSize(cw, ch);

        var canvas = Canvas.builder()
                .xBounds(0, cw)
                .yBounds(0, ch)
                .marker(Marker.BRAILLE)
                .paint(ctx -> {
                    paintGround(ctx, cw, game);
                    paintDecorations(ctx, game);
                    paintObstacles(ctx, game);
                    paintPlayer(ctx, game);
                    paintThrowEffect(ctx, game);
                    paintParticles(ctx, cw, ch, game.era, game.score);
                })
                .build();
        frame.renderWidget(canvas, gameArea);
    }

    private static void paintGround(dev.tamboui.widgets.canvas.Context ctx, int canvasWidth, Game game) {
        ctx.draw(new dev.tamboui.widgets.canvas.shapes.Line(
                0, Physics.GROUND_Y, canvasWidth, Physics.GROUND_Y, game.era.groundColor()));
    }

    private static void paintDecorations(dev.tamboui.widgets.canvas.Context ctx, Game game) {
        for (var deco : game.ground.decorations()) {
            double[][] sprite = switch (deco.type()) {
                case COFFEE_CUP -> Sprites.coffeeCup();
                case TERMINAL -> Sprites.terminal();
                case GIT_BRANCH -> Sprites.gitBranch();
                case IDE_ICON -> Sprites.ideIcon();
                case DOCKER_WHALE -> Sprites.dockerWhale();
                case CLOUD -> Sprites.cloud();
            };
            Color color = switch (deco.type()) {
                case COFFEE_CUP -> Color.rgb(139, 90, 43);
                case TERMINAL -> Color.GREEN;
                case GIT_BRANCH -> Color.GREEN;
                case IDE_ICON -> Color.CYAN;
                case DOCKER_WHALE -> Color.CYAN;
                case CLOUD -> Color.WHITE;
            };
            ctx.draw(Points.of(
                    Sprites.translate(sprite, deco.x(), Physics.GROUND_Y + 1),
                    color));
        }
    }

    private static void paintObstacles(dev.tamboui.widgets.canvas.Context ctx, Game game) {
        for (var obstacle : game.obstacles) {
            double[][] sprite = switch (obstacle.type()) {
                case CONFERENCE_STAGE -> Sprites.conferenceStage();
                case LAPTOP_STACK_WIDE -> Sprites.laptopStackWide();
                case LAPTOP_STACK_TALL -> Sprites.laptopStackTall();
                case COFFEE_SPILL -> Sprites.coffeeSpill();
                case CONFUSED_INTERN -> Sprites.confusedIntern();
                case SLOW_BUILD_SERVER -> Sprites.slowBuildServer();
            };
            Color color = switch (obstacle.type()) {
                case CONFERENCE_STAGE -> Color.rgb(139, 90, 43);
                case LAPTOP_STACK_WIDE, LAPTOP_STACK_TALL -> Color.rgb(180, 180, 190);
                case COFFEE_SPILL -> Color.rgb(139, 90, 43);
                case CONFUSED_INTERN -> Color.WHITE;
                case SLOW_BUILD_SERVER -> Color.rgb(100, 100, 110);
            };
            ctx.draw(Points.of(
                    Sprites.translate(sprite, obstacle.x(), obstacle.bottomY()),
                    color));
        }
    }

    private static void paintPlayer(dev.tamboui.widgets.canvas.Context ctx, Game game) {
        Sprites.CharSprite sprite;
        if (game.throwTimer > 5) {
            sprite = Sprites.dukeThrowing();
        } else if (!game.player.grounded) {
            sprite = Sprites.dukeJumping();
        } else {
            sprite = Sprites.dukeRunning();
        }
        double dx = Player.X - 10;
        double dy = game.player.y;
        ctx.draw(Points.of(Sprites.translate(sprite.body(), dx, dy), Color.WHITE));
        if (sprite.accent().length > 0) {
            ctx.draw(Points.of(Sprites.translate(sprite.accent(), dx, dy), Color.RED));
        }
    }

    private static void paintThrowEffect(dev.tamboui.widgets.canvas.Context ctx, Game game) {
        if (game.throwTimer > 0) {
            ctx.print(Player.X + Player.WIDTH + 3, game.player.y + Player.HEIGHT + 2, "throw new Exception()!");
        }
    }

    private static void paintParticles(dev.tamboui.widgets.canvas.Context ctx, int cw, int ch, Season era, int score) {
        int count;
        Color color;
        if (era == Season.JAVA_21) {
            count = 15;
            color = Color.MAGENTA;
        } else if (era == Season.JAVA_11) {
            count = 15;
            color = Color.CYAN;
        } else if (era == Season.JAVA_1) {
            count = 8;
            color = Color.rgb(139, 90, 43);
        } else if (era == Season.JAVA_5) {
            count = 8;
            color = Color.YELLOW;
        } else {
            return;
        }
        // Seed based on score to get stable-ish particles that drift slowly
        var rng = new Random(score / 3);
        var points = new double[count][2];
        for (int i = 0; i < count; i++) {
            double baseX = rng.nextDouble() * cw;
            double baseY = Physics.GROUND_Y + 10 + rng.nextDouble() * (ch - Physics.GROUND_Y - 15);
            // Slow drift: particles shift slightly each tick
            points[i][0] = (baseX + score * 0.3) % cw;
            points[i][1] = baseY - (score % 30) * 0.5;
            if (points[i][1] < Physics.GROUND_Y + 5) {
                points[i][1] = ch - 5 - rng.nextDouble() * 10;
            }
        }
        ctx.draw(Points.of(points, color));
    }
}
