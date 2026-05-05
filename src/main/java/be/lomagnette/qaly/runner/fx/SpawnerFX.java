package be.lomagnette.qaly.runner.fx;

import java.util.Optional;
import java.util.Random;

public class SpawnerFX {
    private final Random random = new Random();
    private int ticksSinceLastSpawn;
    private boolean lastWasThrowOnly;

    public SpawnerFX() {
        this.ticksSinceLastSpawn = 0;
        this.lastWasThrowOnly = false;
    }

    public Optional<ObstacleFX> maybeSpawn(int score, float speed, float spawnX) {
        ticksSinceLastSpawn++;

        int minGap = Math.max(40, 80 - score / 5);
        if (ticksSinceLastSpawn < minGap) {
            return Optional.empty();
        }

        if (random.nextFloat() > 0.04f) {
            return Optional.empty();
        }

        ObstacleFX obstacle = createRandomObstacle(spawnX);
        ticksSinceLastSpawn = 0;
        lastWasThrowOnly = obstacle.throwable();
        return Optional.of(obstacle);
    }

    private ObstacleFX createRandomObstacle(float x) {
        if (lastWasThrowOnly) {
            return createJumpObstacle(x);
        }

        return random.nextFloat() < 0.6f
                ? createJumpObstacle(x)
                : createThrowObstacle(x);
    }

    private ObstacleFX createJumpObstacle(float x) {
        return switch (random.nextInt(4)) {
            case 0 -> new ObstacleFX.ConferenceStage(x);
            case 1 -> new ObstacleFX.LaptopStack(x, true);
            case 2 -> new ObstacleFX.LaptopStack(x, false);
            default -> new ObstacleFX.CoffeeSpill(x);
        };
    }

    private ObstacleFX createThrowObstacle(float x) {
        return random.nextFloat() < 0.6f
                ? new ObstacleFX.ConfusedIntern(x)
                : new ObstacleFX.SlowBuildServer(x);
    }
}
