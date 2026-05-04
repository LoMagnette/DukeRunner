package be.lomagnette.qaly.runner;

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

    public static CharSprite dukeRunning() {
        double[][] body = fromBitmap(
            "         ####                         ",  // crown tip
            "        ######                        ",  // crown widens fast
            "       ########                       ",  // head
            "      ##########                      ",  // head wider
            "     ############                     ",  // face
            "     ##############                   ",  // face wider
            "     ##################               ",  // nose bulge right
            "    ####################              ",  // nose peak
            "     ##################               ",  // nose bulge
            "     ################                 ",  // below nose
            "    ##################                ",  // body widening
            "    ###################               ",  // body
            "   ####################               ",  // body wider
            "   #####################              ",  // body
            "   ######################             ",  // body widest
            "   ######################             ",  // body
            "   ######################             ",  // body
            "   ######################             ",  // body
            "   ######################             ",  // widest at bottom
            "    ####################              ",  // taper to feet
            "     ##################               ",  // taper
            "      ######    ######                ",  // nub feet
            "       #####    #####                 ",  // nubs
            "       ####      ####                 "   // nub tips
        );
        // Nose: fully red oval on right side (rows 5-9)
        double[][] accent = {
            {17, 18}, {18, 18},
            {17, 17}, {18, 17}, {19, 17}, {20, 17}, {21, 17}, {22, 17},
            {17, 16}, {18, 16}, {19, 16}, {20, 16}, {21, 16}, {22, 16}, {23, 16},
            {17, 15}, {18, 15}, {19, 15}, {20, 15}, {21, 15}, {22, 15},
            {19, 14}, {20, 14}
        };
        return new CharSprite(body, accent);
    }

    public static CharSprite dukeJumping() {
        double[][] body = fromBitmap(
            "         ####                         ",  // crown tip
            "        ######                        ",  // crown
            "       ########                       ",  // head
            "      ##########                      ",  // head wider
            "     ############                     ",  // face
            "     ##############                   ",  // face wider
            "     ##################               ",  // nose bulge
            "    ####################              ",  // nose peak
            "     ##################               ",  // nose bulge
            "     ################                 ",  // below nose
            "    ##################                ",  // body widening
            "    ###################               ",  // body
            "   ####################               ",  // body wider
            "   #####################              ",  // body
            "   ######################             ",  // body widest
            "   ######################             ",  // body
            "    ####################              ",  // taper
            "     ##################               ",  // compact
            "     ########  ########               ",  // tucked nubs
            "      #######  #######                "   // tucked nubs
        );
        // Nose accent (20 rows → row 5 = y14)
        double[][] accent = {
            {17, 14}, {18, 14},
            {17, 13}, {18, 13}, {19, 13}, {20, 13}, {21, 13}, {22, 13},
            {17, 12}, {18, 12}, {19, 12}, {20, 12}, {21, 12}, {22, 12}, {23, 12},
            {17, 11}, {18, 11}, {19, 11}, {20, 11}, {21, 11}, {22, 11},
            {19, 10}, {20, 10}
        };
        return new CharSprite(body, accent);
    }

    public static CharSprite dukeThrowing() {
        double[][] body = fromBitmap(
            "         ####                         ",  // crown tip
            "        ######                        ",  // crown
            "       ########                       ",  // head
            "      ##########                      ",  // head wider
            "     ############                     ",  // face
            "     ##############                   ",  // face wider
            "     ##################               ",  // nose bulge
            "    ####################              ",  // nose peak
            "     ##################               ",  // nose bulge
            "     ################                 ",  // below nose
            "    ##################                ",  // body widening
            "    ###################               ",  // body
            "   ####################  ###          ",  // body + arm start
            "   #####################  ####        ",  // body + arm extending
            "   ######################  ####       ",  // body + hand
            "   ######################             ",  // body
            "   ######################             ",  // body
            "   ######################             ",  // body
            "   ######################             ",  // widest at bottom
            "    ####################              ",  // taper to feet
            "     ##################               ",  // taper
            "      ######    ######                ",  // nub feet
            "       #####    #####                 ",  // nubs
            "       ####      ####                 "   // nub tips
        );
        // Same nose as running (24 rows)
        double[][] accent = {
            {17, 18}, {18, 18},
            {17, 17}, {18, 17}, {19, 17}, {20, 17}, {21, 17}, {22, 17},
            {17, 16}, {18, 16}, {19, 16}, {20, 16}, {21, 16}, {22, 16}, {23, 16},
            {17, 15}, {18, 15}, {19, 15}, {20, 15}, {21, 15}, {22, 15},
            {19, 14}, {20, 14}
        };
        return new CharSprite(body, accent);
    }

    // Title screen: front-facing Duke (~36w × 24h)
    public static CharSprite dukeSitting() {
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
    public static double[][] conferenceStage() {
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
    public static double[][] laptopStackWide() {
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
    public static double[][] laptopStackTall() {
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
    public static double[][] coffeeSpill() {
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
    public static double[][] confusedIntern() {
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
    public static double[][] slowBuildServer() {
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
    public static double[][] coffeeCup() {
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
    public static double[][] terminal() {
        var pts = new ArrayList<double[]>();
        // > character
        dot(pts, 0, 3); dot(pts, 1, 2); dot(pts, 2, 1); dot(pts, 1, 0);
        // _ underscore cursor
        dot(pts, 3, 0); dot(pts, 4, 0);
        return toArray(pts);
    }

    // Git branch icon (~5w × 5h)
    public static double[][] gitBranch() {
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
    public static double[][] ideIcon() {
        var pts = new ArrayList<double[]>();
        // Window frame
        fillRect(pts, 0, 0, 4, 4);
        // Title bar highlight
        dot(pts, 0, 4); dot(pts, 1, 4); dot(pts, 2, 4);
        // Code lines inside (gaps make it look like code)
        return toArray(pts);
    }

    // Docker whale shape (~5w × 4h)
    public static double[][] dockerWhale() {
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
    public static double[][] cloud() {
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
