package be.lomagnette.duke.runner.tui;

public class Player {
    public static final float X = 18.0f;
    public static final float WIDTH = 18.0f;
    static final float HEIGHT = 16.0f;
    public static final int THROW_COOLDOWN_TICKS = 60; // ~1 second at the 16ms (~60fps) tick rate

    float y;
    float verticalVelocity;
    boolean grounded;
    public int throwCooldownTicks;

    public Player() {
        this.y = Physics.GROUND_Y;
        this.verticalVelocity = 0;
        this.grounded = true;
        this.throwCooldownTicks = 0;
    }

    public boolean canThrow() {
        return throwCooldownTicks == 0;
    }

    public void performThrow() {
        throwCooldownTicks = THROW_COOLDOWN_TICKS;
    }

    public void tickCooldown() {
        if (throwCooldownTicks > 0) throwCooldownTicks--;
    }
}
