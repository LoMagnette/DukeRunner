package be.lomagnette.qaly.runner;

import dev.tamboui.style.Color;

public enum Season {
    SPRING("Spring", Color.GREEN, Color.rgb(34, 139, 34)),
    SUMMER("Summer", Color.YELLOW, Color.rgb(200, 170, 50)),
    AUTUMN("Autumn", Color.rgb(255, 140, 0), Color.rgb(139, 90, 43)),
    WINTER("Winter", Color.CYAN, Color.rgb(200, 200, 220));

    private final String label;
    private final Color accentColor;
    private final Color groundColor;

    Season(String label, Color accentColor, Color groundColor) {
        this.label = label;
        this.accentColor = accentColor;
        this.groundColor = groundColor;
    }

    public String label() { return label; }
    public Color accentColor() { return accentColor; }
    public Color groundColor() { return groundColor; }

    public static Season forScore(int score) {
        return values()[(score / 100) % 4];
    }
}
