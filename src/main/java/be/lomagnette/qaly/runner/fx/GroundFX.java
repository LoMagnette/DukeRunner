package be.lomagnette.qaly.runner.fx;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GroundFX {

    public enum DecoType { COFFEE_CUP, TERMINAL, GIT_BRANCH, IDE_ICON, DOCKER_WHALE, CLOUD }

    public record Decoration(DecoType type, float x) {}

    private final List<Decoration> decorations = new ArrayList<>();
    private final Random random = new Random();
    private SeasonFX currentSeason = SeasonFX.JAVA_1;
    private float maxX = 160;

    public GroundFX() {
        initDecorations(160);
    }

    private void initDecorations(float width) {
        this.maxX = width;
        decorations.clear();
        for (float x = 10; x < width; x += 15 + random.nextInt(20)) {
            decorations.add(new Decoration(randomDecoType(), x));
        }
    }

    public void scroll(float speed) {
        var toRemove = new ArrayList<Decoration>();
        for (var d : decorations) {
            if (d.x() - speed < -5) {
                toRemove.add(d);
            }
        }
        decorations.removeAll(toRemove);

        var updated = new ArrayList<Decoration>();
        for (var d : decorations) {
            updated.add(new Decoration(d.type(), d.x() - speed));
        }
        decorations.clear();
        decorations.addAll(updated);

        for (var removed : toRemove) {
            float newX = maxX + random.nextInt(20);
            decorations.add(new Decoration(randomDecoType(), newX));
        }

        if (decorations.isEmpty() || decorations.getLast().x() < maxX - 30) {
            decorations.add(new Decoration(randomDecoType(), maxX + random.nextInt(10)));
        }
    }

    public void setSeason(SeasonFX season) {
        this.currentSeason = season;
    }

    public void setMaxX(float maxX) {
        this.maxX = maxX;
    }

    public SeasonFX currentSeason() { return currentSeason; }
    public List<Decoration> decorations() { return decorations; }

    private DecoType randomDecoType() {
        return switch (currentSeason) {
            case JAVA_1 -> pick(DecoType.COFFEE_CUP, DecoType.GIT_BRANCH, DecoType.TERMINAL);
            case JAVA_5 -> pick(DecoType.IDE_ICON, DecoType.COFFEE_CUP, DecoType.DOCKER_WHALE);
            case JAVA_11 -> pick(DecoType.TERMINAL, DecoType.CLOUD, DecoType.COFFEE_CUP);
            case JAVA_21 -> pick(DecoType.COFFEE_CUP, DecoType.CLOUD, DecoType.IDE_ICON);
        };
    }

    private DecoType pick(DecoType... options) {
        return options[random.nextInt(options.length)];
    }
}
