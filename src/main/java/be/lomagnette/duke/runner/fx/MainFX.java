///usr/bin/env jbang "$0" "$@" ; exit $?
//JAVA 26
//DEPS org.openjfx:javafx-controls:25:${os.detected.jfxname}
//DEPS org.openjfx:javafx-graphics:25:${os.detected.jfxname}
//JAVA_OPTIONS --enable-native-access=ALL-UNNAMED
//FILES META-INF/native-image/fx/reflect-config.json=reflect-config.json
//FILES META-INF/native-image/fx/resource-config.json=resource-config.json
//FILES META-INF/native-image/fx/jni-config.json=jni-config.json
//NATIVE_OPTIONS --initialize-at-run-time=com.sun.prism,com.sun.glass,com.sun.javafx,com.sun.scenario,com.sun.pisces,javafx
//NATIVE_OPTIONS --enable-native-access=ALL-UNNAMED
//NATIVE_OPTIONS -H:+UnlockExperimentalVMOptions
//NATIVE_OPTIONS -H:+ForeignAPISupport
//NATIVE_OPTIONS -Djava.library.path=.
//
// Native image build (requires GraalVM with native-image):
//   1. jbang export native --force src/main/java/be/lomagnette/qaly/runner/fx/MainFX.java
//   2. Extract JavaFX native libs next to the binary:
//      unzip -jo ~/.m2/repository/org/openjfx/javafx-graphics/25/javafx-graphics-25-<platform>.jar "*.so" -d .
//      (use *.dylib on macOS, platform = linux-aarch64, mac-aarch64, etc.)
//   3. Run: ./MainFX
//SOURCES GameFX.java
//SOURCES PlayerFX.java
//SOURCES PhysicsFX.java
//SOURCES ObstacleFX.java
//SOURCES SpawnerFX.java
//SOURCES GroundFX.java
//SOURCES SeasonFX.java
//SOURCES SpritesFX.java
//SOURCES RendererFX.java
//SOURCES ParallaxBackground.java
//SOURCES ParticleSystem.java
//SOURCES ScreenShake.java
//SOURCES AudioFX.java
//SOURCES QalyRunnerFX.java

package be.lomagnette.duke.runner.fx;

import javafx.application.Application;
import java.io.File;
import java.nio.file.Path;

public class MainFX {
    public static void main(String[] args) {
        // Ensure native libraries next to the binary are found
        try {
            String binPath = ProcessHandle.current().info().command().orElse(null);
            String libDir = binPath != null ? Path.of(binPath).getParent().toString() : ".";
            String existing = System.getProperty("java.library.path", "");
            System.setProperty("java.library.path",
                    libDir + File.pathSeparator + "." + File.pathSeparator + existing);
        } catch (Exception ignored) {}
        Application.launch(QalyRunnerFX.class, args);
    }
}
