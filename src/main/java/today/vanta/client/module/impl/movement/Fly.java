package today.vanta.client.module.impl.movement;

import today.vanta.client.event.impl.game.world.UpdateEvent;
import today.vanta.client.module.Category;
import today.vanta.client.module.Module;
import today.vanta.util.game.events.EventListen;
import today.vanta.util.game.player.MovementUtil;

public class Fly extends Module {
    public Fly() {
        super("Fly", "Flies", Category.MOVEMENT);
    }

    @EventListen
    public void onUpdate(UpdateEvent event) {
        mc.thePlayer.motionY = 0f;
        MovementUtil.strafe(0.23f);
    }

    @Override
    public void onEnable() {
        if (mc.thePlayer != null) {
            mc.thePlayer.sendChatMessage("/desync");
        }
    }

    @Override
    public void onDisable() {
        if (mc.thePlayer != null) {
            mc.thePlayer.sendChatMessage("/desync");
        }
    }
}
