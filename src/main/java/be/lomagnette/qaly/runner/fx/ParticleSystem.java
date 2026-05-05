package be.lomagnette.qaly.runner.fx;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.Random;

public class ParticleSystem {

    private static final int MAX_PARTICLES = 100;
    private static final Random RNG = new Random();

    private final double[] x = new double[MAX_PARTICLES];
    private final double[] y = new double[MAX_PARTICLES];
    private final double[] vx = new double[MAX_PARTICLES];
    private final double[] vy = new double[MAX_PARTICLES];
    private final int[] life = new int[MAX_PARTICLES];
    private final int[] maxLife = new int[MAX_PARTICLES];
    private final double[] size = new double[MAX_PARTICLES];
    private final double[] r = new double[MAX_PARTICLES];
    private final double[] g = new double[MAX_PARTICLES];
    private final double[] b = new double[MAX_PARTICLES];
    private int nextSlot = 0;

    public void emitDust(double px, double py) {
        for (int i = 0; i < 2; i++) {
            int s = nextSlot();
            x[s] = px + RNG.nextDouble() * 6 - 3;
            y[s] = py + RNG.nextDouble() * 3;
            vx[s] = -0.5 - RNG.nextDouble() * 1.5;
            vy[s] = -0.3 - RNG.nextDouble() * 0.5;
            life[s] = 15 + RNG.nextInt(10);
            maxLife[s] = life[s];
            size[s] = 1.5 + RNG.nextDouble() * 2;
            r[s] = 0.55; g[s] = 0.35; b[s] = 0.17;
        }
    }

    public void emitSparkle(double px, double py, Color color) {
        for (int i = 0; i < 20; i++) {
            int s = nextSlot();
            x[s] = px;
            y[s] = py;
            double angle = RNG.nextDouble() * Math.PI * 2;
            double speed = 1.0 + RNG.nextDouble() * 3.0;
            vx[s] = Math.cos(angle) * speed;
            vy[s] = Math.sin(angle) * speed;
            life[s] = 25 + RNG.nextInt(15);
            maxLife[s] = life[s];
            size[s] = 2.0 + RNG.nextDouble() * 3;
            r[s] = color.getRed(); g[s] = color.getGreen(); b[s] = color.getBlue();
        }
    }

    public void emitExplosion(double px, double py) {
        for (int i = 0; i < 30; i++) {
            int s = nextSlot();
            x[s] = px;
            y[s] = py;
            double angle = RNG.nextDouble() * Math.PI * 2;
            double speed = 2.0 + RNG.nextDouble() * 5.0;
            vx[s] = Math.cos(angle) * speed;
            vy[s] = Math.sin(angle) * speed;
            life[s] = 30 + RNG.nextInt(20);
            maxLife[s] = life[s];
            size[s] = 3.0 + RNG.nextDouble() * 4;
            // Red/orange/yellow mix
            r[s] = 0.8 + RNG.nextDouble() * 0.2;
            g[s] = RNG.nextDouble() * 0.6;
            b[s] = 0;
        }
    }

    public void tick() {
        for (int i = 0; i < MAX_PARTICLES; i++) {
            if (life[i] > 0) {
                x[i] += vx[i];
                y[i] += vy[i];
                vy[i] += 0.05; // slight gravity on particles
                life[i]--;
            }
        }
    }

    public void render(GraphicsContext gc, double screenH) {
        double scale = SpritesFX.SCALE;
        for (int i = 0; i < MAX_PARTICLES; i++) {
            if (life[i] > 0) {
                double alpha = (double) life[i] / maxLife[i];
                gc.setFill(Color.color(r[i], g[i], b[i], alpha));
                double sx = x[i] * scale;
                double sy = RendererFX.gameToScreenY(y[i], screenH);
                gc.fillOval(sx, sy, size[i], size[i]);
            }
        }
    }

    public void reset() {
        for (int i = 0; i < MAX_PARTICLES; i++) {
            life[i] = 0;
        }
        nextSlot = 0;
    }

    private int nextSlot() {
        // Find a dead particle or use round-robin
        for (int i = 0; i < MAX_PARTICLES; i++) {
            int idx = (nextSlot + i) % MAX_PARTICLES;
            if (life[idx] <= 0) {
                nextSlot = (idx + 1) % MAX_PARTICLES;
                return idx;
            }
        }
        // All alive — overwrite oldest
        int idx = nextSlot;
        nextSlot = (nextSlot + 1) % MAX_PARTICLES;
        return idx;
    }
}
