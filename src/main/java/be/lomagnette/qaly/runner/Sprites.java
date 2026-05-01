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
    // Duke = Java mascot: inverted teardrop blob, big red nose,
    //   no eyes/mouth, thin noodle arms, small nub feet.

    public static CharSprite dukeRunning() {
        double[][] body = fromBitmap(
            "            ##                        ",  // crown tip
            "           ####                       ",  // crown
            "          ######                      ",  // upper head
            "         ########                     ",  // head
            "        ##########                    ",  // head wider
            "       ############                   ",  // face
            "      ##############                  ",  // face wider
            "     ################                 ",  // upper body
            "     ####################             ",  // nose bulge right
            "    ######################            ",  // nose peak
            "     ####################             ",  // nose bulge
            "     #################                ",  // below nose
            "    ####################              ",  // body
            "    #####################             ",  // body widest
            "    #####################             ",  // body
            "     ###################              ",  // body taper
            "      #################               ",  // lower body
            "       ###############                ",  // lower body
            "        #############                 ",  // narrowing
            "         ###########                  ",  // narrow
            "          #########                   ",  // above feet
            "          #### ####                   ",  // feet split
            "         ####   ####                  ",  // feet
            "         ###     ###                  "   // feet bottoms
        );
        // Nose: rounded bump on right side of face (rows 8-10)
        double[][] accent = {
            {21, 15}, {22, 15}, {23, 15}, {24, 15},
            {22, 14}, {23, 14}, {24, 14}, {25, 14},
            {21, 13}, {22, 13}, {23, 13}, {24, 13}
        };
        return new CharSprite(body, accent);
    }

    public static CharSprite dukeJumping() {
        double[][] body = fromBitmap(
            "            ##                        ",  // crown tip
            "           ####                       ",  // crown
            "          ######                      ",  // upper head
            "         ########                     ",  // head
            "        ##########                    ",  // head wider
            "       ############                   ",  // face
            "      ##############                  ",  // face wider
            "     ################                 ",  // upper body
            "     ####################             ",  // nose bulge
            "    ######################            ",  // nose peak
            "     ####################             ",  // nose bulge
            "     #################                ",  // below nose
            "    ####################              ",  // body
            "    #####################             ",  // body widest
            "    #####################             ",  // body
            "     ###################              ",  // body taper
            "      #################               ",  // lower body
            "       ###############                ",  // lower body
            "       ######  #######                ",  // tucked feet
            "      #######  ########               "   // tucked nubs
        );
        // Nose accent (20 rows → row 8 = y11)
        double[][] accent = {
            {21, 11}, {22, 11}, {23, 11}, {24, 11},
            {22, 10}, {23, 10}, {24, 10}, {25, 10},
            {21, 9}, {22, 9}, {23, 9}, {24, 9}
        };
        return new CharSprite(body, accent);
    }

    public static CharSprite dukeThrowing() {
        double[][] body = fromBitmap(
            "            ##                        ",  // crown tip
            "           ####                       ",  // crown
            "          ######                      ",  // upper head
            "         ########                     ",  // head
            "        ##########                    ",  // head wider
            "       ############                   ",  // face
            "      ##############                  ",  // face wider
            "     ################                 ",  // upper body
            "     ####################             ",  // nose bulge
            "    ######################            ",  // nose peak
            "     ####################             ",  // nose bulge
            "     #################                ",  // below nose
            "    ###################### ##         ",  // body + arm start
            "    #######################  ###      ",  // body + arm extending
            "    #####################    ###      ",  // body + hand
            "     ###################              ",  // body taper
            "      #################               ",  // lower body
            "       ###############                ",  // lower body
            "        #############                 ",  // narrowing
            "         ###########                  ",  // narrow
            "          #########                   ",  // above feet
            "          #### ####                   ",  // feet split
            "         ####   ####                  ",  // feet
            "         ###     ###                  "   // feet bottoms
        );
        // Same nose position as running (24 rows)
        double[][] accent = {
            {21, 15}, {22, 15}, {23, 15}, {24, 15},
            {22, 14}, {23, 14}, {24, 14}, {25, 14},
            {21, 13}, {22, 13}, {23, 13}, {24, 13}
        };
        return new CharSprite(body, accent);
    }

    // Title screen: front-facing Duke (~36w × 24h)
    public static CharSprite dukeSitting() {
        double[][] body = fromBitmap(
            "              ##                      ",  // crown tip
            "             ####                     ",  // crown
            "            ######                    ",  // upper head
            "           ########                   ",  // head
            "          ##########                  ",  // head wider
            "         ############                 ",  // face
            "        ##############                ",  // face wider
            "       ################               ",  // upper body
            "      ##################              ",  // body + nose area
            "      ##################              ",  // nose center
            "      ##################              ",  // body + nose area
            "     ####################             ",  // body wider
            "     ####################             ",  // body widest
            "     ####################             ",  // body
            "     ####################             ",  // body
            "      ##################              ",  // body taper
            "       ################               ",  // lower body
            "        ##############                ",  // lower body
            "         ############                 ",  // narrowing
            "          ##########                  ",  // narrow
            "          ##########                  ",  // above feet
            "          ####  ####                  ",  // feet split
            "         #####  #####                 ",  // feet
            "         ####    ####                 "   // feet bottoms
        );
        // Nose: centered oval on face (front view)
        double[][] accent = {
            {13, 16}, {14, 16}, {15, 16},
            {12, 15}, {13, 15}, {14, 15}, {15, 15}, {16, 15},
            {11, 14}, {12, 14}, {13, 14}, {14, 14}, {15, 14}, {16, 14}, {17, 14},
            {12, 13}, {13, 13}, {14, 13}, {15, 13}, {16, 13},
            {13, 12}, {14, 12}, {15, 12}
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
