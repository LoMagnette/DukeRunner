package be.lomagnette.duke.runner.tui;

import dev.tamboui.image.Image;
import dev.tamboui.image.ImageData;
import dev.tamboui.image.ImageScaling;
import dev.tamboui.image.capability.TerminalImageCapabilities;
import dev.tamboui.image.capability.TerminalImageProtocol;
import dev.tamboui.layout.Constraint;
import dev.tamboui.layout.Layout;
import dev.tamboui.layout.Rect;
import dev.tamboui.style.Color;
import dev.tamboui.style.Style;
import dev.tamboui.terminal.Frame;
import dev.tamboui.text.Text;
import dev.tamboui.tfx.Effect;
import dev.tamboui.tfx.ExpandDirection;
import dev.tamboui.tfx.Fx;
import dev.tamboui.tfx.Interpolation;
import dev.tamboui.tfx.TFxDuration;
import dev.tamboui.widgets.block.Block;
import dev.tamboui.widgets.block.BorderType;
import dev.tamboui.widgets.block.Borders;
import dev.tamboui.widgets.block.Title;
import dev.tamboui.widgets.Clear;
import dev.tamboui.widgets.canvas.Canvas;
import dev.tamboui.widgets.canvas.Context;
import dev.tamboui.widgets.canvas.Marker;
import dev.tamboui.widgets.canvas.shapes.Line;
import dev.tamboui.widgets.canvas.shapes.Points;
import dev.tamboui.widgets.gauge.LineGauge;
import dev.tamboui.widgets.paragraph.Paragraph;
import dev.tamboui.widgets.sparkline.Sparkline;

import java.util.Random;

/**
 * Renders the game to a Tamboui {@link Frame}.
 *
 * <p>Instance state (kept out of the game model) covers the view-only concerns:
 * a reused RNG, the rolling speed history behind the HUD sparkline, the lazily
 * loaded title image, per-frame timing for text effects, and the active TFX
 * transition effects.
 */
public final class Renderer {

    private static final int SPEED_HISTORY = 64;

    // Launch-screen copy. Kept as constants (rather than inline literals) so the
    // launch screen's wording — including the how-to-play controls — is covered
    // by tests without having to render to a buffer.
    public static final String TITLE_TEXT = "D U K E   R U N N E R";
    public static final String TAGLINE = "Outrun the build failures";
    public static final String CONTROLS_HEADING = "How to play";
    public static final String JUMP_HINT = "↑ / SPACE  —  jump over obstacles";
    public static final String THROW_HINT = "↓ / B  —  throw an exception to clear the path";
    public static final String START_HINT = "Press SPACE to start   ·   Q to quit";

    private final long[] speedHistory = new long[SPEED_HISTORY];
    private final Random particleRng = new Random();
    private final Background background = new Background();
    private final Particles particles = new Particles();
    private final ScreenShake shake = new ScreenShake();

    // Edge-detection for event particles (fire once on the transition).
    private boolean prevGrounded = true;
    private boolean prevThrowActive;

    private Image titleImage;
    private boolean titleImageLoaded;

    // Timing + transition effects.
    private long lastNanos;
    private long lastDtMs = 16;
    private Game.State prevState;
    private Effect titleFx;
    private Effect gameOverFx;

