package be.lomagnette.qaly.runner.tui;

public sealed abstract class Obstacle
        permits Obstacle.ConferenceStage, Obstacle.LaptopStack, Obstacle.CoffeeSpill, Obstacle.ConfusedIntern, Obstacle.SlowBuildServer {

    protected float x;

    Obstacle(float x) {
        this.x = x;
    }

    public float x() { return x; }
    public void scroll(float speed) { x -= speed; }

    public abstract float bottomY();
    public abstract float width();
    public abstract float height();
    public abstract boolean throwable();
    public abstract ObstacleType type();

    public enum ObstacleType { CONFERENCE_STAGE, LAPTOP_STACK_WIDE, LAPTOP_STACK_TALL, COFFEE_SPILL, CONFUSED_INTERN, SLOW_BUILD_SERVER }

    public static final class ConferenceStage extends Obstacle {
        public ConferenceStage(float x) { super(x); }
        public float bottomY() { return Physics.GROUND_Y; }
        public float width() { return 6; }
        public float height() { return 26; }
        public boolean throwable() { return false; }
        public ObstacleType type() { return ObstacleType.CONFERENCE_STAGE; }
    }

    public static final class LaptopStack extends Obstacle {
        private final boolean wide;
        public LaptopStack(float x, boolean wide) { super(x); this.wide = wide; }
        public boolean isWide() { return wide; }
        public float bottomY() { return Physics.GROUND_Y; }
        public float width() { return wide ? 18 : 8; }
        public float height() { return wide ? 9 : 25; }
        public boolean throwable() { return false; }
        public ObstacleType type() { return wide ? ObstacleType.LAPTOP_STACK_WIDE : ObstacleType.LAPTOP_STACK_TALL; }
    }

    public static final class CoffeeSpill extends Obstacle {
        public CoffeeSpill(float x) { super(x); }
        public float bottomY() { return Physics.GROUND_Y - 1; }
        public float width() { return 22; }
        public float height() { return 6; }
        public boolean throwable() { return false; }
        public ObstacleType type() { return ObstacleType.COFFEE_SPILL; }
    }

    public static final class ConfusedIntern extends Obstacle {
        public ConfusedIntern(float x) { super(x); }
        public float bottomY() { return Physics.GROUND_Y; }
        public float width() { return 14; }
        public float height() { return 14; }
        public boolean throwable() { return true; }
        public ObstacleType type() { return ObstacleType.CONFUSED_INTERN; }
    }

    public static final class SlowBuildServer extends Obstacle {
        public SlowBuildServer(float x) { super(x); }
        public float bottomY() { return Physics.GROUND_Y; }
        public float width() { return 7; }
        public float height() { return 10; }
        public boolean throwable() { return true; }
        public ObstacleType type() { return ObstacleType.SLOW_BUILD_SERVER; }
    }
}
