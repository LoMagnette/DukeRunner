package be.lomagnette.qaly.runner.fx;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;

public class ParallaxBackground {

    private double distantOffset = 0;
    private double midOffset = 0;

    public void scroll(float speed) {
        distantOffset += speed * 0.2;
        midOffset += speed * 0.5;
    }

    public void reset() {
        distantOffset = 0;
        midOffset = 0;
    }

    public void render(GraphicsContext gc, SeasonFX era, double w, double h) {
        double groundY = RendererFX.gameToScreenY(PhysicsFX.GROUND_Y, h);

        // Layer 1: Sky gradient (static, era-colored)
        Color skyTop = skyTopColor(era);
        Color skyBottom = skyBottomColor(era);
        gc.setFill(new LinearGradient(0, 0, 0, groundY, false,
                CycleMethod.NO_CYCLE,
                new Stop(0, skyTop),
                new Stop(1, skyBottom)));
        gc.fillRect(0, 0, w, groundY);

        // Layer 2: Distant silhouettes (0.2x speed) — mountains/clouds
        Color distantColor = distantLayerColor(era);
        renderDistantLayer(gc, w, groundY, distantColor);

        // Layer 3: Mid-ground silhouettes (0.5x speed) — hills/buildings
        Color midColor = midLayerColor(era);
        renderMidLayer(gc, w, groundY, midColor);
    }

    private void renderDistantLayer(GraphicsContext gc, double w, double groundY, Color color) {
        gc.setFill(color);
        double period = 300;
        double baseY = groundY - 20;
        double amplitude = 40;

        for (int x = 0; x < (int) w; x++) {
            double wx = x + distantOffset;
            double height = amplitude * (0.5 + 0.3 * Math.sin(wx / period * Math.PI * 2)
                    + 0.2 * Math.sin(wx / (period * 0.7) * Math.PI * 2 + 1.5));
            gc.fillRect(x, baseY - height, 1, height + 20);
        }
    }

    private void renderMidLayer(GraphicsContext gc, double w, double groundY, Color color) {
        gc.setFill(color);
        double period = 180;
        double baseY = groundY;
        double amplitude = 30;

        for (int x = 0; x < (int) w; x++) {
            double wx = x + midOffset;
            double height = amplitude * (0.4 + 0.35 * Math.sin(wx / period * Math.PI * 2)
                    + 0.15 * Math.sin(wx / (period * 0.5) * Math.PI * 2 + 0.8)
                    + 0.1 * Math.sin(wx / (period * 0.3) * Math.PI * 2 + 2.1));
            gc.fillRect(x, baseY - height, 1, height);
        }
    }

    private static Color skyTopColor(SeasonFX era) {
        return switch (era) {
            case JAVA_1 -> Color.rgb(5, 15, 5);
            case JAVA_5 -> Color.rgb(15, 12, 5);
            case JAVA_11 -> Color.rgb(5, 10, 20);
            case JAVA_21 -> Color.rgb(12, 5, 15);
        };
    }

    private static Color skyBottomColor(SeasonFX era) {
        return switch (era) {
            case JAVA_1 -> Color.rgb(20, 40, 20);
            case JAVA_5 -> Color.rgb(40, 30, 15);
            case JAVA_11 -> Color.rgb(20, 30, 50);
            case JAVA_21 -> Color.rgb(30, 20, 40);
        };
    }

    private static Color distantLayerColor(SeasonFX era) {
        return switch (era) {
            case JAVA_1 -> Color.rgb(15, 30, 15, 0.6);
            case JAVA_5 -> Color.rgb(35, 25, 10, 0.6);
            case JAVA_11 -> Color.rgb(15, 25, 45, 0.6);
            case JAVA_21 -> Color.rgb(25, 15, 35, 0.6);
        };
    }

    private static Color midLayerColor(SeasonFX era) {
        return switch (era) {
            case JAVA_1 -> Color.rgb(20, 45, 20, 0.7);
            case JAVA_5 -> Color.rgb(50, 35, 15, 0.7);
            case JAVA_11 -> Color.rgb(20, 35, 60, 0.7);
            case JAVA_21 -> Color.rgb(35, 20, 50, 0.7);
        };
    }
}
