package today.vanta.client.module.impl.combat;

import today.vanta.client.event.impl.game.player.KeepSprintEvent;
import today.vanta.client.module.Category;
import today.vanta.client.module.Module;
import today.vanta.util.game.events.EventListen;

public class KeepSprint extends Module {
    public KeepSprint() {
        super("KeepSprint", "Removes Attack Slowdown", Category.COMBAT);
    }

    @EventListen
    private void onSprint(KeepSprintEvent event) {
        event.greater = false;
    }
}