    public void render(Frame frame, Game game) {
        long now = System.nanoTime();
        lastDtMs = lastNanos == 0 ? 16 : Math.max(1, Math.min(100, (now - lastNanos) / 1_000_000));
        lastNanos = now;

        // View-only animation state advances every frame regardless of ticks.
        particles.update(lastDtMs);
        shake.tick();

        // Kick off a transition effect the frame the state changes.
        if (game.state != prevState) {
            switch (game.state) {
                case TITLE -> titleFx = Fx.expand(
                        ExpandDirection.HORIZONTAL,
                        Style.create().fg(Color.YELLOW).bold(),
                        650L, Interpolation.QuadOut);
                case GAME_OVER -> {
                    gameOverFx = Fx.coalesce(650L, Interpolation.QuadOut);
                    shake.trigger(10f); // crash punch
                }
                case PLAYING -> {
                    background.reset();
                    particles.clear();
                    shake.reset();
                    prevGrounded = true;
                    prevThrowActive = false;
                }
            }
            prevState = game.state;
        }

        var area = frame.area();

        // Tamboui diffs against the previous frame and only repaints changed
        // cells, so any cell a state leaves untouched keeps its old content.
        // The title/resize screens don't paint their whole area, which let a
        // previous game's HUD and playfield bleed through. Clear every frame so
        // each state starts from a blank buffer.
        frame.renderWidget(Clear.clear(), area);

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

    private void renderResizeMessage(Frame frame, Rect area) {
        var rows = Layout.vertical()
                .constraints(Constraint.fill(), Constraint.length(3), Constraint.fill())
                .split(area);
        var msg = Paragraph.builder()
                .text(Text.from("Please resize your terminal\n(minimum 80x24)"))
                .centered()
                .build();
        frame.renderWidget(msg, rows.get(1));
    }

    private void renderTitle(Frame frame, Rect area, Game game) {
        var rows = Layout.vertical()
                .constraints(
                        Constraint.fill(),
                        Constraint.length(14),  // mascot
                        Constraint.length(1),   // title
                        Constraint.length(1),   // tagline
                        Constraint.length(6),   // how-to-play + start/quit
                        Constraint.fill()
                )
                .split(area);

        // Mascot: use the raster image only on terminals with a true graphics
        // protocol (see titleImage()); otherwise fall back to the hand-drawn
        // braille Duke, which renders cleanly everywhere.
        int artWidth = Math.min(area.width(), 40);
        var artCols = Layout.horizontal()
                .constraints(Constraint.fill(), Constraint.length(artWidth), Constraint.fill())
                .split(rows.get(1));

        Image image = titleImage();
        if (image != null) {
            frame.renderWidget(image, artCols.get(1));
        } else {
            renderTitleSprite(frame, artCols.get(1));
        }

        // Title text (animated in via the expand effect)
        var titleArea = rows.get(2);
        var titleText = Paragraph.builder()
                .text(Text.from(TITLE_TEXT)
                        .fg(Color.YELLOW))
                .centered()
                .build();
        frame.renderWidget(titleText, titleArea);
        if (titleFx != null && !titleFx.done()) {
            titleFx.process(TFxDuration.fromMillis(lastDtMs), frame.buffer(), titleArea);
        }

        // Tagline
        var tagline = Paragraph.builder()
                .text(Text.from(TAGLINE).fg(Color.DARK_GRAY))
                .centered()
                .build();
        frame.renderWidget(tagline, rows.get(3));

        // How to play + start/quit. Split into sub-rows so the heading and the
        // start prompt can carry their own colour while every line stays
        // self-contained (and therefore readable when centered).
        var infoRows = Layout.vertical()
                .constraints(
                        Constraint.length(1),  // heading
                        Constraint.length(1),  // jump
                        Constraint.length(1),  // throw
                        Constraint.length(1),  // spacer
                        Constraint.length(1)   // start / quit
                )
                .split(rows.get(4));

        var heading = Paragraph.builder()
                .text(Text.from(CONTROLS_HEADING).fg(Color.YELLOW))
                .centered()
                .build();
        var jump = Paragraph.builder()
                .text(Text.from(JUMP_HINT).fg(Color.WHITE))
                .centered()
                .build();
        var throwLine = Paragraph.builder()
                .text(Text.from(THROW_HINT).fg(Color.WHITE))
                .centered()
                .build();
        var startQuit = Paragraph.builder()
                .text(Text.from(START_HINT).fg(Color.GREEN))
                .centered()
                .build();
        frame.renderWidget(heading, infoRows.get(0));
        frame.renderWidget(jump, infoRows.get(1));
        frame.renderWidget(throwLine, infoRows.get(2));
        frame.renderWidget(startQuit, infoRows.get(4));
    }

    private void renderTitleSprite(Frame frame, Rect artArea) {
        int cw = artArea.width() * 2;
        int ch = artArea.height() * 4;
        var artCanvas = Canvas.builder()
                .xBounds(0, cw)
                .yBounds(0, ch)
                .marker(Marker.HALF_BLOCK)
                .paint(ctx -> {
                    var sitting = Sprites.dukeSitting();
                    double offsetX = Math.max(0, (cw - Sprites.DUKE_W) / 2.0);
                    double offsetY = Math.max(0, (ch - Sprites.DUKE_H) / 2.0);
                    for (var layer : sitting.layers()) {
                        if (layer.points().length == 0) continue;
                        ctx.draw(Points.of(
                                Sprites.translate(layer.points(), offsetX, offsetY),
                                layer.color()));
                    }
                })
                .build();
        frame.renderWidget(artCanvas, artArea);
    }

    private void renderGame(Frame frame, Rect area, Game game) {
        var rows = Layout.vertical()
                .constraints(Constraint.length(3), Constraint.fill())
                .split(area);

        renderHud(frame, rows.get(0), game);
        renderGameFrame(frame, rows.get(1), game);
    }

    private void renderGameOver(Frame frame, Rect area, Game game) {
        var rows = Layout.vertical()
                .constraints(Constraint.length(3), Constraint.fill())
                .split(area);

        renderHud(frame, rows.get(0), game);
        renderGameFrame(frame, rows.get(1), game);

        // Overlay game over text
        var overlayRows = Layout.vertical()
                .constraints(Constraint.fill(), Constraint.length(5), Constraint.fill())
                .split(rows.get(1));
        var overlayCols = Layout.horizontal()
                .constraints(Constraint.fill(), Constraint.length(30), Constraint.fill())
                .split(overlayRows.get(1));
        var overlayArea = overlayCols.get(1);

        var gameOverText = Paragraph.builder()
                .text(Text.from("BUILD FAILED\n\nScore: " + game.score
                        + "\n\nSPACE to restart | Q to quit")
                        .fg(Color.RED))
                .centered()
                .style(Style.create().fg(Color.RED).bold())
                .build();
        frame.renderWidget(gameOverText, overlayArea);
        if (gameOverFx != null && !gameOverFx.done()) {
            gameOverFx.process(TFxDuration.fromMillis(lastDtMs), frame.buffer(), overlayArea);
        }
    }

    private void renderHud(Frame frame, Rect area, Game game) {
        Color accent = Season.accentColorFor(game.score);
        var block = Block.builder()
                .borders(Borders.ALL)
                .borderType(BorderType.ROUNDED)
                .borderColor(accent)
                .build();
        Rect inner = block.inner(area);
        frame.renderWidget(block, area);

        var cols = Layout.horizontal()
                .constraints(
                        Constraint.length(22),  // throw meter
                        Constraint.fill(),      // speed sparkline
                        Constraint.length(14),  // era
                        Constraint.length(14)   // score
                )
                .split(inner);

        // Throw cooldown as a real gauge (fills back up to READY).
        boolean ready = game.player.canThrow();
        double ratio = 1.0 - (game.player.throwCooldownTicks / (double) Player.THROW_COOLDOWN_TICKS);
        var gauge = LineGauge.builder()
                .ratio(ratio)
                .label(ready ? "THROW" : "catching")
                .filledColor(ready ? Color.GREEN : Color.YELLOW)
                .unfilledColor(Color.DARK_GRAY)
                .build();
        frame.renderWidget(gauge, cols.get(0));

        // Rolling speed history.
        pushSpeed(Math.round(game.speed * 10));
        var speedline = Sparkline.builder()
                .data(speedHistory)
                .max(Math.round(Game.MAX_SPEED * 10))
                .foreground(accent)
                .build();
        frame.renderWidget(speedline, cols.get(1));

        var seasonWidget = Paragraph.builder()
                .text(Text.from(game.era.label()).fg(accent))
                .centered()
                .build();
        frame.renderWidget(seasonWidget, cols.get(2));

        var scoreWidget = Paragraph.builder()
                .text(Text.from("Score: " + game.score).fg(Color.WHITE))
                .style(Style.create().bold())
                .right()
                .build();
        frame.renderWidget(scoreWidget, cols.get(3));
    }

    private void renderGameFrame(Frame frame, Rect area, Game game) {
        Color accent = Season.accentColorFor(game.score);
        var block = Block.builder()
                .borders(Borders.ALL)
                .borderType(BorderType.ROUNDED)
                .borderColor(accent)
                .title(Title.from(" " + game.era.label() + " ").centered())
                .build();
        Rect inner = block.inner(area);
        frame.renderWidget(block, area);
        renderGameCanvas(frame, inner, game);
    }

    private void renderGameCanvas(Frame frame, Rect gameArea, Game game) {
        int cw = gameArea.width() * 2;
        int ch = gameArea.height() * 4;
        game.updateCanvasSize(cw, ch);

        Color sky = Season.skyColorFor(game.score);
        Color horizon = Season.horizonColorFor(game.score);
        Color ground = Season.groundColorFor(game.score);

        // Advance parallax and emit event particles only while playing so the
        // scene freezes (but existing particles still settle) on game over.
        if (game.state == Game.State.PLAYING) {
            background.scroll(game.speed);
            if (!prevGrounded && game.player.grounded) {
                particles.emitDust(Player.X, Physics.GROUND_Y + 1, ground);
            }
            boolean throwActive = game.throwTimer > 0;
            if (throwActive && !prevThrowActive) {
                particles.emitSparks(Player.X + Player.WIDTH + 2,
                        game.player.y + Player.HEIGHT, Color.YELLOW);
            }
            prevThrowActive = throwActive;
            prevGrounded = game.player.grounded;
        }

        // Screen shake nudges the world within the fixed frame (bounds offset).
        double sx = shake.offsetX();
        double sy = shake.offsetY();

        var canvas = Canvas.builder()
                .xBounds(-sx, cw - sx)
                .yBounds(-sy, ch - sy)
                .marker(Marker.HALF_BLOCK)
                .backgroundColor(sky)
                .paint(ctx -> {
                    paintStars(ctx, cw, ch, horizon);
                    background.render(ctx, cw, ch, Physics.GROUND_Y, game.score);
                    paintTerrain(ctx, cw, ground, horizon);
                    paintDecorations(ctx, game);
                    paintObstacles(ctx, game);
                    paintPlayer(ctx, game);
                    paintThrowEffect(ctx, game);
                    paintParticles(ctx, cw, ch, game.era, game.score);
                    particles.render(ctx);
                })
                .build();
        frame.renderWidget(canvas, gameArea);
    }

    private void paintStars(Context ctx, int cw, int ch, Color color) {
        // Stable field seeded once — stars should twinkle in place, not scatter.
        particleRng.setSeed(1234567);
        int count = 24;
        var points = new double[count][2];
        int skyBase = (int) (Physics.GROUND_Y + 12);
        for (int i = 0; i < count; i++) {
            points[i][0] = particleRng.nextDouble() * cw;
            points[i][1] = skyBase + particleRng.nextDouble() * Math.max(1, ch - skyBase - 2);
        }
        ctx.draw(Points.of(points, darken(color, 0.6)));
    }

    private void paintTerrain(Context ctx, int canvasWidth, Color ground, Color horizon) {
        // Fill the strip below the ground line so it reads as solid terrain,
        // then draw a brighter lit edge along the top.
        Color deep = darken(ground, 0.5);
        for (int y = 0; y < (int) Physics.GROUND_Y; y++) {
            float t = y / Physics.GROUND_Y;
            ctx.draw(new Line(0, y, canvasWidth, y, Season.lerp(deep, ground, t)));
        }
        ctx.draw(new Line(0, Physics.GROUND_Y, canvasWidth, Physics.GROUND_Y, horizon));
    }

    private void paintDecorations(Context ctx, Game game) {
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
            // Decorations are background scenery — dim them so the bright,
            // multi-color obstacles clearly read as the foreground hazards.
            ctx.draw(Points.of(
                    Sprites.translate(sprite, deco.x(), Physics.GROUND_Y + 1),
                    darken(color, 0.45)));
        }
    }

