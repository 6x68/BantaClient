package today.vanta.client.module.impl.movement;

import today.vanta.client.event.impl.game.world.UpdateEvent;
import today.vanta.client.module.Category;
import today.vanta.client.module.Module;
import today.vanta.util.game.events.EventListen;

public class Sprint extends Module {
    public Sprint() {
        super("Sprint", "Makes you always sprint.", Category.MOVEMENT);
        displayNames = new String[] {"Sprint", "AutoSprint", "ToggleSprint"};
    }

    @EventListen
    private void onUpdate(UpdateEvent event) {
        mc.gameSettings.keyBindSprint.pressed = true;
    }

    @Override
    public void onDisable() {
        mc.gameSettings.keyBindSprint.pressed = false;
    }
}