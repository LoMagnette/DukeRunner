package be.lomagnette.duke.runner.tui;

import dev.tamboui.style.Color;
import dev.tamboui.widgets.canvas.Context;
import dev.tamboui.widgets.canvas.shapes.Line;

/**
 * Scrolling parallax scenery drawn between the star field and the terrain.
 *
 * <p>Two silhouette ridgelines scroll at different speeds (0.2x and 0.5x of the
 * world speed) to fake depth, with a soft sun/moon disc low over the horizon.
 * All colours are derived from the current {@link Season} palette so the scenery
 * fades through the Java eras along with everything else. Ported from the
 * JavaFX {@code ParallaxBackground}, adapted to the braille/half-block canvas.
 *
 * <p>Coordinates are canvas units with y increasing upward; {@code groundY} is
 * the terrain line, so ridges rise from {@code groundY} into the sky above it.
 */
final class Background {

    private double distantOffset;
    private double midOffset;

    void scroll(float speed) {
        distantOffset += speed * 0.2;
        midOffset += speed * 0.5;
    }

    void reset() {
        distantOffset = 0;
        midOffset = 0;
    }

    void render(Context ctx, int cw, int ch, double groundY, int score) {
        double skyHeight = Math.max(1, ch - groundY);

        // Sun / moon: a soft disc, era-tinted, sitting well up in the sky.
        Color disc = Season.lerp(Season.accentColorFor(score),
                Season.horizonColorFor(score), 0.45f);
        double sunX = cw * 0.72;
        double sunY = groundY + skyHeight * 0.62;
        double sunR = Math.max(3.0, skyHeight * 0.16);
        fillDisc(ctx, cw, ch, groundY, sunX, sunY, sunR, disc);

        // Distant ridge (slow, darkest) then mid ridge (faster, lighter) in
        // front of it — painter's order gives the layering.
        Color ground = Season.groundColorFor(score);
        Color distant = darken(ground, 0.40);
        Color mid = darken(ground, 0.62);
        ridge(ctx, cw, groundY, distantOffset, cw * 0.9, skyHeight * 0.26, 1.5, distant);
        ridge(ctx, cw, groundY, midOffset, cw * 0.55, skyHeight * 0.17, 0.8, mid);
    }

    /** One silhouette ridgeline: a vertical fill per column, height from summed sines. */
    private static void ridge(Context ctx, int cw, double groundY, double offset,
                              double period, double amplitude, double phase, Color color) {
        for (int x = 0; x < cw; x++) {
            double wx = x + offset;
            double n = 0.55
                    + 0.30 * Math.sin(wx / period * Math.PI * 2)
                    + 0.15 * Math.sin(wx / (period * 0.45) * Math.PI * 2 + phase);
            double top = groundY + Math.max(0, amplitude * n);
            ctx.draw(new Line(x, groundY, x, top, color));
        }
    }

    /** Filled circle via one vertical segment per column (cheap, no per-pixel loop). */
    private static void fillDisc(Context ctx, int cw, int ch, double groundY,
                                 double cx, double cy, double r, Color color) {
        int x0 = (int) Math.floor(cx - r);
        int x1 = (int) Math.ceil(cx + r);
        for (int x = Math.max(0, x0); x <= Math.min(cw - 1, x1); x++) {
            double dx = x - cx;
            if (Math.abs(dx) > r) continue;
            double half = Math.sqrt(r * r - dx * dx);
            double yBot = Math.max(groundY + 1, cy - half);
            double yTop = Math.min(ch - 1, cy + half);
            if (yTop > yBot) ctx.draw(new Line(x, yBot, x, yTop, color));
        }
    }

    private static Color darken(Color c, double factor) {
        var rgb = c.toRgb();
        return Color.rgb(
                (int) (rgb.r() * factor),
                (int) (rgb.g() * factor),
                (int) (rgb.b() * factor));
    }
}
