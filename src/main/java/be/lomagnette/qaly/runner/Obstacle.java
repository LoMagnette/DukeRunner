package be.lomagnette.qaly.runner;

public sealed abstract class Obstacle
        permits Obstacle.Fence, Obstacle.HayBale, Obstacle.Puddle, Obstacle.Sheep, Obstacle.Chicken {

    protected float x;

    Obstacle(float x) {
        this.x = x;
    }

    public float x() { return x; }
    public void scroll(float speed) { x -= speed; }

    public abstract float bottomY();
    public abstract float width();
    public abstract float height();
    public abstract boolean barkable();
    public abstract ObstacleType type();

    public enum ObstacleType { FENCE, HAY_BALE_WIDE, HAY_BALE_TALL, PUDDLE, SHEEP, CHICKEN }

    public static final class Fence extends Obstacle {
        public Fence(float x) { super(x); }
        public float bottomY() { return Physics.GROUND_Y; }
        public float width() { return 6; }
        public float height() { return 26; }
        public boolean barkable() { return false; }
        public ObstacleType type() { return ObstacleType.FENCE; }
    }

    public static final class HayBale extends Obstacle {
        private final boolean wide;
        public HayBale(float x, boolean wide) { super(x); this.wide = wide; }
        public boolean isWide() { return wide; }
        public float bottomY() { return Physics.GROUND_Y; }
        public float width() { return wide ? 18 : 8; }
        public float height() { return wide ? 9 : 25; }
        public boolean barkable() { return false; }
        public ObstacleType type() { return wide ? ObstacleType.HAY_BALE_WIDE : ObstacleType.HAY_BALE_TALL; }
    }

    public static final class Puddle extends Obstacle {
        public Puddle(float x) { super(x); }
        public float bottomY() { return Physics.GROUND_Y - 1; }
        public float width() { return 22; }
        public float height() { return 6; }
        public boolean barkable() { return false; }
        public ObstacleType type() { return ObstacleType.PUDDLE; }
    }

    public static final class Sheep extends Obstacle {
        public Sheep(float x) { super(x); }
        public float bottomY() { return Physics.GROUND_Y; }
        public float width() { return 14; }
        public float height() { return 14; }
        public boolean barkable() { return true; }
        public ObstacleType type() { return ObstacleType.SHEEP; }
    }

    public static final class Chicken extends Obstacle {
        public Chicken(float x) { super(x); }
        public float bottomY() { return Physics.GROUND_Y; }
        public float width() { return 7; }
        public float height() { return 10; }
        public boolean barkable() { return true; }
        public ObstacleType type() { return ObstacleType.CHICKEN; }
    }
}
