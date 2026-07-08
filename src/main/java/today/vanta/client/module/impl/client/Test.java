package today.vanta.client.module.impl.client;

import today.vanta.client.event.impl.client.RenderOverlayEvent;
import today.vanta.client.module.Category;
import today.vanta.client.module.Module;
import today.vanta.util.game.events.EventListen;
import today.vanta.util.game.render.shape.impl.Triangle;

import java.awt.*;

public class Test extends Module {
    public Test() {
        super("Test", "Test module for developers.", Category.CLIENT);
    }

    @EventListen
    private void onRenderOverlay(RenderOverlayEvent event) {
        Triangle
                .create(15, 15 + 150, 150, 149)
                .rotate(90)
                .color(Color.RED)
                .push(event);
    }
}
