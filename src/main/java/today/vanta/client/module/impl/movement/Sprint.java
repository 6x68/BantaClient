package today.vanta.client.module.impl.movement;

import net.minecraft.network.play.client.C0CPacketInput;
import today.vanta.client.event.impl.game.world.UpdateEvent;
import today.vanta.client.module.Category;
import today.vanta.client.module.Module;
import today.vanta.client.setting.impl.BooleanSetting;
import today.vanta.util.game.events.EventListen;

public class Sprint extends Module {
    private final BooleanSetting
            bypass = BooleanSetting.builder()
            .name("Miniblox Bypass")
            .value(false)
            .build();
    public Sprint() {
        super("Sprint", "Makes you always sprint.", Category.MOVEMENT);
        displayNames = new String[] {"Sprint", "AutoSprint", "ToggleSprint"};
    }

    @EventListen
    private void onUpdate(UpdateEvent event) {
        mc.gameSettings.keyBindSprint.pressed = true;
        if (bypass.getValue()) {

            mc.thePlayer.sendQueue.addToSendQueue(new C0CPacketInput(
                    mc.thePlayer.moveStrafing,
                    mc.thePlayer.moveForward,
                    mc.thePlayer.movementInput.jump,
                    mc.thePlayer.movementInput.sneak
            ));
        }
    }

    @Override
    public void onDisable() {
        mc.gameSettings.keyBindSprint.pressed = false;
    }
}