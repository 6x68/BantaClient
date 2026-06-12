package today.vanta.client.module.impl.hud;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.player.EntityPlayer;
import today.vanta.client.event.impl.game.render.Render2DEvent;
import today.vanta.client.module.Category;
import today.vanta.client.module.Module;
import today.vanta.client.module.impl.hud.arraylist.BitMapRenderer;
import today.vanta.client.processor.impl.TargetProcessor;
import today.vanta.util.game.events.EventListen;
import today.vanta.util.game.player.ChatUtil;
import today.vanta.util.game.render.RenderUtil;
import today.vanta.util.game.render.shape.impl.Rectangle;

import java.awt.*;

public class TargetList extends Module {
    private static final Color BACKGROUND = new Color(20, 20, 20, 200);
    private static final Color DARKER_BACKGROUND = new Color(20, 20, 20, 255);
    int oldlist;
    public TargetList() {
        super("TargetList", "List of Targets.", Category.HUD);
    }

    @EventListen
    public void onRender2D(Render2DEvent event) {
        Rectangle
                .create(80,20,100,10)
                .color(DARKER_BACKGROUND)
                .draw();
        mc.exhiFontRendererObj.drawString("Targets:",82,21, Color.WHITE);
        if (mc.thePlayer == null) {return;}
        if (TargetProcessor.getInstance().target == null || !(TargetProcessor.getInstance().target instanceof EntityPlayer)) {
            oldlist = 0;
            return;
        }
        float width = 100f;
        float height = 10f * TargetProcessor.getInstance().list.size();
        Rectangle
                .create(80,  30, width, height)
                .color(BACKGROUND)
                .draw();
        float y = 32;
        for (int i = 0; i < TargetProcessor.getInstance().list.size(); i++) {
            String name = TargetProcessor.getInstance().list.get(i).getName();
            mc.fontRendererObj.drawString(name,82,y, Color.WHITE);
            y += 10f;
        }
    }

    @Override
    public void onEnable() {
        oldlist = 0;
    }
}
