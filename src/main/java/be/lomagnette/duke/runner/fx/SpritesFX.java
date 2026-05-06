package be.lomagnette.duke.runner.fx;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

/**
 * Programmatic placeholder sprites for the JavaFX version.
 * All sprites are generated once and cached. Each method returns a JavaFX Image
 * that can be drawn with GraphicsContext.drawImage().
 *
 * Asset contract — to replace placeholders with real PNGs:
 *   Duke poses:   ~54x72 px (running, running2, jumping, throwing, sitting)
 *   Obstacles:    dimensions match game units * SCALE
 *   Decorations:  ~20x20 px icons
 */
public final class SpritesFX {

    private SpritesFX() {}

    public static final double SCALE = 3.0;

    // --- Cached sprites ---
    private static Image dukeRunningImg, dukeRunning2Img, dukeJumpingImg, dukeThrowingImg, dukeSittingImg;
    private static Image conferenceStageImg, laptopStackWideImg, laptopStackTallImg;
    private static Image coffeeSpillImg, confusedInternImg, slowBuildServerImg;
    private static Image coffeeCupImg, terminalImg, gitBranchImg, ideIconImg, dockerWhaleImg, cloudImg;

    // --- Duke sprites (body=white teardrop, nose=red circle) ---

    public static Image dukeRunning() {
        if (dukeRunningImg == null) dukeRunningImg = buildDuke(false, false, false);
        return dukeRunningImg;
    }

    public static Image dukeRunning2() {
        if (dukeRunning2Img == null) dukeRunning2Img = buildDuke(false, false, true);
        return dukeRunning2Img;
    }

    public static Image dukeJumping() {
        if (dukeJumpingImg == null) dukeJumpingImg = buildDuke(true, false, false);
        return dukeJumpingImg;
    }

    public static Image dukeThrowing() {
        if (dukeThrowingImg == null) dukeThrowingImg = buildDuke(false, true, false);
        return dukeThrowingImg;
    }

    public static Image dukeSitting() {
        if (dukeSittingImg == null) dukeSittingImg = buildDukeSitting();
        return dukeSittingImg;
    }

    // --- Obstacle sprites ---

    public static Image conferenceStage() {
        if (conferenceStageImg == null) {
            int w = (int)(6 * SCALE), h = (int)(26 * SCALE);
            conferenceStageImg = buildRect(w, h, Color.rgb(139, 90, 43), Color.rgb(100, 65, 30));
        }
        return conferenceStageImg;
    }

    public static Image laptopStackWide() {
        if (laptopStackWideImg == null) {
            int w = (int)(18 * SCALE), h = (int)(9 * SCALE);
            laptopStackWideImg = buildLaptops(w, h, 3, true);
        }
        return laptopStackWideImg;
    }

    public static Image laptopStackTall() {
        if (laptopStackTallImg == null) {
            int w = (int)(8 * SCALE), h = (int)(25 * SCALE);
            laptopStackTallImg = buildLaptops(w, h, 3, false);
        }
        return laptopStackTallImg;
    }

    public static Image coffeeSpill() {
        if (coffeeSpillImg == null) {
            int w = (int)(22 * SCALE), h = (int)(6 * SCALE);
            coffeeSpillImg = buildSpill(w, h);
        }
        return coffeeSpillImg;
    }

    public static Image confusedIntern() {
        if (confusedInternImg == null) {
            int w = (int)(14 * SCALE), h = (int)(14 * SCALE);
            confusedInternImg = buildPerson(w, h);
        }
        return confusedInternImg;
    }

    public static Image slowBuildServer() {
        if (slowBuildServerImg == null) {
            int w = (int)(7 * SCALE), h = (int)(10 * SCALE);
            slowBuildServerImg = buildServer(w, h);
        }
        return slowBuildServerImg;
    }

    // --- Decoration sprites (~20x20) ---

    public static Image coffeeCup() {
        if (coffeeCupImg == null) coffeeCupImg = buildDecoIcon(Color.rgb(139, 90, 43), 0);
        return coffeeCupImg;
    }

