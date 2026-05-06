package be.lomagnette.duke.runner.fx;

import javax.sound.sampled.*;
import java.util.Random;

public class AudioFX {

    private static final int SAMPLE_RATE = 22050;
    private static final Random RNG = new Random();

    private Clip jumpClip;
    private Clip throwClip;
    private Clip collisionClip;
    private Clip milestoneClip;
    private boolean enabled;

    public void init() {
        try {
            jumpClip = makeClip(synthesizeJump());
            throwClip = makeClip(synthesizeThrow());
            collisionClip = makeClip(synthesizeCollision());
            milestoneClip = makeClip(synthesizeMilestone());
            enabled = true;
        } catch (Exception e) {
            enabled = false;
        }
    }

    public void playJump() { play(jumpClip); }
    public void playThrow() { play(throwClip); }
    public void playCollision() { play(collisionClip); }
    public void playMilestone() { play(milestoneClip); }

    private void play(Clip clip) {
        if (!enabled || clip == null) return;
        try {
            clip.setFramePosition(0);
            clip.start();
        } catch (Exception ignored) {}
    }

    // Short ascending sine tone: 400→800Hz, 100ms
    private byte[] synthesizeJump() {
        float duration = 0.1f;
        int samples = (int)(SAMPLE_RATE * duration);
        byte[] buf = new byte[samples];
        for (int i = 0; i < samples; i++) {
            double t = (double) i / SAMPLE_RATE;
            double progress = (double) i / samples;
            double freq = 400 + 400 * progress;
            double amplitude = 0.5 * (1.0 - progress); // fade out
            buf[i] = (byte)(amplitude * 127 * Math.sin(2 * Math.PI * freq * t));
        }
        return buf;
    }

    // Noise burst with rising filter: 150ms
    private byte[] synthesizeThrow() {
        float duration = 0.15f;
        int samples = (int)(SAMPLE_RATE * duration);
        byte[] buf = new byte[samples];
        double filterState = 0;
        for (int i = 0; i < samples; i++) {
            double progress = (double) i / samples;
            double noise = RNG.nextDouble() * 2 - 1;
            // Simple low-pass with increasing cutoff
            double alpha = 0.05 + 0.9 * progress;
            filterState = filterState * (1 - alpha) + noise * alpha;
            double amplitude = 0.4 * (1.0 - progress * 0.7);
            buf[i] = (byte)(amplitude * 127 * filterState);
        }
        return buf;
    }

    // Low-frequency burst: 100Hz, 200ms with fast decay
    private byte[] synthesizeCollision() {
        float duration = 0.2f;
        int samples = (int)(SAMPLE_RATE * duration);
        byte[] buf = new byte[samples];
        for (int i = 0; i < samples; i++) {
            double t = (double) i / SAMPLE_RATE;
            double progress = (double) i / samples;
            double freq = 100 + 20 * Math.sin(progress * 10);
            double amplitude = 0.7 * Math.exp(-progress * 4); // fast exponential decay
            // Mix in some noise for impact feel
            double noise = (RNG.nextDouble() * 2 - 1) * 0.3 * Math.exp(-progress * 6);
            buf[i] = (byte)(127 * (amplitude * Math.sin(2 * Math.PI * freq * t) + noise));
        }
        return buf;
    }

    // Two-tone chime: C5 (523Hz) then E5 (659Hz), 100ms each
    private byte[] synthesizeMilestone() {
        float duration = 0.2f;
        int samples = (int)(SAMPLE_RATE * duration);
        byte[] buf = new byte[samples];
        int half = samples / 2;
        for (int i = 0; i < samples; i++) {
            double t = (double) i / SAMPLE_RATE;
            double freq = i < half ? 523.25 : 659.25;
            double localProgress = i < half ? (double) i / half : (double)(i - half) / half;
            double amplitude = 0.5 * (1.0 - localProgress * 0.5);
            buf[i] = (byte)(amplitude * 127 * Math.sin(2 * Math.PI * freq * t));
        }
        return buf;
    }

    private static Clip makeClip(byte[] data) throws Exception {
        AudioFormat format = new AudioFormat(SAMPLE_RATE, 8, 1, true, false);
        Clip clip = AudioSystem.getClip();
        clip.open(format, data, 0, data.length);
        return clip;
    }
}
