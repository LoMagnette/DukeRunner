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

    // Podium/stage structure (6w × 26h)
    public static double[][] conferenceStage() {
        var pts = new ArrayList<double[]>();
        fillRect(pts, 0, 0, 5, 2);     // base platform
        fillRect(pts, 1, 0, 4, 18);    // podium body
        fillRect(pts, 0, 18, 5, 20);   // podium top
        fillRect(pts, 2, 20, 3, 25);   // microphone stand
        dot(pts, 1, 25); dot(pts, 4, 25); // mic top
        return toArray(pts);
    }

    // Wide row of laptops (18w × 9h)
    public static double[][] laptopStackWide() {
        var pts = new ArrayList<double[]>();
        fillRect(pts, 0, 0, 17, 1);    // base
        fillRect(pts, 1, 2, 5, 7);     // laptop 1 screen
        fillRect(pts, 0, 1, 6, 2);     // laptop 1 keyboard
        fillRect(pts, 7, 2, 11, 7);    // laptop 2 screen
        fillRect(pts, 6, 1, 12, 2);    // laptop 2 keyboard
        fillRect(pts, 13, 2, 17, 7);   // laptop 3 screen
        fillRect(pts, 12, 1, 17, 2);   // laptop 3 keyboard
        dot(pts, 3, 8); dot(pts, 9, 8); dot(pts, 15, 8); // screen glints
        return toArray(pts);
    }

    // Vertically stacked laptops (8w × 25h)
    public static double[][] laptopStackTall() {
        var pts = new ArrayList<double[]>();
        // Bottom laptop
        fillRect(pts, 0, 0, 7, 1);     // keyboard
        fillRect(pts, 1, 2, 6, 7);     // screen
        // Middle laptop
        fillRect(pts, 0, 8, 7, 9);     // keyboard
        fillRect(pts, 1, 10, 6, 15);   // screen
        // Top laptop
        fillRect(pts, 0, 16, 7, 17);   // keyboard
        fillRect(pts, 1, 18, 6, 23);   // screen
        dot(pts, 3, 24);               // top glint
        return toArray(pts);
    }

    // Coffee spill on ground (22w × 6h)
    public static double[][] coffeeSpill() {
        var pts = new ArrayList<double[]>();
        fillRect(pts, 3, 0, 18, 1);    // thin base
        fillRect(pts, 1, 1, 20, 3);    // main spill
        fillRect(pts, 0, 2, 21, 4);    // widest spread
        // Splatter drops
        dot(pts, 0, 0); dot(pts, 21, 0);
        dot(pts, 2, 5); dot(pts, 8, 5); dot(pts, 14, 5); dot(pts, 19, 5);
        return toArray(pts);
    }

    // Person silhouette - confused intern (14w × 14h)
    public static double[][] confusedIntern() {
        var pts = new ArrayList<double[]>();
        // Head (round)
        fillRect(pts, 5, 10, 8, 13);
        dot(pts, 4, 11); dot(pts, 9, 11);
        dot(pts, 4, 12); dot(pts, 9, 12);
        // Neck
        dot(pts, 6, 9); dot(pts, 7, 9);
        // Body (torso)
        fillRect(pts, 3, 4, 10, 8);
        fillRect(pts, 4, 3, 9, 9);
        // Arms out (confused gesture)
        fillRect(pts, 0, 6, 3, 8);     // left arm
        fillRect(pts, 10, 6, 13, 8);   // right arm
        // Question mark above head
        dot(pts, 7, 14);
        // Legs
        fillRect(pts, 4, 0, 5, 3);
        fillRect(pts, 8, 0, 9, 3);
        return toArray(pts);
    }

    // Server/computer box (7w × 10h)
    public static double[][] slowBuildServer() {
        var pts = new ArrayList<double[]>();
        // Server box
        fillRect(pts, 0, 0, 6, 9);
        // Drive bays (gaps)
        dot(pts, 1, 8); dot(pts, 2, 8); dot(pts, 3, 8); // LED row top
        dot(pts, 1, 5); dot(pts, 2, 5); dot(pts, 3, 5); // LED row mid
        dot(pts, 1, 2); dot(pts, 2, 2); dot(pts, 3, 2); // LED row bottom
        // Ventilation slots
        dot(pts, 5, 7); dot(pts, 5, 4); dot(pts, 5, 1);
        return toArray(pts);
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
