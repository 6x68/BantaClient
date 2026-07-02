package today.vanta.client.module.impl.hud;

import net.minecraft.entity.player.EntityPlayer;
import today.vanta.Vanta;
import today.vanta.client.event.impl.client.RenderOverlayEvent;
import today.vanta.client.module.Category;
import today.vanta.client.module.Module;
import today.vanta.client.module.impl.combat.KillAura;
import today.vanta.util.game.events.EventListen;
import today.vanta.util.game.player.PlayerUtil;
import today.vanta.util.game.render.shape.impl.Rectangle;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TargetList extends Module {
    private static final Color BACKGROUND = new Color(20, 20, 20, 200);
    private static final Color DARKER_BACKGROUND = new Color(20, 20, 20, 255);
    private final List<EntityPlayer> list = new ArrayList<>();

    public TargetList() {
        super("TargetList", "List of Targets.", Category.HUD);
    }

    @EventListen
    private void onRender2D(RenderOverlayEvent event) {
        if (mc.thePlayer == null) {
            return;
        }

        list.clear();

        mc.theWorld.getLoadedEntityList().stream()
                .filter(entity -> entity instanceof EntityPlayer && entity != mc.thePlayer && !entity.isDead && mc.thePlayer.getDistanceToEntity(entity) < Vanta.instance.moduleStorage.getT(KillAura.class).attackRange.getValue().floatValue())
                .map(entity -> (EntityPlayer) entity)
                .forEachOrdered(list::add);

        for (int i = 0; i < list.size(); i++) {
            boolean isIllegal = PlayerUtil.checkIllegal(list.get(i));
            if (isIllegal) {
                list.remove(i);
            }
        }

        Rectangle
                .create(80, 20, 100, 10)
                .color(DARKER_BACKGROUND)
                .push(event);

        mc.exhiFontRendererObj.drawString("Targets:", 82, 21, Color.WHITE);


        float width = 100f;
        float height = 10f * list.size();
        Rectangle
                .create(80, 30, width, height)
                .color(BACKGROUND)
                .push(event);
        float y = 32;

        for (EntityPlayer entityPlayer : list) {
            String name = entityPlayer.getName();
            mc.fontRendererObj.drawString(name, 82, y, Color.WHITE);
            y += 10f;
        }
    }
}