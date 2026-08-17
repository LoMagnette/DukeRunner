package be.lomagnette.duke.runner.tui;

import dev.tamboui.style.Color;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class Sprites {

    private Sprites() {}

    // ── Character sprite data ─────────────────────────────────────
    // A character sprite is a stack of colored layers, drawn back-to-front.
    // Duke's palette: black head, white body, red nose.

    public static final Color DUKE_HEAD = Color.rgb(22, 22, 26); // near-black
    public static final Color DUKE_BODY = Color.rgb(240, 240, 245); // white
    public static final Color DUKE_NOSE = Color.rgb(214, 74, 61); // red

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

    /**
     * Split a mono silhouette into Duke's palette purely from geometry so every
     * pose colors consistently without hand-painting each bitmap: the supplied
     * {@code nose} points become the red layer, the top slice of what remains is
     * the black head, and everything below is the white body. Layers are ordered
     * back-to-front (nose drawn last, on top).
     */
    private static CharSprite colorize(double[][] silhouette, double[][] nose) {
        double minY = Double.POSITIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;
        for (var p : silhouette) {
            minY = Math.min(minY, p[1]);
            maxY = Math.max(maxY, p[1]);
        }
        double height = Math.max(1, maxY - minY);
        double headCut = maxY - height * 0.30; // top 30% → black head

        Set<Long> noseKeys = new HashSet<>();
        for (var p : nose) noseKeys.add(key(p[0], p[1]));

        var body = new ArrayList<double[]>();
        var head = new ArrayList<double[]>();
        for (var p : silhouette) {
            if (noseKeys.contains(key(p[0], p[1]))) continue; // owned by the nose layer
            if (p[1] >= headCut) head.add(p);
            else body.add(p);
        }

        var layers = new ArrayList<Layer>();
        layers.add(new Layer(DUKE_BODY, toArray(body)));
        layers.add(new Layer(DUKE_HEAD, toArray(head)));
        layers.add(new Layer(DUKE_NOSE, nose));
        return new CharSprite(layers);
    }

    private static long key(double x, double y) {
        return (((long) Math.round(x)) << 20) ^ (long) Math.round(y);
    }

    // ── Obstacle palette ──────────────────────────────────────────
    // Bright, saturated "hazard" colors with an identifying detail per object
    // (glowing screens, warning LEDs, a colored intern) so obstacles read as
    // distinct foreground devices against the dimmed background scenery.

    public static final Color OBST_FRAME = Color.rgb(58, 62, 72);    // dark device chassis
    public static final Color OBST_SCREEN = Color.rgb(90, 220, 140);  // glowing terminal screen
    public static final Color OBST_METAL = Color.rgb(184, 190, 202);  // brushed metal
    public static final Color OBST_LED = Color.rgb(244, 92, 72);      // warning LED
    public static final Color OBST_WOOD = Color.rgb(150, 100, 55);    // podium wood
    public static final Color OBST_BRIGHT = Color.rgb(236, 232, 90);  // lectern panel / highlight
    public static final Color OBST_COFFEE = Color.rgb(96, 60, 34);    // coffee (dark)
    public static final Color OBST_COFFEE_LIGHT = Color.rgb(165, 110, 65); // coffee shine
    public static final Color OBST_SKIN = Color.rgb(240, 200, 150);   // intern head
    public static final Color OBST_SHIRT = Color.rgb(214, 74, 110);   // intern shirt

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

    // ── Duke sprites (braille bitmaps, ~36w × 24h) ──────────────
    // '#' = lit dot. First row = top (highest y). Facing right.
    // Duke = Java mascot: inverted teardrop blob (narrow pointed
    //   crown, wide round bottom), HUGE red nose bump, no eyes/mouth,
    //   thin noodle arms, tiny nub feet at bottom.
    //
    // All sprites are parsed once into immutable constants below and
    // returned directly by the public accessors — the render loop runs
    // at ~60 FPS, so re-parsing bitmaps every frame was pure GC churn.

    private static final CharSprite DUKE_RUNNING = buildDukeRunning();
    private static final CharSprite DUKE_RUNNING_B = buildDukeRunningB();
    private static final CharSprite DUKE_JUMPING = buildDukeJumping();
    private static final CharSprite DUKE_THROWING = buildDukeThrowing();
    private static final CharSprite DUKE_SITTING = buildDukeSitting();

    /** Run-cycle frame. {@code frame} alternates the nub feet to fake a stride. */
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

    private static CharSprite buildDukeRunning() {
        double[][] body = fromBitmap(
            "         ####                         ",  // crown tip
            "        ######                        ",  // crown widens fast
            "       ########                       ",  // head
            "      ##########                      ",  // head wider
            "     ############                     ",  // face
            "    ##############                    ",  // face wider (14w)
            "    ##################                ",  // nose bulge (18w)
            "    ####################              ",  // nose peak (20w, widest!)
            "    ##################                ",  // nose bulge (18w)
            "    ##############                    ",  // below nose (14w)
            "    ###############                   ",  // body (15w)
            "   ################                   ",  // body (16w)
            "   ################                   ",  // body
            "   ################                   ",  // body
            "   ################                   ",  // body
            "   ################                   ",  // body
            "   ################                   ",  // body
            "   ################                   ",  // body
            "   ################                   ",  // body (16w bottom)
            "    ##############                    ",  // taper
            "     ############                     ",  // taper
            "      ##########                      ",  // rounding
            "       ####  ####                     ",  // nub feet
            "       ###    ###                     "   // nub tips
        );
        // Nose: fully red oval on right side (rows 5-9)
        double[][] accent = {
            {16, 18}, {17, 18},
            {16, 17}, {17, 17}, {18, 17}, {19, 17}, {20, 17}, {21, 17},
            {16, 16}, {17, 16}, {18, 16}, {19, 16}, {20, 16}, {21, 16}, {22, 16}, {23, 16},
            {16, 15}, {17, 15}, {18, 15}, {19, 15}, {20, 15}, {21, 15},
            {16, 14}, {17, 14}
        };
        return colorize(body, accent);
    }

    // Second run frame: nub feet spread wider apart to fake a stride.
    private static CharSprite buildDukeRunningB() {
        double[][] body = fromBitmap(
            "         ####                         ",  // crown tip
            "        ######                        ",  // crown widens fast
            "       ########                       ",  // head
            "      ##########                      ",  // head wider
            "     ############                     ",  // face
            "    ##############                    ",  // face wider (14w)
            "    ##################                ",  // nose bulge (18w)
            "    ####################              ",  // nose peak (20w, widest!)
            "    ##################                ",  // nose bulge (18w)
            "    ##############                    ",  // below nose (14w)
            "    ###############                   ",  // body (15w)
            "   ################                   ",  // body (16w)
            "   ################                   ",  // body
            "   ################                   ",  // body
            "   ################                   ",  // body
            "   ################                   ",  // body
            "   ################                   ",  // body
            "   ################                   ",  // body
            "   ################                   ",  // body (16w bottom)
            "    ##############                    ",  // taper
            "     ############                     ",  // taper
            "      ##########                      ",  // rounding
            "      ####    ####                    ",  // nub feet (spread)
            "     ###        ###                   "   // nub tips (spread)
        );
        // Same red nose as the primary run frame (24 rows).
        double[][] accent = {
            {16, 18}, {17, 18},
            {16, 17}, {17, 17}, {18, 17}, {19, 17}, {20, 17}, {21, 17},
            {16, 16}, {17, 16}, {18, 16}, {19, 16}, {20, 16}, {21, 16}, {22, 16}, {23, 16},
            {16, 15}, {17, 15}, {18, 15}, {19, 15}, {20, 15}, {21, 15},
            {16, 14}, {17, 14}
        };
        return colorize(body, accent);
    }

    private static CharSprite buildDukeJumping() {
        double[][] body = fromBitmap(
            "         ####                         ",  // crown tip
            "        ######                        ",  // crown
            "       ########                       ",  // head
            "      ##########                      ",  // head wider
            "     ############                     ",  // face
            "    ##############                    ",  // face wider (14w)
            "    ##################                ",  // nose bulge (18w)
            "    ####################              ",  // nose peak (20w)
            "    ##################                ",  // nose bulge (18w)
            "    ##############                    ",  // below nose (14w)
            "    ###############                   ",  // body (15w)
            "   ################                   ",  // body (16w)
            "   ################                   ",  // body
            "   ################                   ",  // body
            "   ################                   ",  // body
            "   ################                   ",  // body (16w)
            "    ##############                    ",  // taper
            "     ############                     ",  // compact
            "     ######  ######                   ",  // tucked nubs
            "      #####  #####                    "   // tucked nubs
        );
        // Nose accent (20 rows → row 5 = y14)
        double[][] accent = {
            {16, 14}, {17, 14},
            {16, 13}, {17, 13}, {18, 13}, {19, 13}, {20, 13}, {21, 13},
            {16, 12}, {17, 12}, {18, 12}, {19, 12}, {20, 12}, {21, 12}, {22, 12}, {23, 12},
            {16, 11}, {17, 11}, {18, 11}, {19, 11}, {20, 11}, {21, 11},
            {16, 10}, {17, 10}
        };
        return colorize(body, accent);
    }

    private static CharSprite buildDukeThrowing() {
        double[][] body = fromBitmap(
            "         ####                         ",  // crown tip
            "        ######                        ",  // crown
            "       ########                       ",  // head
            "      ##########                      ",  // head wider
            "     ############                     ",  // face
            "    ##############                    ",  // face wider (14w)
            "    ##################                ",  // nose bulge (18w)
            "    ####################              ",  // nose peak (20w)
            "    ##################                ",  // nose bulge (18w)
            "    ##############                    ",  // below nose (14w)
            "    ###############                   ",  // body (15w)
            "   ################                   ",  // body (16w)
            "   ################  ###              ",  // body + arm start
            "   ################   ####            ",  // body + arm extending
            "   ################    ####           ",  // body + hand
            "   ################                   ",  // body
            "   ################                   ",  // body
            "   ################                   ",  // body
            "   ################                   ",  // body (16w bottom)
            "    ##############                    ",  // taper
            "     ############                     ",  // taper
            "      ##########                      ",  // rounding
            "       ####  ####                     ",  // nub feet
            "       ###    ###                     "   // nub tips
        );
        // Same nose as running (24 rows)
        double[][] accent = {
            {16, 18}, {17, 18},
            {16, 17}, {17, 17}, {18, 17}, {19, 17}, {20, 17}, {21, 17},
            {16, 16}, {17, 16}, {18, 16}, {19, 16}, {20, 16}, {21, 16}, {22, 16}, {23, 16},
            {16, 15}, {17, 15}, {18, 15}, {19, 15}, {20, 15}, {21, 15},
            {16, 14}, {17, 14}
        };
        return colorize(body, accent);
    }

    // Title screen: front-facing Duke (~36w × 24h)
    private static CharSprite buildDukeSitting() {
        double[][] body = fromBitmap(
            "              ##                      ",  // crown tip
            "             ####                     ",  // crown
            "            ######                    ",  // head
            "           ########                   ",  // head wider
            "          ##########                  ",  // face
            "         ############                 ",  // face wider
            "        ##############                ",  // face widest
            "       ################               ",  // upper body
            "      ##################              ",  // nose area
            "      ##################              ",  // nose center
            "      ##################              ",  // nose area
            "     ####################             ",  // body wider
            "     ####################             ",  // body
            "    ######################            ",  // body widest
            "    ######################            ",  // body
            "    ######################            ",  // body
            "    ######################            ",  // body
            "    ######################            ",  // wide bottom
            "    ######################            ",  // wide at bottom!
            "     ####################             ",  // taper
            "      ##################              ",  // taper
            "       ######    ######               ",  // nub feet
            "        #####    #####                ",  // nubs
            "        ####      ####                "   // nub tips
        );
        // Big centered nose oval (front view)
        double[][] accent = {
            {13, 16}, {14, 16}, {15, 16}, {16, 16},
            {11, 15}, {12, 15}, {13, 15}, {14, 15}, {15, 15}, {16, 15}, {17, 15}, {18, 15},
            {10, 14}, {11, 14}, {12, 14}, {13, 14}, {14, 14}, {15, 14}, {16, 14}, {17, 14}, {18, 14}, {19, 14},
            {11, 13}, {12, 13}, {13, 13}, {14, 13}, {15, 13}, {16, 13}, {17, 13}, {18, 13},
            {13, 12}, {14, 12}, {15, 12}, {16, 12}
        };
        return colorize(body, accent);
    }

    // ── Obstacles (Java-themed, same collision dimensions) ────────
    // Using fromBitmap for precise, readable silhouettes.

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

    /** Parse a visual bitmap into braille dot coordinates. '#' = lit dot. */
    private static double[][] fromBitmap(String... rows) {
        var pts = new ArrayList<double[]>();
        for (int i = 0; i < rows.length; i++) {
            int y = rows.length - 1 - i; // first row = top = highest y
            for (int x = 0; x < rows[i].length(); x++) {
                if (rows[i].charAt(x) == '#') {
                    pts.add(new double[]{x, y});
                }
            }
        }
        return pts.toArray(new double[0][]);
    }

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
