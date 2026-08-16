///usr/bin/env jbang "$0" "$@" ; exit $?
//JAVA 26
//REPOS mavencentral,snapshots=https://central.sonatype.com/repository/maven-snapshots
//DEPS dev.tamboui:tamboui-tui:0.5.0-20260812.202257-6
//DEPS dev.tamboui:tamboui-widgets:0.5.0-20260812.202257-6
//DEPS dev.tamboui:tamboui-panama-backend:0.5.0-20260812.202257-6
//DEPS dev.tamboui:tamboui-image:0.5.0-20260812.202257-6
//DEPS dev.tamboui:tamboui-tfx:0.5.0-20260812.202257-6
//DEPS dev.tamboui:tamboui-tfx-tui:0.5.0-20260812.202257-6
//JAVA_OPTIONS --enable-native-access=ALL-UNNAMED
//SOURCES Game.java
//SOURCES Player.java
//SOURCES Physics.java
//SOURCES Obstacle.java
//SOURCES Spawner.java
//SOURCES Ground.java
//SOURCES Renderer.java
//SOURCES Sprites.java
//SOURCES Season.java
//FILES META-INF/native-image/resource-config.json=resource-config.json
//FILES bouvier.png=../../../../../../resources/bouvier.png

package be.lomagnette.duke.runner.tui;

import dev.tamboui.tui.TuiConfig;
import dev.tamboui.tui.TuiRunner;
import dev.tamboui.tui.event.KeyEvent;
import dev.tamboui.tui.event.ResizeEvent;
import dev.tamboui.tui.event.TickEvent;

import java.time.Duration;

public class DukeRunner {

    public static void main(String[] args) throws Exception {
        var config = TuiConfig.builder()
                .tickRate(Duration.ofMillis(16))
                .build();

        var game = new Game();
        var renderer = new Renderer();

        try (var tui = TuiRunner.create(config)) {
            tui.run(
                    (event, runner) -> switch (event) {
                        case KeyEvent k -> {
                            boolean redraw = game.handleEvent(k);
                            if (game.quit) runner.quit();
                            yield redraw;
                        }
                        case TickEvent t -> {
                            game.tick();
                            yield true;
                        }
                        case ResizeEvent r -> true;
                        default -> false;
                    },
                    frame -> renderer.render(frame, game)
            );
        }
    }
}
