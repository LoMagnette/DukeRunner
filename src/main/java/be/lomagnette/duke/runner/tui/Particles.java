package be.lomagnette.duke.runner.tui;

import dev.tamboui.style.Color;
import dev.tamboui.widgets.canvas.Context;
import dev.tamboui.widgets.canvas.shapes.Points;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Transient, event-driven particles (landing dust, throw sparks) layered on top
 * of the ambient era drift. Positions are canvas units with y increasing upward;
 * gravity therefore pulls velocity down. Compact port of the JavaFX
 * {@code ParticleSystem} — one small mutable pool, updated per frame by real
 * elapsed milliseconds so bursts decay at the same rate regardless of frame time.
 */
final class Particles {

    private static final class P {
        double x, y, vx, vy, life, maxLife;
        Color color;
    }

    private static final double GRAVITY = 0.35; // canvas units / frame^2 at 60fps

    private final List<P> live = new ArrayList<>();
    private final Random rng = new Random();

    /** A puff kicked backward and up from the player's feet on landing. */
    void emitDust(double x, double y, Color color) {
        for (int i = 0; i < 10; i++) {
            P p = new P();
            p.x = x + rng.nextDouble() * 4 - 2;
            p.y = y;
            p.vx = -rng.nextDouble() * 1.5 - 0.3;      // trails backward
            p.vy = rng.nextDouble() * 1.2 + 0.2;       // small upward hop
            p.maxLife = p.life = 14 + rng.nextInt(8);
            p.color = color;
            live.add(p);
        }
    }

    /** A bright fan of sparks thrown ahead of the player. */
    void emitSparks(double x, double y, Color color) {
        for (int i = 0; i < 14; i++) {
            P p = new P();
            p.x = x;
            p.y = y + rng.nextDouble() * 4 - 2;
            p.vx = rng.nextDouble() * 3.0 + 0.5;       // shoots forward
            p.vy = rng.nextDouble() * 2.0 - 1.0;
            p.maxLife = p.life = 10 + rng.nextInt(6);
            p.color = color;
            live.add(p);
        }
    }

    void update(double dtMs) {
        double step = Math.max(0.25, Math.min(4.0, dtMs / 16.0));
        for (var it = live.iterator(); it.hasNext(); ) {
            P p = it.next();
            p.vy -= GRAVITY * step;
            p.x += p.vx * step;
            p.y += p.vy * step;
            p.life -= step;
            if (p.life <= 0) it.remove();
        }
    }

    void render(Context ctx) {
        if (live.isEmpty()) return;
        // Group by faded colour so each particle dims as it dies.
        for (P p : live) {
            double t = Math.max(0, p.life / p.maxLife);
            var rgb = p.color.toRgb();
            Color faded = Color.rgb(
                    (int) (rgb.r() * t),
                    (int) (rgb.g() * t),
                    (int) (rgb.b() * t));
            ctx.draw(Points.of(new double[][] {{p.x, p.y}}, faded));
        }
    }

    void clear() {
        live.clear();
    }
}
