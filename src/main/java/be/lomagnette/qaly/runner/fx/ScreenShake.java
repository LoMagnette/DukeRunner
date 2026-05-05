package be.lomagnette.qaly.runner.fx;

import java.util.Random;

public class ScreenShake {

    private static final Random RNG = new Random();
    private static final float DECAY = 0.85f;

    private float intensity;
    private double offsetX;
    private double offsetY;

    public void trigger(float intensity) {
        this.intensity = intensity;
    }

    public void tick() {
        if (intensity > 0.1f) {
            offsetX = (RNG.nextDouble() * 2 - 1) * intensity;
            offsetY = (RNG.nextDouble() * 2 - 1) * intensity;
            intensity *= DECAY;
        } else {
            intensity = 0;
            offsetX = 0;
            offsetY = 0;
        }
    }

    public double offsetX() { return offsetX; }
    public double offsetY() { return offsetY; }
    public boolean isActive() { return intensity > 0.1f; }

    public void reset() {
        intensity = 0;
        offsetX = 0;
        offsetY = 0;
    }
}
