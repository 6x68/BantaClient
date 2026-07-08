package today.vanta.client.module.impl.misc;

import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import today.vanta.client.event.impl.game.world.UpdateEvent;
import today.vanta.client.module.Category;
import today.vanta.client.module.Module;
import today.vanta.util.game.events.EventListen;
import today.vanta.util.game.player.MovementUtil;

public class MinibloxFix extends Module {
    public MinibloxFix() {
        super("Miniblox Fix", "Fixes miniblox translation layer issues.", Category.MISC);
    }

    @EventListen
    private void onUpdate(UpdateEvent event) {
        PotionEffect speedEffect = mc.thePlayer.getActivePotionEffect(Potion.moveSpeed);
        if (speedEffect != null) {
            float speed = mc.thePlayer.capabilities.getWalkSpeed() * speedEffect.getAmplifier();
            mc.thePlayer.moveForward = speed;
        }
    }
}
