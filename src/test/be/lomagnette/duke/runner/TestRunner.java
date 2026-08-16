///usr/bin/env jbang "$0" "$@" ; exit $?
//JAVA 26
//DEPS dev.tamboui:tamboui-tui:LATEST
//DEPS dev.tamboui:tamboui-widgets:LATEST
//DEPS dev.tamboui:tamboui-panama-backend:LATEST
//DEPS org.openjfx:javafx-graphics:25:${os.detected.jfxname}
//JAVA_OPTIONS --enable-native-access=ALL-UNNAMED
//SOURCES ../../../../../main/java/be/lomagnette/duke/runner/tui/Game.java
//SOURCES ../../../../../main/java/be/lomagnette/duke/runner/tui/Player.java
//SOURCES ../../../../../main/java/be/lomagnette/duke/runner/tui/Physics.java
//SOURCES ../../../../../main/java/be/lomagnette/duke/runner/tui/Obstacle.java
//SOURCES ../../../../../main/java/be/lomagnette/duke/runner/tui/Spawner.java
//SOURCES ../../../../../main/java/be/lomagnette/duke/runner/tui/Ground.java
//SOURCES ../../../../../main/java/be/lomagnette/duke/runner/tui/Renderer.java
//SOURCES ../../../../../main/java/be/lomagnette/duke/runner/tui/Sprites.java
//SOURCES ../../../../../main/java/be/lomagnette/duke/runner/tui/Season.java
//SOURCES ../../../../../main/java/be/lomagnette/duke/runner/fx/SeasonFX.java
//SOURCES ../../../../../main/java/be/lomagnette/duke/runner/fx/PlayerFX.java
//SOURCES ../../../../../main/java/be/lomagnette/duke/runner/fx/PhysicsFX.java
//SOURCES ../../../../../main/java/be/lomagnette/duke/runner/fx/ObstacleFX.java
//SOURCES ../../../../../main/java/be/lomagnette/duke/runner/fx/GroundFX.java
//SOURCES ../../../../../main/java/be/lomagnette/duke/runner/fx/SpawnerFX.java
//SOURCES ../../../../../main/java/be/lomagnette/duke/runner/fx/GameFX.java
//SOURCES ../../../../../main/java/be/lomagnette/duke/runner/fx/SpritesFX.java
//SOURCES ../../../../../main/java/be/lomagnette/duke/runner/fx/RendererFX.java
//SOURCES ../../../../../main/java/be/lomagnette/duke/runner/fx/ParallaxBackground.java
//SOURCES ../../../../../main/java/be/lomagnette/duke/runner/fx/ParticleSystem.java
//SOURCES ../../../../../main/java/be/lomagnette/duke/runner/fx/ScreenShake.java
//SOURCES ../../../../../main/java/be/lomagnette/duke/runner/fx/AudioFX.java
//SOURCES tui/VariableJumpTest.java
//SOURCES tui/JavaReskinTest.java
//SOURCES fx/JavaFXGameTest.java

package be.lomagnette.duke.runner;

import be.lomagnette.duke.runner.tui.VariableJumpTest;
import be.lomagnette.duke.runner.tui.JavaReskinTest;
import be.lomagnette.duke.runner.fx.JavaFXGameTest;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class TestRunner {

    static int passed = 0;
    static int failed = 0;
    static List<String> failures = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        String filter = null;
        for (int i = 0; i < args.length; i++) {
            if ("--filter".equals(args[i]) && i + 1 < args.length) {
                filter = args[i + 1];
            }
        }

        List<Class<?>> testClasses = List.of(
                VariableJumpTest.class,
                JavaReskinTest.class,
                JavaFXGameTest.class
        );

        for (var clazz : testClasses) {
            if (filter != null && !clazz.getSimpleName().equals(filter)) continue;
            runTestClass(clazz);
        }

        System.out.println();
        System.out.println("Results: " + passed + " passed, " + failed + " failed");
        if (!failures.isEmpty()) {
            System.out.println("Failures:");
            failures.forEach(f -> System.out.println("  - " + f));
            System.exit(1);
        }
    }

    static void runTestClass(Class<?> clazz) throws Exception {
        System.out.println("--- " + clazz.getSimpleName() + " ---");
        Object instance = clazz.getDeclaredConstructor().newInstance();
        for (Method m : clazz.getDeclaredMethods()) {
            if (m.getName().startsWith("test") && m.getParameterCount() == 0) {
                try {
                    m.invoke(instance);
                    System.out.println("  PASS: " + m.getName());
                    passed++;
                } catch (Exception e) {
                    Throwable cause = e.getCause() != null ? e.getCause() : e;
                    System.out.println("  FAIL: " + m.getName() + " - " + cause.getMessage());
                    failed++;
                    failures.add(clazz.getSimpleName() + "." + m.getName() + ": " + cause.getMessage());
                }
            }
        }
    }

    public static void assertEquals(Object expected, Object actual, String msg) {
        if (!expected.equals(actual)) {
            throw new AssertionError(msg + " — expected: " + expected + ", got: " + actual);
        }
    }

    public static void assertTrue(boolean condition, String msg) {
        if (!condition) {
            throw new AssertionError(msg);
        }
    }

    public static void assertFalse(boolean condition, String msg) {
        if (condition) {
            throw new AssertionError(msg);
        }
    }
}
