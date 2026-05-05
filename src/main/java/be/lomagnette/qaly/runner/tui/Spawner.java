package be.lomagnette.qaly.runner.tui;

import java.util.Optional;
import java.util.Random;

public class Spawner {
    private final Random random = new Random();
    private int ticksSinceLastSpawn;
    private boolean lastWasBarkOnly;

    public Spawner() {
        this.ticksSinceLastSpawn = 0;
        this.lastWasBarkOnly = false;
    }

    public Optional<Obstacle> maybeSpawn(int score, float speed, float spawnX) {
        ticksSinceLastSpawn++;

        int minGap = Math.max(40, 80 - score / 5);
        if (ticksSinceLastSpawn < minGap) {
            return Optional.empty();
        }

        // Random chance to spawn increases with ticks since last spawn
        if (random.nextFloat() > 0.04f) {
            return Optional.empty();
        }

        Obstacle obstacle = createRandomObstacle(spawnX);
        ticksSinceLastSpawn = 0;
        lastWasBarkOnly = obstacle.throwable();
        return Optional.of(obstacle);
    }

    private Obstacle createRandomObstacle(float x) {
        // If last obstacle was bark-only, force a jump obstacle
        if (lastWasBarkOnly) {
            return createJumpObstacle(x);
        }

        // Weighted random: 60% jump, 40% bark
        return random.nextFloat() < 0.6f
                ? createJumpObstacle(x)
                : createBarkObstacle(x);
    }

    private Obstacle createJumpObstacle(float x) {
        return switch (random.nextInt(4)) {
            case 0 -> new Obstacle.ConferenceStage(x);
            case 1 -> new Obstacle.LaptopStack(x, true);
            case 2 -> new Obstacle.LaptopStack(x, false);
            default -> new Obstacle.CoffeeSpill(x);
        };
    }

    private Obstacle createBarkObstacle(float x) {
        return random.nextFloat() < 0.6f
                ? new Obstacle.ConfusedIntern(x)
                : new Obstacle.SlowBuildServer(x);
    }
}
