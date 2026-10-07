package be.lomagnette.duke.runner.tui;

import java.util.Random;

/**
 * Decaying screen-shake offset, applied by nudging the game canvas bounds so the
 * whole scene jitters within its (fixed) frame. Offsets are in canvas units.
 * Ported verbatim from the JavaFX {@code ScreenShake}; framework-agnostic.
 */
final class ScreenShake {

    private static final Random RNG = new Random();
    private static final float DECAY = 0.85f;

    private float intensity;
    private double offsetX;
    private double offsetY;

    void trigger(float intensity) {
        this.intensity = intensity;
    }

    void tick() {
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

    double offsetX() { return offsetX; }
    double offsetY() { return offsetY; }

    void reset() {
        intensity = 0;
        offsetX = 0;
        offsetY = 0;
    }
}
