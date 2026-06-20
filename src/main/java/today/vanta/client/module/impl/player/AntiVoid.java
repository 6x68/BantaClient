package today.vanta.client.module.impl.player;

import today.vanta.client.event.impl.game.world.UpdateEvent;
import today.vanta.client.module.Category;
import today.vanta.client.module.Module;
import today.vanta.util.game.events.EventListen;
import today.vanta.util.game.player.PlayerUtil;

public class AntiVoid extends Module {
    int tick;
    public AntiVoid() {
        super("AntiVoid", "teleport yes.", Category.PLAYER);
    }

    @EventListen
    public void onUpdate(UpdateEvent event) {
        if (PlayerUtil.isOverVoid()) {
            tick++;
        } else {
            tick = 0;
        }
    }
}