    public static Image terminal() {
        if (terminalImg == null) terminalImg = buildDecoIcon(Color.GREEN, 1);
        return terminalImg;
    }

    public static Image gitBranch() {
        if (gitBranchImg == null) gitBranchImg = buildDecoIcon(Color.LIMEGREEN, 2);
        return gitBranchImg;
    }

    public static Image ideIcon() {
        if (ideIconImg == null) ideIconImg = buildDecoIcon(Color.CYAN, 3);
        return ideIconImg;
    }

    public static Image dockerWhale() {
        if (dockerWhaleImg == null) dockerWhaleImg = buildDecoIcon(Color.DEEPSKYBLUE, 4);
        return dockerWhaleImg;
    }

    public static Image cloud() {
        if (cloudImg == null) cloudImg = buildDecoIcon(Color.WHITE, 5);
        return cloudImg;
    }

    // --- Builder helpers ---

    private static Image buildDuke(boolean jumping, boolean throwing, boolean altFrame) {
        int w = 54, h = 72;
        var img = new WritableImage(w, h);
        var pw = img.getPixelWriter();

        // Body: inverted teardrop (narrow top, wide bottom)
        // Top portion is black (crown), lower portion is white (body)
        int centerX = w / 2 - (throwing ? 3 : 0);
        double crownEnd = 0.35;

        for (int y = 0; y < h; y++) {
            double progress = (double) y / h;
            int radius;
            if (progress < 0.1) {
                radius = (int)(2 + progress * 80); // crown tip
            } else if (progress < 0.4) {
                radius = (int)(10 + (progress - 0.1) * 40); // widening head
            } else {
                radius = (int)(22 - (progress - 0.4) * 8); // body narrows slightly to bottom
                if (jumping && progress > 0.85) radius = (int)(radius * 0.7); // tucked
            }

            Color bodyColor = progress < crownEnd ? Color.BLACK : Color.WHITE;

            // Feet gap
            if (!jumping && progress > 0.9) {
                for (int x = centerX - radius; x <= centerX + radius; x++) {
                    if (x >= 0 && x < w) {
                        int footGap = (int)(w * 0.06);
                        if (Math.abs(x - centerX) < footGap) continue;
                        int offset = altFrame ? 2 : 0;
                        if (x < centerX) {
                            pw.setColor(x - offset, y, bodyColor);
                        } else {
                            pw.setColor(Math.min(x + offset, w - 1), y, bodyColor);
                        }
                    }
                }
            } else {
                for (int x = centerX - radius; x <= centerX + radius; x++) {
                    if (x >= 0 && x < w) pw.setColor(x, y, bodyColor);
                }
            }
        }

        // Red nose: oval on right side of face
        int noseX = centerX + 10;
        int noseY = (int)(h * 0.3);
        fillOval(pw, noseX, noseY, 8, 5, Color.RED, w, h);

        // Hands
        int handY = (int)(h * 0.55);
        int bodyRadiusAtHand = (int)(22 - (0.55 - 0.4) * 8);
        if (throwing) {
            // Left hand
            fillOval(pw, centerX - bodyRadiusAtHand - 5, handY, 4, 3, Color.WHITE, w, h);
            // Throwing arm
            for (int i = 0; i < 15; i++) {
                int ax = centerX + 18 + i;
                int ay = (int)(h * 0.5) + (i / 3);
                if (ax < w && ay < h) {
                    pw.setColor(ax, ay, Color.WHITE);
                    if (ay + 1 < h) pw.setColor(ax, ay + 1, Color.WHITE);
                    if (ay + 2 < h) pw.setColor(ax, ay + 2, Color.WHITE);
                }
            }
            // Hand at end of throwing arm
            int throwHandX = Math.min(centerX + 33, w - 5);
            int throwHandY = (int)(h * 0.5) + 4;
            fillOval(pw, throwHandX, throwHandY, 4, 3, Color.WHITE, w, h);
        } else if (jumping) {
            // Hands raised to the sides
            fillOval(pw, centerX - bodyRadiusAtHand - 5, handY - 6, 4, 3, Color.WHITE, w, h);
            fillOval(pw, centerX + bodyRadiusAtHand + 5, handY - 6, 4, 3, Color.WHITE, w, h);
        } else {
            // Running: hands swing with animation
            int swing = altFrame ? 4 : -4;
            fillOval(pw, centerX - bodyRadiusAtHand - 5, handY + swing, 4, 3, Color.WHITE, w, h);
            fillOval(pw, centerX + bodyRadiusAtHand + 5, handY - swing, 4, 3, Color.WHITE, w, h);
        }

        return img;
    }

