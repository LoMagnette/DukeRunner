package be.lomagnette.duke.runner.tui;

import java.util.ArrayList;
import java.util.List;

public final class Sprites {

    private Sprites() {}

    // ── Character sprite data ─────────────────────────────────────
    // Body (white) + accent (red nose), rendered as separate layers.

    public record CharSprite(double[][] body, double[][] accent) {}

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

    // Obstacle + decoration silhouettes, likewise parsed once.
    private static final double[][] CONFERENCE_STAGE = buildConferenceStage();
    private static final double[][] LAPTOP_STACK_WIDE = buildLaptopStackWide();
    private static final double[][] LAPTOP_STACK_TALL = buildLaptopStackTall();
    private static final double[][] COFFEE_SPILL = buildCoffeeSpill();
    private static final double[][] CONFUSED_INTERN = buildConfusedIntern();
    private static final double[][] SLOW_BUILD_SERVER = buildSlowBuildServer();
    private static final double[][] COFFEE_CUP = buildCoffeeCup();
    private static final double[][] TERMINAL = buildTerminal();
    private static final double[][] GIT_BRANCH = buildGitBranch();
    private static final double[][] IDE_ICON = buildIdeIcon();
    private static final double[][] DOCKER_WHALE = buildDockerWhale();
    private static final double[][] CLOUD = buildCloud();

    public static double[][] conferenceStage() { return CONFERENCE_STAGE; }
    public static double[][] laptopStackWide() { return LAPTOP_STACK_WIDE; }
    public static double[][] laptopStackTall() { return LAPTOP_STACK_TALL; }
    public static double[][] coffeeSpill() { return COFFEE_SPILL; }
    public static double[][] confusedIntern() { return CONFUSED_INTERN; }
    public static double[][] slowBuildServer() { return SLOW_BUILD_SERVER; }
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
        return new CharSprite(body, accent);
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
        return new CharSprite(body, accent);
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
        return new CharSprite(body, accent);
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
        return new CharSprite(body, accent);
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
        return new CharSprite(body, accent);
    }

    // ── Obstacles (Java-themed, same collision dimensions) ────────
    // Using fromBitmap for precise, readable silhouettes.

    // Lectern/podium (6w × 26h)
    private static double[][] buildConferenceStage() {
        return fromBitmap(
            " #### ",  // podium top surface
            "######",  // lip
            "######",
            " #### ",  // front panel
            " #### ",
            " #### ",
            " #### ",
            " #### ",
            " #### ",
            " #### ",
            " #### ",
            " #### ",
            " #### ",
            " #### ",
            " #### ",
            " #### ",  // panel bottom
            "  ##  ",  // stem
            "  ##  ",
            "  ##  ",
            "  ##  ",
            "  ##  ",
            " #### ",  // base widens
            " #### ",
            "######",  // base
            "######",
            "######"   // base bottom
        );
    }

    // Three monitors on a desk (18w × 9h)
    private static double[][] buildLaptopStackWide() {
        return fromBitmap(
            "  ##    ##    ##  ",  // screen tops
            " ####  ####  #### ",  // screens
            " ####  ####  #### ",
            " ####  ####  #### ",  // screen bottoms
            "##################",  // desk surface
            "##################",
            "##################",
            "##################",
            "##################"   // base
        );
    }

    // Three laptops stacked vertically (8w × 25h)
    private static double[][] buildLaptopStackTall() {
        return fromBitmap(
            "  ####  ",  // top laptop screen
            " ###### ",
            " ###### ",
            " ###### ",
            " ###### ",
            "########",  // keyboard
            "########",
            "        ",  // gap
            "        ",
            "  ####  ",  // middle laptop screen
            " ###### ",
            " ###### ",
            " ###### ",
            " ###### ",
            "########",  // keyboard
            "########",
            "        ",  // gap
            "        ",
            "  ####  ",  // bottom laptop screen
            " ###### ",
            " ###### ",
            " ###### ",
            " ###### ",
            "########",  // keyboard
            "########"   // base
        );
    }

    // Puddle on ground (22w × 6h)
    private static double[][] buildCoffeeSpill() {
        return fromBitmap(
            "  ##################  ",  // top edge
            " #################### ",  // wider
            "######################",  // widest
            "######################",  // widest
            " #################### ",  // narrowing
            "   ################   "   // base
        );
    }

    // Person silhouette with arms out (14w × 14h)
    private static double[][] buildConfusedIntern() {
        return fromBitmap(
            "     ####     ",  // head top
            "    ######    ",  // head
            "    ######    ",  // head
            "     ####     ",  // chin
            "      ##      ",  // neck
            "  ##########  ",  // shoulders
            "##############",  // arms extended wide
            "##############",  // arms + torso
            "  ##########  ",  // torso
            "    ######    ",  // waist
            "    ######    ",  // hips
            "    ##  ##    ",  // legs
            "    ##  ##    ",  // legs
            "   ###  ###   "   // feet
        );
    }

    // Server rack box (7w × 10h)
    private static double[][] buildSlowBuildServer() {
        return fromBitmap(
            "#######",  // top frame
            "# ### #",  // drive bay
            "#     #",  // empty slot
            "# ### #",  // drive bay
            "#     #",  // empty slot
            "# ### #",  // drive bay
            "#     #",  // empty slot
            "# ### #",  // drive bay
            "#######",  // bottom frame
            "#######"   // base
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
