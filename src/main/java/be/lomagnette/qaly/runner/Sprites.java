package be.lomagnette.qaly.runner;

import java.util.ArrayList;
import java.util.List;

public final class Sprites {

    private Sprites() {}

    // ── Dog sprite data ──────────────────────────────────────────
    // Body (white) + tongue accent (red), rendered as separate layers.

    public record DogSprite(double[][] body, double[][] tongue) {}

    // ── Dog sprites (braille bitmaps, ~36w × 24h) ────────────────
    // '#' = lit dot. First row = top (highest y). Facing right.
    // ~36 dots wide × 24 dots tall → 18 terminal cols × 6 terminal rows

    public static DogSprite dogRunning() {
        double[][] body = fromBitmap(
            "              ######                  ",
            "             #########                ",
            "            ############              ",
            "           ##############             ",
            "           ### ##########             ",
            "           ##############             ",
            "           ################           ",
            "           ##################         ",
            "   #####  ####################        ",
            "   ###### ######################      ",
            "    ##### ######################      ",
            "     #### ########################    ",
            "      ### ########################    ",
            "       ############################   ",
            "       ############################   ",
            "        ##########################    ",
            "         ########################     ",
            "          ######################      ",
            "           ##########    ##########   ",
            "           ##########    ##########   ",
            "          ###########    ###########  ",
            "          ###########    ###########  ",
            "         ############    ############ ",
            "         ############    ############ "
        );
        double[][] tongue = { {25, 19}, {25, 18} };
        return new DogSprite(body, tongue);
    }

    public static DogSprite dogJumping() {
        double[][] body = fromBitmap(
            "              ######                  ",
            "             #########                ",
            "            ############              ",
            "           ##############             ",
            "           ### ##########             ",
            "           ##############             ",
            "           ################           ",
            "           ##################         ",
            "   #####  ####################        ",
            "   ###### ######################      ",
            "    ##### ######################      ",
            "     #### ########################    ",
            "      ### ########################    ",
            "       ############################   ",
            "       ############################   ",
            "        ##########################    ",
            "       ########  ##########  ######## ",
            "      ########    ########    ####### ",
            "     #########    ########    ########",
            "     #########    ########    ########"
        );
        double[][] tongue = { {25, 15}, {25, 14} };
        return new DogSprite(body, tongue);
    }

    public static DogSprite dogBarking() {
        double[][] body = fromBitmap(
            "              ######                  ",
            "             #########                ",
            "            ############              ",
            "           ##############             ",
            "           ### ##########             ",
            "           ## #########  ##           ",
            "           ###           ##           ",
            "           ##############             ",
            "           ################           ",
            "           ##################         ",
            "   #####  ####################        ",
            "   ###### ######################      ",
            "    ##### ######################      ",
            "     #### ########################    ",
            "      ### ########################    ",
            "       ############################   ",
            "       ############################   ",
            "        ##########################    ",
            "         ########################     ",
            "          ######################      ",
            "           ##########    ##########   ",
            "           ##########    ##########   ",
            "          ###########    ###########  ",
            "          ###########    ###########  ",
            "         ############    ############ ",
            "         ############    ############ "
        );
        double[][] tongue = { {17, 18}, {18, 18}, {19, 18}, {20, 18} };
        return new DogSprite(body, tongue);
    }

    // Title screen: sitting Bouvier, front-facing (~33w × 24h)
    public static DogSprite dogSitting() {
        double[][] body = fromBitmap(
            "       #######        #######         ",
            "      #########      #########        ",
            "     ###########    ###########        ",
            "    #################################  ",
            "    ####  ############  ##########     ",
            "    #################################  ",
            "     ############  ############        ",
            "     ###############################   ",
            "      #############################    ",
            "       ###########################     ",
            "      #############################    ",
            "     ###############################   ",
            "     ###############################   ",
            "     ###############################   ",
            "      #############################    ",
            "       ###########################     ",
            "        #########################      ",
            "          ##########    ##########     ",
            "         ###########    ###########    ",
            "        ############    ############   ",
            "        ############    ############   ",
            "       #############    #############  ",
            "       #############    #############  ",
            "      ## ###########    ## ###########  "
        );
        double[][] tongue = {};
        return new DogSprite(body, tongue);
    }