    private static Image buildDukeSitting() {
        int w = 70, h = 80;
        var img = new WritableImage(w, h);
        var pw = img.getPixelWriter();

        int centerX = w / 2;
        double crownEnd = 0.35;

        for (int y = 0; y < h; y++) {
            double progress = (double) y / h;
            int radius;
            if (progress < 0.08) {
                radius = (int)(2 + progress * 100);
            } else if (progress < 0.35) {
                radius = (int)(10 + (progress - 0.08) * 60);
            } else {
                radius = (int)(26 - (progress - 0.35) * 10);
                if (progress > 0.9) {
                    // Feet
                    for (int x = centerX - radius; x <= centerX + radius; x++) {
                        if (x >= 0 && x < w && Math.abs(x - centerX) > 4) {
                            pw.setColor(x, y, Color.WHITE);
                        }
                    }
                    continue;
                }
            }

            Color bodyColor = progress < crownEnd ? Color.BLACK : Color.WHITE;
            for (int x = centerX - radius; x <= centerX + radius; x++) {
                if (x >= 0 && x < w) pw.setColor(x, y, bodyColor);
            }
        }

        // Centered nose
        fillOval(pw, centerX, (int)(h * 0.35), 10, 7, Color.RED, w, h);

        // Hands resting at sides
        int handY = (int)(h * 0.55);
        int bodyRadiusAtHand = (int)(26 - (0.55 - 0.35) * 10);
        fillOval(pw, centerX - bodyRadiusAtHand - 5, handY, 5, 4, Color.WHITE, w, h);
        fillOval(pw, centerX + bodyRadiusAtHand + 5, handY, 5, 4, Color.WHITE, w, h);

        return img;
    }

