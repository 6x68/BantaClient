package today.vanta.client.module.impl.movement;

import today.vanta.client.event.impl.game.player.SneakInputEvent;
import today.vanta.client.module.Category;
import today.vanta.client.module.Module;
import today.vanta.util.game.events.EventListen;

public class FastSneak extends Module {
    public FastSneak() {
        super("FastSneak","Removes sneaking slowdown.", Category.MOVEMENT);
    }

    @EventListen
    private void onSneakInput(SneakInputEvent event) {
        event.cancelled = true;
    }
}