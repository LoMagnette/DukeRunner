///usr/bin/env jbang "$0" "$@" ; exit $?
//JAVA 26
//DEPS org.openjfx:javafx-controls:25:${os.detected.jfxname}
//DEPS org.openjfx:javafx-graphics:25:${os.detected.jfxname}
//JAVA_OPTIONS --enable-native-access=ALL-UNNAMED
//FILES META-INF/native-image/fx/reflect-config.json=reflect-config.json
//FILES META-INF/native-image/fx/resource-config.json=resource-config.json
//
// Native image build (requires GraalVM with JavaFX support):
//   jbang export native src/MainFX.java
//
// For Gluon GluonFX substrate (recommended for JavaFX native):
//   See https://docs.gluonhq.com/ for platform-specific setup
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

package be.lomagnette.qaly.runner.fx;

import javafx.application.Application;

public class MainFX {
    public static void main(String[] args) {
        Application.launch(QalyRunnerFX.class, args);
    }
}
