///usr/bin/env jbang "$0" "$@" ; exit $?
//JAVA 25
//DEPS org.openjfx:javafx-controls:25:${os.detected.jfxname}
//DEPS org.openjfx:javafx-graphics:25:${os.detected.jfxname}
//JAVA_OPTIONS --enable-native-access=ALL-UNNAMED
//FILES META-INF/native-image/fx/reflect-config.json=main/java/be/lomagnette/qaly/runner/fx/reflect-config.json
//FILES META-INF/native-image/fx/resource-config.json=main/java/be/lomagnette/qaly/runner/fx/resource-config.json
//
// Native image build (requires GraalVM with JavaFX support):
//   jbang export native src/MainFX.java
//
// For Gluon GluonFX substrate (recommended for JavaFX native):
//   See https://docs.gluonhq.com/ for platform-specific setup
//SOURCES main/java/be/lomagnette/qaly/runner/fx/GameFX.java
//SOURCES main/java/be/lomagnette/qaly/runner/fx/PlayerFX.java
//SOURCES main/java/be/lomagnette/qaly/runner/fx/PhysicsFX.java
//SOURCES main/java/be/lomagnette/qaly/runner/fx/ObstacleFX.java
//SOURCES main/java/be/lomagnette/qaly/runner/fx/SpawnerFX.java
//SOURCES main/java/be/lomagnette/qaly/runner/fx/GroundFX.java
//SOURCES main/java/be/lomagnette/qaly/runner/fx/SeasonFX.java
//SOURCES main/java/be/lomagnette/qaly/runner/fx/SpritesFX.java
//SOURCES main/java/be/lomagnette/qaly/runner/fx/RendererFX.java
//SOURCES main/java/be/lomagnette/qaly/runner/fx/ParallaxBackground.java
//SOURCES main/java/be/lomagnette/qaly/runner/fx/ParticleSystem.java
//SOURCES main/java/be/lomagnette/qaly/runner/fx/ScreenShake.java
//SOURCES main/java/be/lomagnette/qaly/runner/fx/AudioFX.java
//SOURCES main/java/be/lomagnette/qaly/runner/fx/QalyRunnerFX.java

package be.lomagnette.qaly.runner.fx;

import javafx.application.Application;

public class MainFX {
    public static void main(String[] args) {
        Application.launch(QalyRunnerFX.class, args);
    }
}
