package today.vanta.client.module.impl.hud;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import today.vanta.Vanta;
import today.vanta.client.event.impl.client.RenderOverlayEvent;
import today.vanta.client.module.Category;
import today.vanta.client.module.Module;
import today.vanta.client.module.impl.combat.KillAura;
import today.vanta.client.processor.impl.TargetProcessor;
import today.vanta.util.game.events.EventListen;
import today.vanta.util.game.player.ChatUtil;
import today.vanta.util.game.world.EntityUtil;

import java.util.ArrayList;
import java.util.List;

public class KillStats extends Module {
    KillAura killaura = Vanta.instance.moduleStorage.getT(KillAura.class);
    List<EntityLivingBase> list = new ArrayList<>();
    EntityLivingBase target;
    int kills = 0;
    String oldTarget = "";
    public KillStats() {
        super("Kill Stats", "Keeps track of your kills.", Category.HUD);
    }
    @EventListen
    public void onRender2D(RenderOverlayEvent event) {
        list.clear();

        mc.theWorld.getLoadedEntityList().stream()
                .filter(e -> e instanceof EntityPlayer)
                .map(e -> (EntityLivingBase) e)
                .filter(e -> EntityUtil.isValidforKill(e, killaura.raytrace.getValue(), killaura.searchRange.getValue().floatValue()))
                .sorted(EntityUtil.getComparatorForSorting(killaura.sortMode.getValue()))
                .forEachOrdered(list::add);

        target = list.isEmpty() ? null : list.get(0);
        if (killaura.isEnabled() && target != null) {
            if (target.isDead && !target.getName().equals(oldTarget)) {
                oldTarget = target.getName();
                kills++;
                ChatUtil.send(ChatUtil.Prefix.INFO, String.valueOf(kills));
                list.remove(0);
            }
        }
        if (target == null) {
            oldTarget = "";
        }
    }
}
