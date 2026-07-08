package today.vanta.client.module.impl.movement;

import today.vanta.client.event.impl.game.player.JumpDelayEvent;
import today.vanta.client.module.Category;
import today.vanta.client.module.Module;
import today.vanta.util.game.events.EventListen;

public class NoJumpDelay extends Module {
    public NoJumpDelay() {
        super("NoJumpDelay", "Removes Minecraft's jump delay.", Category.MOVEMENT);
    }

    @EventListen
    private void onJumpDelay(JumpDelayEvent event) {
        event.cancelled = true;
    }
}