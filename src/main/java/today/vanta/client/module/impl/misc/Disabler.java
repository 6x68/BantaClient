package today.vanta.client.module.impl.misc;

import net.minecraft.network.play.client.C0CPacketInput;
import today.vanta.client.event.impl.game.world.UpdateEvent;
import today.vanta.client.module.Category;
import today.vanta.client.module.Module;
import today.vanta.client.setting.impl.MultiStringSetting;
import today.vanta.util.game.events.EventListen;

import java.util.Arrays;

public class Disabler extends Module {
    private final MultiStringSetting disable = MultiStringSetting.builder()
            .name("Disable")
            .value("Miniblox")
            .values("Miniblox")
            .build();

    public Disabler() {
        super("Disabler", "Disable anticheats, or at least parts of them.", Category.MISC);
    }

    @EventListen
    private void onUpdate(UpdateEvent event) {
        if (disable.isEnabled("Miniblox")) {
            mc.thePlayer.sendQueue.addToSendQueue(new C0CPacketInput(
                    mc.thePlayer.moveStrafing,
                    mc.thePlayer.moveForward,
                    mc.thePlayer.movementInput.jump,
                    mc.thePlayer.movementInput.sneak
            ));
        }
    }

    @Override
    public String getSuffix() {
        return "" + (disable.getValue().length == 1 ? Arrays.toString(disable.getValue()).replaceAll("[\\[\\](){}]", "") : disable.getValue().length);
    }
}