package be.lomagnette.qaly.runner.fx;

import javafx.scene.paint.Color;

public enum SeasonFX {
    JAVA_1("Java 1", Color.GREEN, Color.rgb(34, 139, 34)),
    JAVA_5("Java 5", Color.YELLOW, Color.rgb(200, 170, 50)),
    JAVA_11("Java 11", Color.CYAN, Color.rgb(100, 130, 170)),
    JAVA_21("Java 21+", Color.MAGENTA, Color.rgb(140, 100, 160));

    private final String label;
    private final Color accentColor;
    private final Color groundColor;

    SeasonFX(String label, Color accentColor, Color groundColor) {
        this.label = label;
        this.accentColor = accentColor;
        this.groundColor = groundColor;
    }

    public String label() { return label; }
    public Color accentColor() { return accentColor; }
    public Color groundColor() { return groundColor; }

    public static SeasonFX forScore(int score) {
        return values()[(score / 100) % 4];
    }
}
