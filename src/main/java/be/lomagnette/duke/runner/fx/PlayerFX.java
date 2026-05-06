package be.lomagnette.duke.runner.fx;

public class PlayerFX {
    public static final float X = 18.0f;
    public static final float WIDTH = 18.0f;
    public static final float HEIGHT = 16.0f;
    public static final int THROW_COOLDOWN_TICKS = 60;

    public float y;
    public float verticalVelocity;
    public boolean grounded;
    public int throwCooldownTicks;

    public PlayerFX() {
        this.y = PhysicsFX.GROUND_Y;
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
