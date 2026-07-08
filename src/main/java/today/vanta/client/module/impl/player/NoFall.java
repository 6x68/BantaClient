package today.vanta.client.module.impl.player;

import today.vanta.client.event.impl.game.world.UpdateEvent;
import today.vanta.client.module.Category;
import today.vanta.client.module.Module;
import today.vanta.util.game.events.EventListen;

public class NoFall extends Module {
    public NoFall() {
        super("NoFall", "No fall damage.", Category.PLAYER);
    }

    @EventListen
    private void onUpdate(UpdateEvent event) {
        mc.thePlayer.fallDistance = 0.0f;
    }
}