package today.vanta.client.module.impl.player;

import today.vanta.client.event.impl.game.player.SwingDelayEvent;
import today.vanta.client.module.Category;
import today.vanta.client.module.Module;
import today.vanta.util.game.events.EventListen;

public class NoClickDelay extends Module {
    public NoClickDelay() {
        super("NoClickDelay", "Removes Minecraft Click Delay.", Category.PLAYER);
    }

    @EventListen
    public void onSwingDelay(SwingDelayEvent event) {
        event.cancelled = true;
    }
}
