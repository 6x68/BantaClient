package today.vanta.client.module.impl.hud;

import net.minecraft.entity.EntityLivingBase;
import today.vanta.client.event.impl.client.RenderOverlayEvent;
import today.vanta.client.module.Category;
import today.vanta.client.module.Module;
import today.vanta.client.processor.impl.TargetProcessor;
import today.vanta.util.game.events.EventListen;
import today.vanta.util.game.player.ChatUtil;

public class KillStats extends Module {
    EntityLivingBase target;
    public KillStats() {
        super("Kill Stats", "Keeps track of your kills.", Category.HUD);
    }
    @EventListen
    public void onRender2D(RenderOverlayEvent event) {
        if (TargetProcessor.getInstance().target != null) {
            target = TargetProcessor.getInstance().target;
            if (target.isDead) {
                ChatUtil.send(ChatUtil.Prefix.INFO, "Target is dead bro");
            }
        }
    }
}
