package be.lomagnette.qaly.runner;

public class Player {
    static final float X = 18.0f;
    static final float WIDTH = 18.0f;
    static final float HEIGHT = 16.0f;
    static final int BARK_COOLDOWN_TICKS = 60; // ~2 seconds at 30fps

    float y;
    float verticalVelocity;
    boolean grounded;
    int barkCooldownTicks;
    boolean jumpHeld;
    int jumpHoldTicks;

    public Player() {
        this.y = Physics.GROUND_Y;
        this.verticalVelocity = 0;
        this.grounded = true;
        this.barkCooldownTicks = 0;
        this.jumpHeld = false;
        this.jumpHoldTicks = 0;
    }

    public void startJumpHold() {
        jumpHeld = true;
        jumpHoldTicks = 0;
    }

    public void tickJumpHold() {
        if (jumpHeld) jumpHoldTicks++;
    }

    public void endJumpHold() {
        jumpHeld = false;
        jumpHoldTicks = 0;
    }

    public boolean canBark() {
        return barkCooldownTicks == 0;
    }

    public void bark() {
        barkCooldownTicks = BARK_COOLDOWN_TICKS;
    }

    public void tickCooldown() {
        if (barkCooldownTicks > 0) barkCooldownTicks--;
    }
}