    private void paintObstacles(Context ctx, Game game) {
        for (var obstacle : game.obstacles) {
            Sprites.CharSprite sprite = switch (obstacle.type()) {
                case CONFERENCE_STAGE -> Sprites.conferenceStage();
                case LAPTOP_STACK_WIDE -> Sprites.laptopStackWide();
                case LAPTOP_STACK_TALL -> Sprites.laptopStackTall();
                case COFFEE_SPILL -> Sprites.coffeeSpill();
                case CONFUSED_INTERN -> Sprites.confusedIntern();
                case SLOW_BUILD_SERVER -> Sprites.slowBuildServer();
            };
            for (var layer : sprite.layers()) {
                if (layer.points().length == 0) continue;
                ctx.draw(Points.of(
                        Sprites.translate(layer.points(), obstacle.x(), obstacle.bottomY()),
                        layer.color()));
            }
        }
    }

    private void paintPlayer(Context ctx, Game game) {
        Sprites.CharSprite sprite;
        double bob = 0.0;
        if (game.throwTimer > 5) {
            sprite = Sprites.dukeThrowing();
        } else if (!game.player.grounded) {
            sprite = Sprites.dukeJumping();
        } else {
            int runFrame = game.score / 4;
            sprite = Sprites.dukeRunning(runFrame);
            // Tiny vertical bob synced to the stride for a sense of gait.
            bob = (runFrame & 1) == 1 ? 1.0 : 0.0;
        }
        // Centre Duke's grid on the collision box [Player.X, Player.X+WIDTH]:
        // align the grid's centre column (DUKE_CX) with the box centre so the
        // visible Duke and his hitbox coincide.
        double dx = Player.X + Player.WIDTH / 2 - Sprites.DUKE_CX;
        double dy = game.player.y + bob;
        for (var layer : sprite.layers()) {
            if (layer.points().length == 0) continue;
            ctx.draw(Points.of(Sprites.translate(layer.points(), dx, dy), layer.color()));
        }
    }

