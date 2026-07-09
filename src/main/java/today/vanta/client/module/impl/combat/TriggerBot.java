package today.vanta.client.module.impl.combat;

import net.minecraft.entity.EntityLivingBase;
import today.vanta.client.event.impl.game.world.UpdateEvent;
import today.vanta.client.module.Category;
import today.vanta.client.module.Module;
import today.vanta.util.game.events.EventListen;
import today.vanta.util.system.math.ClickUtil;

public class TriggerBot extends Module {
    private final ClickUtil clickUtil = new ClickUtil();
    private EntityLivingBase target;

    public TriggerBot() {
        super("TriggerBot", "Clicks Left trigger when hovering player.", Category.COMBAT);
    }

    @EventListen
    private void onUpdate(UpdateEvent event) {
        if (mc.objectMouseOver.entityHit instanceof EntityLivingBase) {
            target = (EntityLivingBase) mc.objectMouseOver.entityHit;
            if (clickUtil.shouldClick(mc.thePlayer.hurtTime > 0)) {
                mc.clickMouse();
            }
        }
    }
}
