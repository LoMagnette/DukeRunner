package be.lomagnette.duke.runner.tui;

import dev.tamboui.style.Color;

public enum Season {
    // label, accent, ground, sky-top (zenith), sky-horizon (near ground)
    JAVA_1("Java 1", Color.GREEN, Color.rgb(34, 139, 34), Color.rgb(20, 40, 30), Color.rgb(120, 170, 110)),
    JAVA_5("Java 5", Color.YELLOW, Color.rgb(200, 170, 50), Color.rgb(60, 45, 20), Color.rgb(230, 180, 90)),
    JAVA_11("Java 11", Color.CYAN, Color.rgb(100, 130, 170), Color.rgb(20, 30, 55), Color.rgb(120, 150, 200)),
    JAVA_21("Java 21+", Color.MAGENTA, Color.rgb(140, 100, 160), Color.rgb(25, 15, 45), Color.rgb(150, 90, 170));

    /** Points of score spanned by a single era before it cycles. */
    public static final int ERA_LENGTH = 100;

    private final String label;
    private final Color accentColor;
    private final Color groundColor;
    private final Color skyColor;
    private final Color horizonColor;

    Season(String label, Color accentColor, Color groundColor, Color skyColor, Color horizonColor) {
        this.label = label;
        this.accentColor = accentColor;
        this.groundColor = groundColor;
        this.skyColor = skyColor;
        this.horizonColor = horizonColor;
    }

    public String label() { return label; }
    public Color accentColor() { return accentColor; }
    public Color groundColor() { return groundColor; }
    public Color skyColor() { return skyColor; }
    public Color horizonColor() { return horizonColor; }

    public static Season forScore(int score) {
        return values()[(score / ERA_LENGTH) % 4];
    }

    public Season next() {
        return values()[(ordinal() + 1) % values().length];
    }

    // ── Smooth transitions ─────────────────────────────────────────
    // Renderer uses these so the "repaint through Java history" fades
    // from one era's palette to the next instead of snapping. Progress
    // is how far we are into the current era's score band (0.0 → 1.0).

    private static float progress(int score) {
        return (score % ERA_LENGTH) / (float) ERA_LENGTH;
    }

    public static Color groundColorFor(int score) {
        Season cur = forScore(score);
        return lerp(cur.groundColor, cur.next().groundColor, progress(score));
    }

    public static Color accentColorFor(int score) {
        Season cur = forScore(score);
        return lerp(cur.accentColor, cur.next().accentColor, progress(score));
    }

    public static Color skyColorFor(int score) {
        Season cur = forScore(score);
        return lerp(cur.skyColor, cur.next().skyColor, progress(score));
    }

    public static Color horizonColorFor(int score) {
        Season cur = forScore(score);
        return lerp(cur.horizonColor, cur.next().horizonColor, progress(score));
    }

    /** Linear RGB interpolation. {@code t} is clamped to [0, 1]. */
    public static Color lerp(Color a, Color b, float t) {
        t = Math.max(0f, Math.min(1f, t));
        var ca = a.toRgb();
        var cb = b.toRgb();
        int r = Math.round(ca.r() + (cb.r() - ca.r()) * t);
        int g = Math.round(ca.g() + (cb.g() - ca.g()) * t);
        int bl = Math.round(ca.b() + (cb.b() - ca.b()) * t);
        return Color.rgb(r, g, bl);
    }
}
