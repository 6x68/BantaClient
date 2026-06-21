package today.vanta.client.module.impl.combat;

import net.minecraft.entity.EntityLivingBase;
import today.vanta.client.event.impl.game.world.UpdateEvent;
import today.vanta.client.module.Category;
import today.vanta.client.module.Module;
import today.vanta.util.game.events.EventListen;
import today.vanta.util.system.math.Counter;

public class TriggerBot extends Module {
    private final Counter attackCounter = new Counter();
    EntityLivingBase target;
    int mincps = 11;
    int maxcps = 13;
    public TriggerBot() {
        super("TriggerBot", "Clicks Left trigger when hovering player.", Category.COMBAT);
    }

    private long calculateAttackDelay() {
        long cps = (mincps + mincps) / 2;
        return 1000 / cps;
    }

    @EventListen
    public void onUpdate(UpdateEvent event) {
        if (mc.objectMouseOver.entityHit instanceof EntityLivingBase) {
            target = (EntityLivingBase) mc.objectMouseOver.entityHit;
            if (attackCounter.hasElapsed(calculateAttackDelay(), true) && target.hurtTime < 1) {
                mc.clickMouse();
            }
        }
    }
}
