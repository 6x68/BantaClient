package today.vanta.client.module.impl.combat;

import today.vanta.client.event.impl.game.player.AllowAttackWhileBlockingEvent;
import today.vanta.client.module.Category;
import today.vanta.client.module.Module;
import today.vanta.util.game.events.EventListen;

public class BlockHit extends Module {
    public BlockHit() {
        super("BlockHit", "Allows you to hit while blocking.", Category.COMBAT);
    }

    @EventListen
    private void onAllowAttackWhileBlocking(AllowAttackWhileBlockingEvent event) {
        event.cancelled = true;
    }
}