    private static Image buildRect(int w, int h, Color fill, Color border) {
        var img = new WritableImage(w, h);
        var pw = img.getPixelWriter();
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                boolean isBorder = x < 2 || x >= w - 2 || y < 2 || y >= h - 2;
                pw.setColor(x, y, isBorder ? border : fill);
            }
        }
        return img;
    }

    private static Image buildLaptops(int w, int h, int count, boolean horizontal) {
        var img = new WritableImage(w, h);
        var pw = img.getPixelWriter();
        Color screen = Color.rgb(60, 80, 120);
        Color body = Color.rgb(180, 180, 190);
        Color frame = Color.rgb(120, 120, 130);

        if (horizontal) {
            int laptopW = w / count - 2;
            for (int i = 0; i < count; i++) {
                int ox = i * (laptopW + 2);
                // Screen top half
                for (int y = 0; y < h / 2; y++) {
                    for (int x = ox + 2; x < ox + laptopW - 2 && x < w; x++) {
                        pw.setColor(x, y, screen);
                    }
                }
                // Base bottom half
                for (int y = h / 2; y < h; y++) {
                    for (int x = ox; x < ox + laptopW && x < w; x++) {
                        pw.setColor(x, y, y == h / 2 ? frame : body);
                    }
                }
            }
        } else {
            int laptopH = h / count - 2;
            for (int i = 0; i < count; i++) {
                int oy = i * (laptopH + 2);
                // Screen
                for (int y = oy; y < oy + laptopH * 2 / 3 && y < h; y++) {
                    for (int x = 3; x < w - 3; x++) {
                        pw.setColor(x, y, screen);
                    }
                }
                // Base
                for (int y = oy + laptopH * 2 / 3; y < oy + laptopH && y < h; y++) {
                    for (int x = 0; x < w; x++) {
                        pw.setColor(x, y, body);
                    }
                }
            }
        }
        return img;
    }

    private static Image buildSpill(int w, int h) {
        var img = new WritableImage(w, h);
        var pw = img.getPixelWriter();
        Color coffee = Color.rgb(101, 67, 33);
        Color light = Color.rgb(139, 90, 43);

        int centerX = w / 2;
        for (int y = 0; y < h; y++) {
            double fy = (double) y / h;
            int radius = (int)(centerX * (1.0 - 0.3 * Math.abs(fy - 0.5)));
            for (int x = centerX - radius; x <= centerX + radius; x++) {
                if (x >= 0 && x < w) {
                    pw.setColor(x, y, (x + y) % 3 == 0 ? light : coffee);
                }
            }
        }
        return img;
    }

    private static Image buildPerson(int w, int h) {
        var img = new WritableImage(w, h);
        var pw = img.getPixelWriter();
        Color skin = Color.rgb(220, 190, 160);
        Color shirt = Color.rgb(70, 130, 200);

        int cx = w / 2;
        // Head
        fillOval(pw, cx, h / 6, w / 5, h / 7, skin, w, h);
        // Body
        for (int y = h / 3; y < h * 3 / 4; y++) {
            int bw = (int)(w * 0.4 * (y < h / 2 ? 1.0 : 0.8));
            for (int x = cx - bw; x <= cx + bw; x++) {
                if (x >= 0 && x < w) pw.setColor(x, y, shirt);
            }
        }
        // Arms extended
        for (int x = 0; x < w; x++) {
            for (int y = h * 2 / 5; y < h * 2 / 5 + 4 && y < h; y++) {
                if (x >= 0 && x < w) pw.setColor(x, y, shirt);
            }
        }
        // Legs
        for (int y = h * 3 / 4; y < h; y++) {
            for (int dx = -1; dx <= 1; dx += 2) {
                int lx = cx + dx * w / 6;
                for (int x = lx - 2; x <= lx + 2 && x < w; x++) {
                    if (x >= 0) pw.setColor(x, y, Color.rgb(60, 60, 80));
                }
            }
        }
        // Question mark above head
        int qx = cx + 2, qy = 3;
        if (qx < w && qy < h) pw.setColor(qx, qy, Color.YELLOW);
        if (qx + 1 < w) pw.setColor(qx + 1, qy, Color.YELLOW);
        if (qx + 1 < w && qy + 1 < h) pw.setColor(qx + 1, qy + 1, Color.YELLOW);
        if (qx < w && qy + 2 < h) pw.setColor(qx, qy + 2, Color.YELLOW);
        if (qx < w && qy + 4 < h) pw.setColor(qx, qy + 4, Color.YELLOW);

        return img;
    }

    private static Image buildServer(int w, int h) {
        var img = new WritableImage(w, h);
        var pw = img.getPixelWriter();
        Color frame = Color.rgb(80, 80, 90);
        Color panel = Color.rgb(50, 50, 60);
        Color led = Color.LIME;

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                boolean isBorder = x == 0 || x == w - 1 || y == 0 || y == h - 1;
                boolean isSlot = (y % 6 == 3) && x > 1 && x < w - 1;
                pw.setColor(x, y, isBorder ? frame : (isSlot ? frame : panel));
            }
        }
        // LED dots
        for (int y = 4; y < h - 2; y += 6) {
            if (y < h && w - 3 >= 0) pw.setColor(w - 3, y, led);
        }
        return img;
    }

    private static Image buildDecoIcon(Color color, int shape) {
        int size = 20;
        var img = new WritableImage(size, size);
        var pw = img.getPixelWriter();

        switch (shape) {
            case 0 -> { // coffee cup
                for (int y = 4; y < 16; y++) {
                    for (int x = 4; x < 12; x++) pw.setColor(x, y, color);
                }
                pw.setColor(13, 7, color); pw.setColor(13, 8, color);
                pw.setColor(13, 10, color); pw.setColor(13, 11, color);
                pw.setColor(14, 8, color); pw.setColor(14, 9, color); pw.setColor(14, 10, color);
                // Steam
                pw.setColor(6, 2, color.deriveColor(0, 1, 1, 0.5));
                pw.setColor(8, 1, color.deriveColor(0, 1, 1, 0.5));
                pw.setColor(7, 3, color.deriveColor(0, 1, 1, 0.5));
            }
            case 1 -> { // terminal >_
                for (int y = 3; y < 17; y++) {
                    for (int x = 2; x < 18; x++) {
                        boolean isBorder = x == 2 || x == 17 || y == 3 || y == 16;
                        if (isBorder) pw.setColor(x, y, color);
                    }
                }
                // > prompt
                pw.setColor(5, 8, color); pw.setColor(6, 9, color); pw.setColor(7, 10, color);
                pw.setColor(6, 11, color); pw.setColor(5, 12, color);
                // _ cursor
                pw.setColor(10, 12, color); pw.setColor(11, 12, color); pw.setColor(12, 12, color);
            }
            case 2 -> { // git branch
                for (int y = 2; y < 18; y++) pw.setColor(6, y, color);
                for (int y = 2; y < 10; y++) {
                    int x = 6 + (y - 2);
                    if (x < size) pw.setColor(x, y, color);
                }
                fillOval(pw, 6, 17, 2, 2, color, size, size);
                fillOval(pw, 13, 3, 2, 2, color, size, size);
            }
            case 3 -> { // IDE icon
                for (int y = 2; y < 18; y++) {
                    for (int x = 2; x < 18; x++) {
                        if (y < 5) pw.setColor(x, y, color);
                        else if (x == 2 || x == 17 || y == 17) pw.setColor(x, y, color);
                    }
                }
                // Code lines
                for (int x = 5; x < 12; x++) pw.setColor(x, 8, color.deriveColor(0, 1, 1, 0.5));
                for (int x = 5; x < 15; x++) pw.setColor(x, 11, color.deriveColor(0, 1, 1, 0.5));
                for (int x = 5; x < 10; x++) pw.setColor(x, 14, color.deriveColor(0, 1, 1, 0.5));
            }
            case 4 -> { // docker whale
                // Body
                for (int y = 10; y < 17; y++) {
                    for (int x = 2; x < 18; x++) pw.setColor(x, y, color);
                }
                // Containers on top
                for (int col = 0; col < 3; col++) {
                    int bx = 4 + col * 4;
                    for (int y = 6; y < 10; y++) {
                        for (int x = bx; x < bx + 3 && x < size; x++) {
                            pw.setColor(x, y, color);
                        }
                    }
                }
                // Tail
                pw.setColor(1, 11, color); pw.setColor(1, 12, color);
            }
            case 5 -> { // cloud
                fillOval(pw, 10, 10, 8, 5, color, size, size);
                fillOval(pw, 7, 7, 4, 3, color, size, size);
                fillOval(pw, 13, 7, 3, 3, color, size, size);
            }
        }
        return img;
    }

    private static void fillOval(javafx.scene.image.PixelWriter pw,
                                  int cx, int cy, int rx, int ry,
                                  Color color, int maxW, int maxH) {
        for (int y = cy - ry; y <= cy + ry; y++) {
            for (int x = cx - rx; x <= cx + rx; x++) {
                double dx = (double)(x - cx) / rx;
                double dy = (double)(y - cy) / ry;
                if (dx * dx + dy * dy <= 1.0 && x >= 0 && x < maxW && y >= 0 && y < maxH) {
                    pw.setColor(x, y, color);
                }
            }
        }
    }
}
