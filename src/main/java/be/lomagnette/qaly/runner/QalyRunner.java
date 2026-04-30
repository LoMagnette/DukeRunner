///usr/bin/env jbang "$0" "$@" ; exit $?
//JAVA 26
//DEPS dev.tamboui:tamboui-tui:LATEST
//DEPS dev.tamboui:tamboui-widgets:LATEST
//DEPS dev.tamboui:tamboui-panama-backend:LATEST
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

package be.lomagnette.qaly.runner;

import dev.tamboui.tui.TuiConfig;
import dev.tamboui.tui.TuiRunner;
import dev.tamboui.tui.event.KeyEvent;
import dev.tamboui.tui.event.ResizeEvent;
import dev.tamboui.tui.event.TickEvent;

import java.time.Duration;

public class QalyRunner {

    public static void main(String[] args) throws Exception {
        var config = TuiConfig.builder()
                .tickRate(Duration.ofMillis(33))
                .build();

        var game = new Game();

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
                    frame -> Renderer.render(frame, game)
            );
        }
    }
}
