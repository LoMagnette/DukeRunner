package be.lomagnette.qaly.runner;

import dev.tamboui.style.Color;
import static be.lomagnette.qaly.runner.TestRunner.*;

public class JavaReskinTest {

    // --- Task 1: Season → Java eras ---

    public void testJava1EraProperties() {
        var era = Season.JAVA_1;
        assertEquals("Java 1", era.label(), "JAVA_1 label");
        assertEquals(Color.GREEN, era.accentColor(), "JAVA_1 accent");
        assertEquals(Color.rgb(34, 139, 34), era.groundColor(), "JAVA_1 ground");
    }

    public void testJava5EraProperties() {
        var era = Season.JAVA_5;
        assertEquals("Java 5", era.label(), "JAVA_5 label");
        assertEquals(Color.YELLOW, era.accentColor(), "JAVA_5 accent");
        assertEquals(Color.rgb(200, 170, 50), era.groundColor(), "JAVA_5 ground");
    }

    public void testJava11EraProperties() {
        var era = Season.JAVA_11;
        assertEquals("Java 11", era.label(), "JAVA_11 label");
        assertEquals(Color.CYAN, era.accentColor(), "JAVA_11 accent");
        assertEquals(Color.rgb(100, 130, 170), era.groundColor(), "JAVA_11 ground");
    }

    public void testJava21EraProperties() {
        var era = Season.JAVA_21;
        assertEquals("Java 21+", era.label(), "JAVA_21 label");
        assertEquals(Color.MAGENTA, era.accentColor(), "JAVA_21 accent");
        assertEquals(Color.rgb(140, 100, 160), era.groundColor(), "JAVA_21 ground");
    }

    public void testForScoreCyclesEras() {
        assertEquals(Season.JAVA_1, Season.forScore(0), "score 0 → JAVA_1");
        assertEquals(Season.JAVA_5, Season.forScore(100), "score 100 → JAVA_5");
        assertEquals(Season.JAVA_11, Season.forScore(200), "score 200 → JAVA_11");
        assertEquals(Season.JAVA_21, Season.forScore(300), "score 300 → JAVA_21");
        assertEquals(Season.JAVA_1, Season.forScore(400), "score 400 → cycles back to JAVA_1");
    }
}
