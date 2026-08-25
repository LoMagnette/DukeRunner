package be.lomagnette.duke.runner.tui;

import dev.tamboui.style.Color;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class Sprites {

    private Sprites() {}

    // ── Character sprite data ─────────────────────────────────────
    // A character sprite is a stack of colored layers, drawn back-to-front.
    // Duke = the Java mascot (see src/main/resources/duke.png): a big solid
    // BLACK pointed cone for the head/body, a large glossy RED nose low-centre,
    // a WHITE belly below it, thin black arms akimbo, and a wavy two-foot skirt.
    // Poses are generated parametrically by dukeGrid() and coloured from this
    // palette, so every frame stays on-model.

    public static final Color DUKE_BLACK = Color.rgb(20, 20, 24);    // cone / arms / outline
    public static final Color DUKE_BODY = Color.rgb(246, 246, 250);  // white belly
    public static final Color DUKE_NOSE = Color.rgb(183, 26, 62);    // crimson nose
    public static final Color DUKE_GLINT = Color.rgb(255, 255, 255); // highlight on the nose

    // Duke sprite grid size (game/canvas units). The whole figure — arms and
    // feet included — fits in DUKE_W × DUKE_H; DUKE_CX is the horizontal centre
    // the renderer aligns to Duke's collision box.
    public static final int DUKE_W = 24;
    public static final int DUKE_H = 28;
    public static final double DUKE_CX = (DUKE_W - 1) / 2.0;

    /** One solid-color layer of a sprite. */
    public record Layer(Color color, double[][] points) {}

    /** A multi-layer colored sprite. */
    public record CharSprite(List<Layer> layers) {
        public int pointCount() {
            int n = 0;
            for (var l : layers) n += l.points().length;
            return n;
        }

        public boolean hasColor(Color c) {
            for (var l : layers) if (l.color().equals(c)) return true;
            return false;
        }
    }

    /** Duke's poses. Each maps to the same base figure with different legs/arms. */
    public enum Pose { SIT, RUN_A, RUN_B, JUMP, THROW }

    // Cell glyphs used by the generated Duke grid.
    private static final char BLK = 'B', WHT = 'W', RED = 'R', GLT = 'H';

    /**
     * Front-facing Duke on a {@link #DUKE_W}×{@link #DUKE_H} grid straight from
     * the mascot's geometry (see duke.png): a solid black cone tapering to the
     * crown, a white belly inset below the nose, a glossy red nose with a glint,
     * thin black arms akimbo, and a wavy skirt that splits into two feet. Used
     * for the title mascot; the in-game poses use {@link #dukeGridProfile}.
     * Grid row 0 is the crown (top).
     */
    private static char[][] dukeGridFront(Pose pose) {
        int w = DUKE_W, h = DUKE_H;
        double cx = DUKE_CX;
        char[][] g = new char[h][w];
        for (char[] row : g) java.util.Arrays.fill(row, ' ');

        int coneBase = (int) (h * 0.58);        // cone tip → shoulders
        double maxHW = w * 0.40;                 // half-width at the shoulders
        int noseCy = (int) (h * 0.46);
        double noseRx = w * 0.16, noseRy = h * 0.12;
        double outline = Math.max(1.5, w * 0.07); // black frame around the belly
        boolean glint = w >= 36;                   // glint only survives at high res
        int footTop = (int) (h * (pose == Pose.JUMP ? 0.70 : 0.74));
        double shallow = h * 0.80;                 // skirt bottom at notch / corners
        double deep = pose == Pose.JUMP ? h * 0.86 : h - 1; // feet tuck up on jump
        double lean = pose == Pose.RUN_A ? -0.35 : pose == Pose.RUN_B ? 0.35 : 0.0;

        // Outer silhouette half-width by row: a slightly convex cone (so the tip
        // isn't a 1-pixel antenna once downsampled), then a skirt.
        java.util.function.DoubleUnaryOperator hwAt = y ->
                y <= coneBase ? maxHW * Math.pow(y / (double) coneBase, 0.72) : maxHW - (y - coneBase) * 0.15;
        // Lowest skirt row for a signed offset from centre: a wave with two deep
        // feet (|u|≈0.5) and a shallow notch/corners (u=0,±1), tilted by lean.
        java.util.function.DoubleUnaryOperator bottomAt = d -> {
            double u = Math.max(-1, Math.min(1, d / maxHW));
            double base = shallow + (deep - shallow) * (1 - Math.cos(2 * Math.PI * u)) / 2;
            return base + lean * (deep - shallow) * Math.sin(Math.PI * u);
        };

        // 1) Solid black silhouette (cone above, wavy skirt below).
        for (int y = 0; y < h; y++) {
            double hw = hwAt.applyAsDouble(y);
            for (int x = (int) Math.round(cx - hw); x <= cx + hw; x++) {
                if (x < 0 || x >= w) continue;
                if (y >= footTop && y > bottomAt.applyAsDouble(x - cx)) continue;
                g[y][x] = BLK;
            }
        }
        // 2) White belly: interior below the nose, inset from the black outline.
        for (int y = noseCy; y < h; y++) {
            double hw = hwAt.applyAsDouble(y) - outline;
            for (int x = (int) Math.round(cx - hw); x <= cx + hw; x++) {
                if (x < 0 || x >= w || g[y][x] != BLK) continue;
                if (y >= footTop && y > bottomAt.applyAsDouble(x - cx) - outline) continue;
                g[y][x] = WHT;
            }
        }
        // 3) Arms.
        double armR = Math.max(1.5, w * 0.07);
        switch (pose) {
            case THROW -> { // left hand on hip, right arm thrust forward
                dukeArm(g, -1, maxHW, armR);
                thickLine(g, cx + maxHW - 2, h * 0.56, cx + maxHW + 6, h * 0.50, armR, BLK);
            }
            case JUMP -> { // both arms flung outward/up
                thickLine(g, cx - maxHW + 2, h * 0.58, cx - maxHW - 4, h * 0.46, armR, BLK);
                thickLine(g, cx + maxHW - 2, h * 0.58, cx + maxHW + 4, h * 0.46, armR, BLK);
            }
            default -> { // akimbo
                dukeArm(g, -1, maxHW, armR);
                dukeArm(g, +1, maxHW, armR);
            }
        }
        // 4) Glossy red nose with a white glint on its upper-left.
        oval(g, cx, noseCy, noseRx, noseRy, RED);
        if (glint) oval(g, cx - noseRx * 0.4, noseCy - noseRy * 0.45, noseRx * 0.3, noseRy * 0.34, GLT);
        return g;
    }

    /** One akimbo arm: down-out to the elbow, then back in to the hip. */
    private static void dukeArm(char[][] g, int s, double maxHW, double r) {
        double cx = DUKE_CX, h = DUKE_H;
        thickLine(g, cx + s * (maxHW - 2), h * 0.56, cx + s * (maxHW + 3), h * 0.68, r, BLK);
        thickLine(g, cx + s * (maxHW + 3), h * 0.68, cx + s * (maxHW - 3), h * 0.80, r, BLK);
    }

    /**
     * Side-facing Duke used for the in-game poses — nose leading to the right so
     * he clearly faces the oncoming obstacles. Same black-cone / red-nose /
     * white-belly build as the front view, but the cone leans forward and the
     * front (right) edge is fuller than the flatter back, with the big nose
     * protruding from the leading edge. Feet are static (a faked leg-stride read
     * as jitter at this resolution); the sense of motion comes from the small
     * vertical bob the renderer applies. JUMP tucks the feet; THROW thrusts an arm.
     */
    private static char[][] dukeGridProfile(Pose pose) {
        int w = DUKE_W, h = DUKE_H;
        double cx = DUKE_CX;
        char[][] g = new char[h][w];
        for (char[] row : g) java.util.Arrays.fill(row, ' ');

        int coneBase = (int) (h * 0.56);
        double maxHW = w * 0.36;
        double lean = w * 0.14;                    // cone tip leans forward (right)
        int noseCy = (int) (h * 0.44);
        double noseRx = w * 0.15, noseRy = h * 0.13;
        double outline = Math.max(1.5, w * 0.07);
        boolean glint = w >= 36;                   // glint only survives at high res
        int footTop = (int) (h * (pose == Pose.JUMP ? 0.70 : 0.74));
        double shallow = h * 0.80;
        double deep = pose == Pose.JUMP ? h * 0.86 : h - 1;

        java.util.function.DoubleUnaryOperator axisAt = y ->
                y <= coneBase ? cx + lean * (1 - y / (double) coneBase) : cx;
        // Slightly convex cone: widens fast near the crown so the tip isn't a
        // 1-pixel "antenna" once the terminal downsamples it.
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
                g[y][x] = BLK;
            }
        }
        // 2) White belly (inset from the outline).
        for (int y = noseCy; y < h; y++) {
            double axis = axisAt.applyAsDouble(y);
            double front = axis + frontHW.applyAsDouble(y) - outline, back = axis - backHW.applyAsDouble(y) + outline;
            for (int x = (int) Math.round(back); x <= front; x++) {
                if (x < 0 || x >= w || g[y][x] != BLK) continue;
                if (y >= footTop && y > bottomAt.applyAsDouble(x - cx) - outline) continue;
                g[y][x] = WHT;
            }
        }
        // 3) Arm — only where it reads outside the body (thrust / lift); the run
        //    cycle is carried by the striding feet, so no arm over the belly.
        double frontEdge = axisAt.applyAsDouble((int) (h * 0.50)) + frontHW.applyAsDouble((int) (h * 0.50));
        if (pose == Pose.THROW) {
            thickLine(g, frontEdge - 2, h * 0.54, frontEdge + 6, h * 0.44, outline, BLK);
        } else if (pose == Pose.JUMP) {
            thickLine(g, frontEdge - 2, h * 0.52, frontEdge + 4, h * 0.40, outline, BLK);
        }
        // 4) Nose on the leading edge, with a glint.
        double nAxis = axisAt.applyAsDouble(noseCy);
        double nx = nAxis + frontHW.applyAsDouble(noseCy) - noseRx * 0.4;
        oval(g, nx, noseCy, noseRx, noseRy, RED);
        if (glint) oval(g, nx - noseRx * 0.35, noseCy - noseRy * 0.4, noseRx * 0.3, noseRy * 0.34, GLT);
        return g;
    }

    private static void oval(char[][] g, double cx, double cy, double rx, double ry, char c) {
        int h = g.length, w = g[0].length;
        for (int y = (int) (cy - ry); y <= cy + ry; y++)
            for (int x = (int) (cx - rx); x <= cx + rx; x++) {
                if (x < 0 || x >= w || y < 0 || y >= h) continue;
                double dx = (x - cx) / rx, dy = (y - cy) / ry;
                if (dx * dx + dy * dy <= 1.0) g[y][x] = c;
            }
    }

    private static void thickLine(char[][] g, double x0, double y0, double x1, double y1, double r, char c) {
        int steps = (int) (Math.hypot(x1 - x0, y1 - y0) * 3) + 1;
        for (int i = 0; i <= steps; i++) {
            double t = i / (double) steps;
            oval(g, x0 + (x1 - x0) * t, y0 + (y1 - y0) * t, r, r, c);
        }
    }

    /** Colour a generated Duke grid into layered points (row 0 = top). */
    private static CharSprite dukeSprite(Pose pose) {
        // Title mascot faces front; the in-game run/jump/throw poses are profile.
        char[][] g = pose == Pose.SIT ? dukeGridFront(pose) : dukeGridProfile(pose);
        int h = g.length;
        var black = new ArrayList<double[]>();
        var white = new ArrayList<double[]>();
        var red = new ArrayList<double[]>();
        var glint = new ArrayList<double[]>();
        for (int i = 0; i < h; i++) {
            int y = h - 1 - i; // first row = top = highest y
            for (int x = 0; x < g[i].length; x++) {
                switch (g[i][x]) {
                    case BLK -> black.add(new double[]{x, y});
                    case WHT -> white.add(new double[]{x, y});
                    case RED -> red.add(new double[]{x, y});
                    case GLT -> glint.add(new double[]{x, y});
                    default -> { }
                }
            }
        }
        // Non-overlapping layers, so draw order is only cosmetic.
        var layers = new ArrayList<Layer>();
        layers.add(new Layer(DUKE_BODY, toArray(white)));
        layers.add(new Layer(DUKE_BLACK, toArray(black)));
        layers.add(new Layer(DUKE_NOSE, toArray(red)));
        layers.add(new Layer(DUKE_GLINT, toArray(glint)));
        return new CharSprite(layers);
    }

    // ── Obstacle palette ──────────────────────────────────────────
    // Bright, saturated "hazard" colors with an identifying detail per object
    // (glowing screens, warning LEDs, a colored intern) so obstacles read as
    // distinct foreground devices against the dimmed background scenery.

    public static final Color OBST_FRAME = Color.rgb(58, 62, 72);    // dark device chassis
    public static final Color OBST_SCREEN = Color.rgb(90, 220, 140);  // glowing terminal screen
    public static final Color OBST_METAL = Color.rgb(184, 190, 202);  // brushed metal
    public static final Color OBST_LED = Color.rgb(244, 92, 72);      // warning LED
    public static final Color OBST_NEON = Color.rgb(96, 214, 232);    // cyan glow / code / eyes
    public static final Color OBST_COFFEE = Color.rgb(96, 60, 34);    // coffee (dark)
    public static final Color OBST_COFFEE_LIGHT = Color.rgb(165, 110, 65); // coffee shine
    public static final Color OBST_CUP = Color.rgb(232, 234, 240);    // paper coffee cup
    public static final Color OBST_BUG_BODY = Color.rgb(198, 60, 70); // beetle shell
    public static final Color OBST_BUG_DARK = Color.rgb(48, 44, 58);  // legs / seam
    public static final Color OBST_WOOD = Color.rgb(134, 92, 54);     // podium wood body
    public static final Color OBST_BRIGHT = Color.rgb(238, 210, 120); // lit lectern panel
    public static final Color OBST_SKIN = Color.rgb(232, 190, 158);   // intern skin
    public static final Color OBST_SHIRT = Color.rgb(84, 138, 210);   // intern shirt

    /**
     * Parse a color bitmap into a layered sprite. Each non-space glyph is looked
     * up in {@code palette}; unmapped glyphs fall back to the {@code '#'} entry.
     * Points are grouped into one {@link Layer} per color, in first-seen order.
     */
    private static CharSprite fromPalette(Map<Character, Color> palette, String... rows) {
        Map<Color, List<double[]>> byColor = new LinkedHashMap<>();
        for (int i = 0; i < rows.length; i++) {
            int y = rows.length - 1 - i; // first row = top = highest y
            for (int x = 0; x < rows[i].length(); x++) {
                char c = rows[i].charAt(x);
                if (c == ' ') continue;
                Color col = palette.getOrDefault(c, palette.get('#'));
                if (col == null) continue;
                byColor.computeIfAbsent(col, k -> new ArrayList<>()).add(new double[]{x, y});
            }
        }
        var layers = new ArrayList<Layer>();
        byColor.forEach((col, pts) -> layers.add(new Layer(col, toArray(pts))));
        return new CharSprite(layers);
    }

    private static Map<Character, Color> palette(Object... pairs) {
        var m = new LinkedHashMap<Character, Color>();
        for (int i = 0; i < pairs.length; i += 2) {
            m.put((Character) pairs[i], (Color) pairs[i + 1]);
        }
        return m;
    }

    // ── Duke sprites ────────────────────────────────────────────
    // Generated once from dukeGrid()/dukeSprite() into immutable constants and
    // returned directly by the accessors — the render loop runs at ~60 FPS, so
    // rebuilding the poses every frame would be pure GC churn.

    private static final CharSprite DUKE_RUNNING = dukeSprite(Pose.RUN_A);
    private static final CharSprite DUKE_RUNNING_B = dukeSprite(Pose.RUN_B);
    private static final CharSprite DUKE_JUMPING = dukeSprite(Pose.JUMP);
    private static final CharSprite DUKE_THROWING = dukeSprite(Pose.THROW);
    private static final CharSprite DUKE_SITTING = dukeSprite(Pose.SIT);

    /** Run-cycle frame. {@code frame} alternates the stride to fake running. */
    public static CharSprite dukeRunning(int frame) {
        return (frame & 1) == 0 ? DUKE_RUNNING : DUKE_RUNNING_B;
    }

    public static CharSprite dukeRunning() { return DUKE_RUNNING; }
    public static CharSprite dukeJumping() { return DUKE_JUMPING; }
    public static CharSprite dukeThrowing() { return DUKE_THROWING; }
    public static CharSprite dukeSitting() { return DUKE_SITTING; }

    // Obstacles are layered color sprites; decorations stay mono silhouettes.
    private static final CharSprite CONFERENCE_STAGE = buildConferenceStage();
    private static final CharSprite LAPTOP_STACK_WIDE = buildLaptopStackWide();
    private static final CharSprite LAPTOP_STACK_TALL = buildLaptopStackTall();
    private static final CharSprite COFFEE_SPILL = buildCoffeeSpill();
    private static final CharSprite CONFUSED_INTERN = buildConfusedIntern();
    private static final CharSprite SLOW_BUILD_SERVER = buildSlowBuildServer();
    private static final double[][] COFFEE_CUP = buildCoffeeCup();
    private static final double[][] TERMINAL = buildTerminal();
    private static final double[][] GIT_BRANCH = buildGitBranch();
    private static final double[][] IDE_ICON = buildIdeIcon();
    private static final double[][] DOCKER_WHALE = buildDockerWhale();
    private static final double[][] CLOUD = buildCloud();

    public static CharSprite conferenceStage() { return CONFERENCE_STAGE; }
    public static CharSprite laptopStackWide() { return LAPTOP_STACK_WIDE; }
    public static CharSprite laptopStackTall() { return LAPTOP_STACK_TALL; }
    public static CharSprite coffeeSpill() { return COFFEE_SPILL; }
    public static CharSprite confusedIntern() { return CONFUSED_INTERN; }
    public static CharSprite slowBuildServer() { return SLOW_BUILD_SERVER; }
    public static double[][] coffeeCup() { return COFFEE_CUP; }
    public static double[][] terminal() { return TERMINAL; }
    public static double[][] gitBranch() { return GIT_BRANCH; }
    public static double[][] ideIcon() { return IDE_ICON; }
    public static double[][] dockerWhale() { return DOCKER_WHALE; }
    public static double[][] cloud() { return CLOUD; }

    // ── Obstacles (Java-themed, same collision dimensions) ────────
    // Using fromPalette for precise, readable multi-color silhouettes.

    // Lectern/podium (6w × 26h): wood body + a bright lectern panel on top.
    private static CharSprite buildConferenceStage() {
        return fromPalette(palette('W', OBST_WOOD, 'K', OBST_BRIGHT),
            " KKKK ",  // podium top panel (bright)
            "WWWWWW",  // lip
            "WWWWWW",
            " WWWW ",  // front panel
            " WWWW ",
            " WWWW ",
            " WWWW ",
            " WWWW ",
            " WWWW ",
            " WWWW ",
            " WWWW ",
            " WWWW ",
            " WWWW ",
            " WWWW ",
            " WWWW ",
            " WWWW ",  // panel bottom
            "  WW  ",  // stem
            "  WW  ",
            "  WW  ",
            "  WW  ",
            "  WW  ",
            " WWWW ",  // base widens
            " WWWW ",
            "WWWWWW",  // base
            "WWWWWW",
            "WWWWWW"   // base bottom
        );
    }

    // Three monitors on a desk (18w × 9h): green screens in dark bezels on metal.
    private static CharSprite buildLaptopStackWide() {
        return fromPalette(palette('F', OBST_FRAME, 'S', OBST_SCREEN, 'M', OBST_METAL),
            "  FF    FF    FF  ",  // screen tops
            " FSSF  FSSF  FSSF ",  // screens (glow)
            " FSSF  FSSF  FSSF ",
            " FFFF  FFFF  FFFF ",  // screen bottoms
            "MMMMMMMMMMMMMMMMMM",  // desk surface
            "MMMMMMMMMMMMMMMMMM",
            "MMMMMMMMMMMMMMMMMM",
            "MMMMMMMMMMMMMMMMMM",
            "MMMMMMMMMMMMMMMMMM"   // base
        );
    }

    // Three laptops stacked vertically (8w × 25h): green screens, metal keyboards.
    private static CharSprite buildLaptopStackTall() {
        return fromPalette(palette('S', OBST_SCREEN, 'M', OBST_METAL),
            "  SSSS  ",  // top laptop screen
            " SSSSSS ",
            " SSSSSS ",
            " SSSSSS ",
            " SSSSSS ",
            "MMMMMMMM",  // keyboard
            "MMMMMMMM",
            "        ",  // gap
            "        ",
            "  SSSS  ",  // middle laptop screen
            " SSSSSS ",
            " SSSSSS ",
            " SSSSSS ",
            " SSSSSS ",
            "MMMMMMMM",  // keyboard
            "MMMMMMMM",
            "        ",  // gap
            "        ",
            "  SSSS  ",  // bottom laptop screen
            " SSSSSS ",
            " SSSSSS ",
            " SSSSSS ",
            " SSSSSS ",
            "MMMMMMMM",  // keyboard
            "MMMMMMMM"   // base
        );
    }

    // Puddle on ground (22w × 6h): dark coffee with a lighter shine on top.
    private static CharSprite buildCoffeeSpill() {
        return fromPalette(palette('C', OBST_COFFEE, 'H', OBST_COFFEE_LIGHT),
            "  HHHHHHHHHHHHHHHHHH  ",  // top edge (shine)
            " HHHHHHHHHHHHHHHHHHHH ",  // wider
            "CCCCCCCCCCCCCCCCCCCCCC",  // widest
            "CCCCCCCCCCCCCCCCCCCCCC",  // widest
            " CCCCCCCCCCCCCCCCCCCC ",  // narrowing
            "   CCCCCCCCCCCCCCCC   "   // base
        );
    }

    // Person with arms out (14w × 14h): skin head, colored shirt.
    private static CharSprite buildConfusedIntern() {
        return fromPalette(palette('S', OBST_SKIN, 'T', OBST_SHIRT),
            "     SSSS     ",  // head top
            "    SSSSSS    ",  // head
            "    SSSSSS    ",  // head
            "     SSSS     ",  // chin
            "      TT      ",  // neck
            "  TTTTTTTTTT  ",  // shoulders
            "TTTTTTTTTTTTTT",  // arms extended wide
            "TTTTTTTTTTTTTT",  // arms + torso
            "  TTTTTTTTTT  ",  // torso
            "    TTTTTT    ",  // waist
            "    TTTTTT    ",  // hips
            "    TT  TT    ",  // legs
            "    TT  TT    ",  // legs
            "   TTT  TTT   "   // feet
        );
    }

    // Server rack (7w × 10h): dark chassis with warning-LED drive bays.
    private static CharSprite buildSlowBuildServer() {
        return fromPalette(palette('F', OBST_FRAME, 'L', OBST_LED),
            "FFFFFFF",  // top frame
            "F LLL F",  // drive bay (lit)
            "F     F",  // empty slot
            "F LLL F",  // drive bay
            "F     F",  // empty slot
            "F LLL F",  // drive bay
            "F     F",  // empty slot
            "F LLL F",  // drive bay
            "FFFFFFF",  // bottom frame
            "FFFFFFF"   // base
        );
    }

    // ── Decorations (Java-themed, ~5w × 5-8h) ──────────────────

    // Coffee cup silhouette (~5w × 8h)
    private static double[][] buildCoffeeCup() {
        var pts = new ArrayList<double[]>();
        // Cup body
        fillRect(pts, 0, 0, 3, 4);
        // Handle
        dot(pts, 4, 2); dot(pts, 4, 3);
        // Steam wisps
        dot(pts, 1, 5); dot(pts, 2, 6); dot(pts, 1, 7);
        return toArray(pts);
    }

    // Terminal prompt >_ (~5w × 4h)
    private static double[][] buildTerminal() {
        var pts = new ArrayList<double[]>();
        // > character
        dot(pts, 0, 3); dot(pts, 1, 2); dot(pts, 2, 1); dot(pts, 1, 0);
        // _ underscore cursor
        dot(pts, 3, 0); dot(pts, 4, 0);
        return toArray(pts);
    }

    // Git branch icon (~5w × 5h)
    private static double[][] buildGitBranch() {
        var pts = new ArrayList<double[]>();
        // Main line (vertical)
        dot(pts, 1, 0); dot(pts, 1, 1); dot(pts, 1, 2); dot(pts, 1, 3); dot(pts, 1, 4);
        // Branch line
        dot(pts, 2, 3); dot(pts, 3, 4);
        // Node dots
        dot(pts, 0, 0); dot(pts, 2, 0); // base
        dot(pts, 4, 4); // branch tip
        return toArray(pts);
    }

    // IDE window/panel shape (~5w × 5h)
    private static double[][] buildIdeIcon() {
        var pts = new ArrayList<double[]>();
        // Window frame
        fillRect(pts, 0, 0, 4, 4);
        // Title bar highlight
        dot(pts, 0, 4); dot(pts, 1, 4); dot(pts, 2, 4);
        // Code lines inside (gaps make it look like code)
        return toArray(pts);
    }

    // Docker whale shape (~5w × 4h)
    private static double[][] buildDockerWhale() {
        var pts = new ArrayList<double[]>();
        // Body
        dot(pts, 1, 0); dot(pts, 2, 0); dot(pts, 3, 0); dot(pts, 4, 0);
        dot(pts, 0, 1); dot(pts, 1, 1); dot(pts, 2, 1); dot(pts, 3, 1); dot(pts, 4, 1);
        // Containers on top
        dot(pts, 1, 2); dot(pts, 2, 2); dot(pts, 3, 2);
        dot(pts, 1, 3); dot(pts, 3, 3);
        return toArray(pts);
    }

    // Cloud puff (~5w × 5h)
    private static double[][] buildCloud() {
        var pts = new ArrayList<double[]>();
        dot(pts, 1, 0); dot(pts, 2, 0); dot(pts, 3, 0);
        dot(pts, 0, 1); dot(pts, 1, 1); dot(pts, 2, 1); dot(pts, 3, 1); dot(pts, 4, 1);
        dot(pts, 0, 2); dot(pts, 1, 2); dot(pts, 2, 2); dot(pts, 3, 2); dot(pts, 4, 2);
        dot(pts, 1, 3); dot(pts, 2, 3); dot(pts, 3, 3);
        return toArray(pts);
    }

    // ── Utilities ─────────────────────────────────────────────────

    public static double[][] translate(double[][] sprite, double dx, double dy) {
        var result = new double[sprite.length][2];
        for (int i = 0; i < sprite.length; i++) {
            result[i][0] = sprite[i][0] + dx;
            result[i][1] = sprite[i][1] + dy;
        }
        return result;
    }

    private static void dot(List<double[]> pts, int x, int y) {
        pts.add(new double[]{x, y});
    }

    private static void fillRect(List<double[]> pts, int x1, int y1, int x2, int y2) {
        for (int x = x1; x <= x2; x++) {
            for (int y = y1; y <= y2; y++) {
                pts.add(new double[]{x, y});
            }
        }
    }

    private static double[][] toArray(List<double[]> pts) {
        return pts.toArray(new double[0][]);
    }
}