    // ── Obstacles (braille dots) ──────────────────────────────────

    public static double[][] fence() {
        var pts = new ArrayList<double[]>();
        fillRect(pts, 0, 0, 1, 19);
        fillRect(pts, 3, 0, 4, 19);
        fillRect(pts, 0, 17, 4, 18);
        fillRect(pts, 0, 10, 4, 11);
        return toArray(pts);
    }

    public static double[][] hayBaleWide() {
        var pts = new ArrayList<double[]>();
        fillRect(pts, 1, 0, 12, 9);
        fillRect(pts, 0, 1, 13, 8);
        for (int x = 2; x <= 11; x += 3) {
            dot(pts, x, 4); dot(pts, x, 5);
        }
        return toArray(pts);
    }

    public static double[][] hayBaleTall() {
        var pts = new ArrayList<double[]>();
        fillRect(pts, 1, 0, 4, 17);
        fillRect(pts, 0, 1, 5, 16);
        return toArray(pts);
    }

    public static double[][] puddle() {
        var pts = new ArrayList<double[]>();
        fillRect(pts, 1, 0, 14, 1);
        fillRect(pts, 0, 1, 15, 2);
        for (int x = 2; x <= 13; x += 2) {
            dot(pts, x, 2);
        }
        return toArray(pts);
    }

    public static double[][] sheep() {
        var pts = new ArrayList<double[]>();
        fillRect(pts, 1, 4, 8, 9);
        fillRect(pts, 0, 5, 9, 8);
        dot(pts, 0, 9); dot(pts, 9, 9);
        fillRect(pts, 9, 5, 11, 8);
        dot(pts, 11, 7);
        fillRect(pts, 2, 0, 3, 4);
        fillRect(pts, 6, 0, 7, 4);
        return toArray(pts);
    }

    public static double[][] chicken() {
        var pts = new ArrayList<double[]>();
        fillRect(pts, 1, 2, 3, 5);
        fillRect(pts, 3, 4, 5, 6);
        dot(pts, 5, 5);
        dot(pts, 0, 5); dot(pts, 0, 4);
        fillRect(pts, 1, 0, 1, 2);
        fillRect(pts, 3, 0, 3, 2);
        dot(pts, 4, 6);
        return toArray(pts);
    }

    // ── Decorations (braille dots) ────────────────────────────────

    public static double[][] flower() {
        var pts = new ArrayList<double[]>();
        dot(pts, 1, 0); dot(pts, 1, 1); dot(pts, 1, 2);
        dot(pts, 0, 3); dot(pts, 1, 3); dot(pts, 2, 3); dot(pts, 1, 4);
        return toArray(pts);
    }

    public static double[][] stone() {
        var pts = new ArrayList<double[]>();
        dot(pts, 0, 0); dot(pts, 1, 0); dot(pts, 2, 0);
        dot(pts, 0, 1); dot(pts, 1, 1);
        return toArray(pts);
    }

    public static double[][] grassTuft() {
        var pts = new ArrayList<double[]>();
        dot(pts, 0, 0); dot(pts, 1, 1); dot(pts, 2, 0); dot(pts, 1, 2);
        return toArray(pts);
    }

    public static double[][] sun() {
        var pts = new ArrayList<double[]>();
        dot(pts, 1, 0); dot(pts, 0, 1); dot(pts, 1, 1);
        dot(pts, 2, 1); dot(pts, 1, 2);
        return toArray(pts);
    }

    public static double[][] leaf() {
        var pts = new ArrayList<double[]>();
        dot(pts, 0, 0); dot(pts, 1, 0); dot(pts, 1, 1); dot(pts, 2, 1);
        return toArray(pts);
    }

    public static double[][] snowflake() {
        var pts = new ArrayList<double[]>();
        dot(pts, 1, 0); dot(pts, 0, 1); dot(pts, 1, 1);
        dot(pts, 2, 1); dot(pts, 1, 2);
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
