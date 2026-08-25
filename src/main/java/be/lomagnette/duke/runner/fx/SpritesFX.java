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

    // --- Duke sprites ---
    // Duke = the Java mascot (see src/main/resources/duke.png): a big solid
    // BLACK pointed cone for the head/body, a glossy RED nose low-centre with a
    // white glint, a WHITE belly below it, thin black arms akimbo, and a wavy
    // two-foot skirt. Poses are generated parametrically by dukeGrid() (a char
    // grid, black='B' white='W' red='R' glint='H') and rasterised by imageFromGrid().

    private static final Color DUKE_BLACK = Color.rgb(20, 20, 24);
    private static final Color DUKE_BODY = Color.rgb(246, 246, 250);
    private static final Color DUKE_NOSE = Color.rgb(183, 26, 62);
    private static final Color DUKE_GLINT = Color.WHITE;
    private enum Pose { SIT, RUN_A, RUN_B, JUMP, THROW }

    public static Image dukeRunning() {
        if (dukeRunningImg == null) dukeRunningImg = buildDuke(Pose.RUN_A, 54, 66);
        return dukeRunningImg;
    }

    public static Image dukeRunning2() {
        if (dukeRunning2Img == null) dukeRunning2Img = buildDuke(Pose.RUN_B, 54, 66);
        return dukeRunning2Img;
    }

    public static Image dukeJumping() {
        if (dukeJumpingImg == null) dukeJumpingImg = buildDuke(Pose.JUMP, 54, 66);
        return dukeJumpingImg;
    }

    public static Image dukeThrowing() {
        if (dukeThrowingImg == null) dukeThrowingImg = buildDuke(Pose.THROW, 54, 66);
        return dukeThrowingImg;
    }

    public static Image dukeSitting() {
        if (dukeSittingImg == null) dukeSittingImg = buildDuke(Pose.SIT, 72, 84);
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

    /** Rasterise a Duke pose into a w×h transparent-backed image. */
    private static Image buildDuke(Pose pose, int w, int h) {
        // Title mascot faces front; the in-game run/jump/throw poses are profile.
        char[][] g = pose == Pose.SIT ? dukeGridFront(pose, w, h) : dukeGridProfile(pose, w, h);
        var img = new WritableImage(w, h);
        var pw = img.getPixelWriter();
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                Color c = switch (g[y][x]) {
                    case 'B' -> DUKE_BLACK;
                    case 'W' -> DUKE_BODY;
                    case 'R' -> DUKE_NOSE;
                    case 'H' -> DUKE_GLINT;
                    default -> null;
                };
                if (c != null) pw.setColor(x, y, c);
            }
        }
        return img;
    }

    /**
     * Front-facing Duke on a w×h char grid from the mascot's geometry: a solid
     * black cone tapering to the crown, a white belly inset below the nose, a
     * glossy red nose with a glint, thin black arms akimbo, and a wavy skirt that
     * splits into two feet. Used for the title; in-game poses use the profile.
     * Row 0 is the crown (top).
     */
    private static char[][] dukeGridFront(Pose pose, int w, int h) {
        double cx = (w - 1) / 2.0;
        char[][] g = new char[h][w];
        for (char[] row : g) java.util.Arrays.fill(row, ' ');

        int coneBase = (int)(h * 0.58);
        double maxHW = w * 0.40;
        int noseCy = (int)(h * 0.46);
        double noseRx = w * 0.16, noseRy = h * 0.12;
        double outline = Math.max(1.5, w * 0.07);
        boolean glint = w >= 36;
        int footTop = (int)(h * (pose == Pose.JUMP ? 0.70 : 0.74));
        double shallow = h * 0.80;
        double deep = pose == Pose.JUMP ? h * 0.86 : h - 1;
        double lean = pose == Pose.RUN_A ? -0.35 : pose == Pose.RUN_B ? 0.35 : 0.0;
        double armR = Math.max(1.5, w * 0.07);

        java.util.function.DoubleUnaryOperator hwAt = y ->
                y <= coneBase ? maxHW * Math.pow(y / (double) coneBase, 0.72) : maxHW - (y - coneBase) * 0.15;
        java.util.function.DoubleUnaryOperator bottomAt = d -> {
            double u = Math.max(-1, Math.min(1, d / maxHW));
            double base = shallow + (deep - shallow) * (1 - Math.cos(2 * Math.PI * u)) / 2;
            return base + lean * (deep - shallow) * Math.sin(Math.PI * u);
        };

        // 1) Solid black silhouette (cone + wavy skirt).
        for (int y = 0; y < h; y++) {
            double hw = hwAt.applyAsDouble(y);
            for (int x = (int) Math.round(cx - hw); x <= cx + hw; x++) {
                if (x < 0 || x >= w) continue;
                if (y >= footTop && y > bottomAt.applyAsDouble(x - cx)) continue;
                g[y][x] = 'B';
            }
        }
        // 2) White belly inset below the nose.
        for (int y = noseCy; y < h; y++) {
            double hw = hwAt.applyAsDouble(y) - outline;
            for (int x = (int) Math.round(cx - hw); x <= cx + hw; x++) {
                if (x < 0 || x >= w || g[y][x] != 'B') continue;
                if (y >= footTop && y > bottomAt.applyAsDouble(x - cx) - outline) continue;
                g[y][x] = 'W';
            }
        }
        // 3) Arms.
        switch (pose) {
            case THROW -> {
                dukeArm(g, cx, h, -1, maxHW, armR);
                thickLine(g, cx + maxHW - 2, h * 0.56, cx + maxHW + 6, h * 0.50, armR, 'B');
            }
            case JUMP -> {
                thickLine(g, cx - maxHW + 2, h * 0.58, cx - maxHW - 4, h * 0.46, armR, 'B');
                thickLine(g, cx + maxHW - 2, h * 0.58, cx + maxHW + 4, h * 0.46, armR, 'B');
            }
            default -> {
                dukeArm(g, cx, h, -1, maxHW, armR);
                dukeArm(g, cx, h, +1, maxHW, armR);
            }
        }
        // 4) Glossy red nose with a glint.
        oval(g, cx, noseCy, noseRx, noseRy, 'R');
        if (glint) oval(g, cx - noseRx * 0.4, noseCy - noseRy * 0.45, noseRx * 0.3, noseRy * 0.34, 'H');
        return g;
    }

    private static void dukeArm(char[][] g, double cx, int h, int s, double maxHW, double r) {
        thickLine(g, cx + s * (maxHW - 2), h * 0.56, cx + s * (maxHW + 3), h * 0.68, r, 'B');
        thickLine(g, cx + s * (maxHW + 3), h * 0.68, cx + s * (maxHW - 3), h * 0.80, r, 'B');
    }

    /**
     * Side-facing Duke for the in-game poses — nose leading right so he faces the
     * oncoming obstacles. The cone leans forward, the front (right) edge is fuller
     * than the flatter back, and the nose protrudes from the leading edge. Feet
     * are static; motion comes from the renderer's small vertical bob.
     */
    private static char[][] dukeGridProfile(Pose pose, int w, int h) {
        double cx = (w - 1) / 2.0;
        char[][] g = new char[h][w];
        for (char[] row : g) java.util.Arrays.fill(row, ' ');

        int coneBase = (int)(h * 0.56);
        double maxHW = w * 0.36, lean = w * 0.14;
        int noseCy = (int)(h * 0.44);
        double noseRx = w * 0.15, noseRy = h * 0.13;
        double outline = Math.max(1.5, w * 0.07);
        boolean glint = w >= 36;                    // glint only survives at high res
        int footTop = (int)(h * (pose == Pose.JUMP ? 0.70 : 0.74));
        double shallow = h * 0.80;
        double deep = pose == Pose.JUMP ? h * 0.86 : h - 1;

        java.util.function.DoubleUnaryOperator axisAt = y ->
                y <= coneBase ? cx + lean * (1 - y / (double) coneBase) : cx;
        // Slightly convex cone so the crown isn't a razor-thin tip.
        java.util.function.DoubleUnaryOperator hwAt = y ->
                y <= coneBase ? maxHW * Math.pow(y / (double) coneBase, 0.72) : maxHW - (y - coneBase) * 0.10;
        java.util.function.DoubleUnaryOperator frontHW = y -> hwAt.applyAsDouble(y) * 1.12;
        java.util.function.DoubleUnaryOperator backHW = y -> hwAt.applyAsDouble(y) * 0.82;
        // Wavy skirt bottom: two symmetric feet (no stride tilt).
        java.util.function.DoubleUnaryOperator bottomAt = d -> {
            double u = Math.max(-1, Math.min(1, d / maxHW));
            return shallow + (deep - shallow) * (1 - Math.cos(2 * Math.PI * u)) / 2;
        };

        // 1) Black silhouette (leaning cone + wavy skirt).
        for (int y = 0; y < h; y++) {
            double axis = axisAt.applyAsDouble(y);
            double front = axis + frontHW.applyAsDouble(y), back = axis - backHW.applyAsDouble(y);
            for (int x = (int) Math.round(back); x <= front; x++) {
                if (x < 0 || x >= w) continue;
                if (y >= footTop && y > bottomAt.applyAsDouble(x - cx)) continue;
                g[y][x] = 'B';
            }
        }
        // 2) White belly (inset from the outline).
        for (int y = noseCy; y < h; y++) {
            double axis = axisAt.applyAsDouble(y);
            double front = axis + frontHW.applyAsDouble(y) - outline, back = axis - backHW.applyAsDouble(y) + outline;
            for (int x = (int) Math.round(back); x <= front; x++) {
                if (x < 0 || x >= w || g[y][x] != 'B') continue;
                if (y >= footTop && y > bottomAt.applyAsDouble(x - cx) - outline) continue;
                g[y][x] = 'W';
            }
        }
        // 3) Arm — only where it reads outside the body (thrust / lift).
        double frontEdge = axisAt.applyAsDouble((int)(h * 0.50)) + frontHW.applyAsDouble((int)(h * 0.50));
        if (pose == Pose.THROW) {
            thickLine(g, frontEdge - 2, h * 0.54, frontEdge + 6, h * 0.44, outline, 'B');
        } else if (pose == Pose.JUMP) {
            thickLine(g, frontEdge - 2, h * 0.52, frontEdge + 4, h * 0.40, outline, 'B');
        }
        // 4) Nose on the leading edge, with a glint.
        double nAxis = axisAt.applyAsDouble(noseCy);
        double nx = nAxis + frontHW.applyAsDouble(noseCy) - noseRx * 0.4;
        oval(g, nx, noseCy, noseRx, noseRy, 'R');
        if (glint) oval(g, nx - noseRx * 0.35, noseCy - noseRy * 0.4, noseRx * 0.3, noseRy * 0.34, 'H');
        return g;
    }

    private static void oval(char[][] g, double cx, double cy, double rx, double ry, char c) {
        int h = g.length, w = g[0].length;
        for (int y = (int)(cy - ry); y <= cy + ry; y++)
            for (int x = (int)(cx - rx); x <= cx + rx; x++) {
                if (x < 0 || x >= w || y < 0 || y >= h) continue;
                double dx = (x - cx) / rx, dy = (y - cy) / ry;
                if (dx * dx + dy * dy <= 1.0) g[y][x] = c;
            }
    }

    private static void thickLine(char[][] g, double x0, double y0, double x1, double y1, double r, char c) {
        int steps = (int)(Math.hypot(x1 - x0, y1 - y0) * 3) + 1;
        for (int i = 0; i <= steps; i++) {
            double t = i / (double) steps;
            oval(g, x0 + (x1 - x0) * t, y0 + (y1 - y0) * t, r, r, c);
        }
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
