package be.lomagnette.duke.runner.fx;

public final class PhysicsFX {
    public static final float GROUND_Y = 8.0f;
    public static final float GRAVITY = 0.5f;
    public static final float JUMP_VELOCITY = 7.0f;
    public static final float THROW_RANGE = 40.0f;

    private PhysicsFX() {}

    public static void applyGravity(PlayerFX p) {
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

    public static void jump(PlayerFX p) {
        if (p.grounded) {
            p.verticalVelocity = JUMP_VELOCITY;
            p.grounded = false;
        }
    }

    public static boolean collides(PlayerFX p, ObstacleFX o) {
        float px = PlayerFX.X;
        float py = p.y;
        float pw = PlayerFX.WIDTH;
        float ph = PlayerFX.HEIGHT;

        float ox = o.x();
        float oy = o.bottomY();
        float ow = o.width();
        float oh = o.height();

        return px < ox + ow && px + pw > ox && py < oy + oh && py + ph > oy;
    }

    public static boolean inThrowRange(PlayerFX p, ObstacleFX o) {
        float playerRight = PlayerFX.X + PlayerFX.WIDTH;
        float obstacleLeft = o.x();

        boolean inHorizontalRange = obstacleLeft > PlayerFX.X
                && obstacleLeft < playerRight + THROW_RANGE;

        boolean inVerticalRange = p.y < o.bottomY() + o.height()
                && p.y + PlayerFX.HEIGHT > o.bottomY();

        return o.throwable() && inHorizontalRange && inVerticalRange;
    }
}
