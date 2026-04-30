package be.lomagnette.qaly.runner;

public final class Physics {
    static final float GROUND_Y = 8.0f;
    static final float GRAVITY = 0.18f;
    static final float JUMP_VELOCITY = 3.5f;
    static final float BARK_RANGE = 40.0f;
    static final int MAX_HOLD_TICKS = 15;

    private Physics() {}

    public static void applyGravity(Player p) {
        if (!p.grounded) {
            p.verticalVelocity -= GRAVITY;
            p.y += p.verticalVelocity;
            if (p.y <= GROUND_Y) {
                p.y = GROUND_Y;
                p.verticalVelocity = 0;
                p.grounded = true;
            }
        }
    }

    public static void jump(Player p) {
        if (p.grounded) {
            p.verticalVelocity = JUMP_VELOCITY;
            p.grounded = false;
        }
    }

    public static void checkJumpCut(Player p, boolean jumpKeyThisTick) {
        if (!p.jumpHeld) return;
        if (p.verticalVelocity <= 0) {
            p.endJumpHold();
            return;
        }
        if (!jumpKeyThisTick || p.jumpHoldTicks >= MAX_HOLD_TICKS) {
            p.verticalVelocity = 0;
            p.endJumpHold();
        }
    }

    public static boolean collides(Player p, Obstacle o) {
        float px = Player.X;
        float py = p.y;
        float pw = Player.WIDTH;
        float ph = Player.HEIGHT;

        float ox = o.x();
        float oy = o.bottomY();
        float ow = o.width();
        float oh = o.height();

        return px < ox + ow && px + pw > ox && py < oy + oh && py + ph > oy;
    }

    public static boolean inBarkRange(Player p, Obstacle o) {
        float playerRight = Player.X + Player.WIDTH;
        float obstacleLeft = o.x();

        // Horizontal: obstacle must be ahead of player and within bark range
        boolean inHorizontalRange = obstacleLeft > Player.X
                && obstacleLeft < playerRight + BARK_RANGE;

        // Vertical: player and obstacle must overlap vertically
        boolean inVerticalRange = p.y < o.bottomY() + o.height()
                && p.y + Player.HEIGHT > o.bottomY();

        return o.barkable() && inHorizontalRange && inVerticalRange;
    }
}
