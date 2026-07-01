package today.vanta.client.module.impl.hud;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import today.vanta.Vanta;
import today.vanta.client.event.impl.game.render.Render2DEvent;
import today.vanta.client.module.Category;
import today.vanta.client.module.Module;
import today.vanta.client.module.impl.combat.KillAura;
import today.vanta.client.processor.impl.TargetProcessor;
import today.vanta.util.game.events.EventListen;
import today.vanta.util.game.player.PlayerUtil;
import today.vanta.util.game.render.shape.impl.Rectangle;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TargetList extends Module {
    private static final Color BACKGROUND = new Color(20, 20, 20, 200);
    private static final Color DARKER_BACKGROUND = new Color(20, 20, 20, 255);
    int oldlist;
    boolean isIllegal;
    List<EntityPlayer> list = new ArrayList<>();
    EntityPlayer entity;
    public TargetList() {
        super("TargetList", "List of Targets.", Category.HUD);
    }

    @EventListen
    private void onRender2D(Render2DEvent event) {
        list.clear();

        mc.theWorld.getLoadedEntityList().stream()
                .filter(entity -> entity instanceof EntityPlayer && entity != mc.thePlayer && !entity.isDead && mc.thePlayer.getDistanceToEntity(entity) < Vanta.instance.moduleStorage.getT(KillAura.class).attackRange.getValue().floatValue())
                .map(entity -> (EntityPlayer) entity)
                .forEachOrdered(list::add);

        entity = list.isEmpty() ? null : list.get(0);
        for (int i = 0; i < list.size(); i++) {
            isIllegal = PlayerUtil.checkIllegal(list.get(i));
            if (isIllegal) {
                list.remove(i);
            }
        }
        Rectangle
                .create(80,20,100,10)
                .color(DARKER_BACKGROUND)
                .draw();
        mc.exhiFontRendererObj.drawString("Targets:",82,21, Color.WHITE);
        if (mc.thePlayer == null) {return;}
        float width = 100f;
        float height = 10f * list.size();
        Rectangle
                .create(80,  30, width, height)
                .color(BACKGROUND)
                .draw();
        float y = 32;
        for (int i = 0; i < list.size(); i++) {
            String name = list.get(i).getName();
            mc.fontRendererObj.drawString(name,82,y, Color.WHITE);
            y += 10f;
        }
    }

    @Override
    public void onEnable() {
        oldlist = 0;
    }
}
