package today.vanta.client.module.impl.hud;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import today.vanta.client.event.impl.client.RenderOverlayEvent;
import today.vanta.client.module.Category;
import today.vanta.client.module.Module;
import today.vanta.client.module.impl.combat.KillAura;
import today.vanta.client.processor.impl.TargetProcessor;
import today.vanta.util.game.events.EventListen;
import today.vanta.util.game.player.ChatUtil;
import today.vanta.util.game.render.RenderUtil;
import today.vanta.util.game.render.font.CFonts;
import today.vanta.util.game.world.EntityUtil;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class KillStats extends Module {
    private final List<EntityLivingBase> list = new ArrayList<>();
    private int kills = 0;

    public KillStats() {
        super("KillStats", "Keeps track of your kills.", Category.HUD);
    }

    @EventListen
    private void onRenderOverlay(RenderOverlayEvent event) {
        KillAura killAura = TargetProcessor.getInstance().killaura;
        list.clear();

        mc.theWorld.getLoadedEntityList().stream()
                .filter(e -> e instanceof EntityPlayer)
                .map(e -> (EntityLivingBase) e)
                .filter(e -> EntityUtil.isValidforKill(e, killAura.raytrace.getValue(), killAura.searchRange.getValue().floatValue()))
                .sorted(EntityUtil.getComparatorForSorting(killAura.sortMode.getValue()))
                .forEachOrdered(list::add);

        if (!list.isEmpty()) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).isDead) {
                    if (list.get(i).getLastAttacker() == null) return;
                    ChatUtil.send(ChatUtil.Prefix.INFO, list.get(i).getName() + " Killer: " + list.get(i).getLastAttacker().getName());
                    if (list.get(i).getLastAttacker() == mc.thePlayer) {
                        kills++;
                        list.remove(i);
                    }
                }
            }
        }

        if (mc.thePlayer == null || mc.theWorld == null) {
            kills = 0;
        }

        RenderUtil.drawWindowRectangle(event, "Kill Stats", 90, 90, 50, 25);
        CFonts.SFPT_REGULAR_16.drawStringWithShadow(String.valueOf(kills), 91, 102, Color.WHITE);
    }
}
