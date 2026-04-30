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
            "               # ###                  ",  // shaggy ear tufts
            "              ########                 ",  // ear + head top
            "             ##########                ",  // head (round)
            "            ############               ",  // head wider
            "            ## #########               ",  // eye + face
            "            #############              ",  // face / nose
            "           ################            ",  // beard (wider!)
            "           ##################          ",  // beard → body
            "   # ###  #####################        ",  // bushy tail + body
            "   ######  ######################      ",  // tail + body
            "    #####  #######################     ",  // body
            "     ####  ########################    ",  // body
            "      #### #########################   ",  // body
            "       ############################    ",  // body widest
            "       # ##########################    ",  // fur tuft on back
            "        ###########################    ",  // body
            "         #########################     ",  // body tapering
            "          ######################       ",  // body narrowing
            "           #########    ##########     ",  // upper legs
            "           ########      #########     ",  // knee narrows
            "          #########      ##########    ",  // legs widen
            "          #########      ##########    ",  // legs
            "         ##########      ###########   ",  // paws (feathered)
            "         ## #######      ## ########   "   // paw toes
        );
        double[][] tongue = { {24, 19}, {24, 18} };
        return new DogSprite(body, tongue);
    }

    public static DogSprite dogJumping() {
        double[][] body = fromBitmap(
            "               # ###                  ",  // shaggy ear tufts
            "              ########                 ",  // ear + head top
            "             ##########                ",  // head
            "            ############               ",  // head wider
            "            ## #########               ",  // eye + face
            "            #############              ",  // face / nose
            "           ################            ",  // beard
            "           ##################          ",  // beard → body
            "   # ###  #####################        ",  // bushy tail + body
            "   ######  ######################      ",  // tail + body
            "    #####  #######################     ",  // body
            "     ####  ########################    ",  // body
            "      #### #########################   ",  // body
            "       ############################    ",  // body widest
            "       # ##########################    ",  // fur tuft
            "        ###########################    ",  // body
            "       ########  ##########  ########  ",  // tucked legs
            "      ########    ########    ######## ",  // tucked mid
            "     #########    ########    #########",  // tucked paws
            "     ## ######    ## #####    ## ######"   // paw toes tucked
        );
        double[][] tongue = { {24, 15}, {24, 14} };
        return new DogSprite(body, tongue);
    }

    public static DogSprite dogBarking() {
        double[][] body = fromBitmap(
            "               # ###                  ",  // shaggy ear tufts
            "              ########                 ",  // ear + head top
            "             ##########                ",  // head
            "            ############               ",  // head wider
            "            ## #########               ",  // eye + face
            "            ## ########  ##            ",  // eye + mouth open top
            "            ###          ##            ",  // beard + mouth bottom
            "            #############              ",  // jaw closes
            "           ################            ",  // beard
            "           ##################          ",  // beard → body
            "   # ###  #####################        ",  // bushy tail + body
            "   #######  #####################      ",  // tail raised + body
            "    #####  #######################     ",  // body
            "     ####  ########################    ",  // body
            "      #### #########################   ",  // body
            "       ############################    ",  // body widest
            "       # ##########################    ",  // fur tuft
            "        ###########################    ",  // body
            "         #########################     ",  // body tapering
            "          ######################       ",  // body narrowing
            "           #########    ##########     ",  // upper legs
            "           ########      #########     ",  // knee narrows
            "          #########      ##########    ",  // legs widen
            "          #########      ##########    ",  // legs
            "         ##########      ###########   ",  // paws
            "         ## #######      ## ########   "   // paw toes
        );
        double[][] tongue = { {17, 19}, {18, 19}, {19, 19}, {20, 19} };
        return new DogSprite(body, tongue);
    }

    // Title screen: sitting Bouvier, front-facing (~34w × 24h)
    public static DogSprite dogSitting() {
        double[][] body = fromBitmap(
            "      # #####          # #####         ",  // shaggy ear tufts
            "      ########        ########         ",  // ears
            "     ##########      ##########         ",  // ears wider
            "    ##################################  ",  // head top
            "    ####  ############  ##########      ",  // eyes
            "    ##################################  ",  // head
            "     #############  ############        ",  // nose / snout
            "     ################################   ",  // beard
            "      ##############################    ",  // chin
            "       ############################     ",  // neck
            "      ##############################    ",  // body
            "     ################################   ",  // body wider
            "     ################################   ",  // body
            "     ################################   ",  // body
            "      ##############################    ",  // body
            "       ############################     ",  // body tapering
            "        ##########################      ",  // lower body
            "          ##########    ##########      ",  // upper legs
            "         ###########    ###########     ",  // mid legs
            "        ############    ############    ",  // legs
            "        ############    ############    ",  // legs
            "       #############    #############   ",  // lower legs
            "       #############    #############   ",  // paws
            "      ## ###########    ## ###########  "   // paw toes
        );
        double[][] tongue = {};
        return new DogSprite(body, tongue);
    }

    // ── Obstacles (braille dots, scaled ~1.3x) ─────────────────────

    public static double[][] fence() {
        var pts = new ArrayList<double[]>();
        fillRect(pts, 0, 0, 1, 25);    // left post
        fillRect(pts, 4, 0, 5, 25);    // right post
        fillRect(pts, 0, 22, 5, 24);   // upper rail
        fillRect(pts, 0, 12, 5, 14);   // lower rail
        return toArray(pts);
    }

    public static double[][] hayBaleWide() {
        var pts = new ArrayList<double[]>();
        fillRect(pts, 2, 0, 15, 8);    // core
        fillRect(pts, 1, 1, 16, 7);    // wider middle
        fillRect(pts, 0, 2, 17, 6);    // widest
        for (int x = 3; x <= 14; x += 4) {
            dot(pts, x, 4); dot(pts, x, 5);
        }
        return toArray(pts);
    }

    public static double[][] hayBaleTall() {
        var pts = new ArrayList<double[]>();
        fillRect(pts, 2, 0, 5, 24);    // core
        fillRect(pts, 1, 1, 6, 23);    // wider middle
        fillRect(pts, 0, 2, 7, 22);    // widest
        return toArray(pts);
    }

    public static double[][] puddle() {
        var pts = new ArrayList<double[]>();
        fillRect(pts, 2, 0, 19, 2);    // base
        fillRect(pts, 1, 1, 20, 3);    // middle
        fillRect(pts, 0, 2, 21, 4);    // widest
        for (int x = 3; x <= 18; x += 3) {
            dot(pts, x, 5);            // ripple tops
        }
        return toArray(pts);
    }

    public static double[][] sheep() {
        var pts = new ArrayList<double[]>();
        // Fluffy body (oval)
        fillRect(pts, 2, 5, 10, 12);
        fillRect(pts, 1, 6, 11, 11);
        fillRect(pts, 0, 7, 12, 10);
        // Fluffy tufts
        dot(pts, 0, 12); dot(pts, 12, 12);
        dot(pts, 0, 5); dot(pts, 12, 5);
        // Head
        fillRect(pts, 12, 7, 14, 11);
        dot(pts, 14, 10);              // ear
        // Legs
        fillRect(pts, 3, 0, 4, 5);
        fillRect(pts, 8, 0, 9, 5);
        return toArray(pts);
    }

    public static double[][] chicken() {
        var pts = new ArrayList<double[]>();
        // Body
        fillRect(pts, 1, 3, 4, 7);
        fillRect(pts, 4, 5, 6, 8);
        // Tail
        dot(pts, 0, 6); dot(pts, 0, 7);
        // Head + comb
        dot(pts, 6, 8); dot(pts, 6, 9);
        dot(pts, 5, 9);
        // Beak
        dot(pts, 7, 8);
        // Legs
        fillRect(pts, 2, 0, 2, 3);
        fillRect(pts, 4, 0, 4, 3);
        return toArray(pts);
    }

    // ── Decorations (braille dots, scaled ~1.5x) ─────────────────

    public static double[][] coffeeCup() {
        var pts = new ArrayList<double[]>();
        // Stem
        dot(pts, 2, 0); dot(pts, 2, 1); dot(pts, 2, 2); dot(pts, 2, 3);
        // Petals
        dot(pts, 1, 4); dot(pts, 2, 4); dot(pts, 3, 4);
        dot(pts, 0, 5); dot(pts, 1, 5); dot(pts, 2, 5); dot(pts, 3, 5); dot(pts, 4, 5);
        dot(pts, 1, 6); dot(pts, 2, 6); dot(pts, 3, 6);
        dot(pts, 2, 7);
        return toArray(pts);
    }

    public static double[][] terminal() {
        var pts = new ArrayList<double[]>();
        dot(pts, 1, 0); dot(pts, 2, 0); dot(pts, 3, 0);
        dot(pts, 0, 1); dot(pts, 1, 1); dot(pts, 2, 1); dot(pts, 3, 1); dot(pts, 4, 1);
        dot(pts, 0, 2); dot(pts, 1, 2); dot(pts, 2, 2); dot(pts, 3, 2);
        dot(pts, 1, 3); dot(pts, 2, 3);
        return toArray(pts);
    }

    public static double[][] gitBranch() {
        var pts = new ArrayList<double[]>();
        dot(pts, 0, 0); dot(pts, 2, 0); dot(pts, 4, 0);
        dot(pts, 1, 1); dot(pts, 3, 1);
        dot(pts, 1, 2); dot(pts, 2, 3); dot(pts, 3, 2);
        dot(pts, 2, 4);
        return toArray(pts);
    }

    public static double[][] ideIcon() {
        var pts = new ArrayList<double[]>();
        dot(pts, 2, 0);
        dot(pts, 0, 1); dot(pts, 2, 1); dot(pts, 4, 1);
        dot(pts, 1, 2); dot(pts, 2, 2); dot(pts, 3, 2);
        dot(pts, 0, 3); dot(pts, 2, 3); dot(pts, 4, 3);
        dot(pts, 2, 4);
        return toArray(pts);
    }

    public static double[][] dockerWhale() {
        var pts = new ArrayList<double[]>();
        dot(pts, 0, 0); dot(pts, 1, 0);
        dot(pts, 1, 1); dot(pts, 2, 1); dot(pts, 3, 1);
        dot(pts, 2, 2); dot(pts, 3, 2); dot(pts, 4, 2);
        dot(pts, 3, 3); dot(pts, 4, 3);
        return toArray(pts);
    }

    public static double[][] cloud() {
        var pts = new ArrayList<double[]>();
        dot(pts, 2, 0);
        dot(pts, 0, 1); dot(pts, 2, 1); dot(pts, 4, 1);
        dot(pts, 1, 2); dot(pts, 2, 2); dot(pts, 3, 2);
        dot(pts, 0, 3); dot(pts, 2, 3); dot(pts, 4, 3);
        dot(pts, 2, 4);
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