    private void paintThrowEffect(Context ctx, Game game) {
        if (game.throwTimer > 0) {
            ctx.print(Player.X + Player.WIDTH + 3, game.player.y + Player.HEIGHT + 2, "throw new Exception()!");
        }
    }

    private void paintParticles(Context ctx, int cw, int ch, Season era, int score) {
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
        particleRng.setSeed(score / 3);
        var points = new double[count][2];
        for (int i = 0; i < count; i++) {
            double baseX = particleRng.nextDouble() * cw;
            double baseY = Physics.GROUND_Y + 10 + particleRng.nextDouble() * (ch - Physics.GROUND_Y - 15);
            // Slow drift: particles shift slightly each tick
            points[i][0] = (baseX + score * 0.3) % cw;
            points[i][1] = baseY - (score % 30) * 0.5;
            if (points[i][1] < Physics.GROUND_Y + 5) {
                points[i][1] = ch - 5 - particleRng.nextDouble() * 10;
            }
        }
        ctx.draw(Points.of(points, color));
    }

    // ── Helpers ─────────────────────────────────────────────────────

    private Image titleImage() {
        if (!titleImageLoaded) {
            titleImageLoaded = true;
            try {
                // Only use the raster mascot when the terminal offers a true
                // graphics protocol (Kitty/iTerm/Sixel). On everything else the
                // best support is HALF_BLOCK/BRAILLE, which renders the image as
                // a muddy, garbled block — the hand-drawn braille Duke reads far
                // better there, so we leave titleImage null and let it take over.
                var caps = TerminalImageCapabilities.detect();
                var best = caps.bestSupport();
                if (best == TerminalImageProtocol.KITTY
                        || best == TerminalImageProtocol.ITERM2
                        || best == TerminalImageProtocol.SIXEL) {
                    titleImage = Image.builder()
                            .data(ImageData.fromResource("/bouvier.png"))
                            .scaling(ImageScaling.FIT)
                            .protocol(caps.bestProtocol())
                            .build();
                }
            } catch (Exception e) {
                titleImage = null; // fall back to the braille sprite
            }
        }
        return titleImage;
    }

    private void pushSpeed(long value) {
        System.arraycopy(speedHistory, 1, speedHistory, 0, speedHistory.length - 1);
        speedHistory[speedHistory.length - 1] = value;
    }

    private static Color darken(Color c, double factor) {
        var rgb = c.toRgb();
        return Color.rgb(
                (int) (rgb.r() * factor),
                (int) (rgb.g() * factor),
                (int) (rgb.b() * factor));
    }
}
