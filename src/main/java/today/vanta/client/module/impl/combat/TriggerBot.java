package today.vanta.client.module.impl.combat;

import net.minecraft.entity.EntityLivingBase;
import today.vanta.client.event.impl.game.world.UpdateEvent;
import today.vanta.client.module.Category;
import today.vanta.client.module.Module;
import today.vanta.util.game.events.EventListen;
import today.vanta.util.system.math.Counter;

public class TriggerBot extends Module {
    private final Counter attackCounter = new Counter();
    private EntityLivingBase target;
    private final int mincps = 11;
    private final int maxcps = 13;
    public TriggerBot() {
        super("TriggerBot", "Clicks Left trigger when hovering player.", Category.COMBAT);
    }

    private long calculateAttackDelay() {
        long cps = (mincps + maxcps) / 2;
        return 1000 / cps;
    }

    @EventListen
    private void onUpdate(UpdateEvent event) {
        if (mc.objectMouseOver.entityHit instanceof EntityLivingBase) {
            target = (EntityLivingBase) mc.objectMouseOver.entityHit;
            if (attackCounter.hasElapsed(calculateAttackDelay(), true)) {
                mc.clickMouse();
            }
        }
    }
}
