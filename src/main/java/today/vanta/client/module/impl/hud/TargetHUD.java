package today.vanta.client.module.impl.hud;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import today.vanta.Vanta;
import today.vanta.client.event.impl.game.render.Render2DEvent;
import today.vanta.client.module.Category;
import today.vanta.client.module.Module;
import today.vanta.client.module.impl.client.Theme;
import today.vanta.client.processor.impl.TargetProcessor;
import today.vanta.util.game.events.EventListen;
import today.vanta.util.game.events.EventPriority;
import today.vanta.util.game.render.RenderUtil;
import today.vanta.util.game.render.font.CFonts;

import java.awt.*;

public class TargetHUD extends Module {
    EntityLivingBase target;
    public TargetHUD() {
        super("TargetHUD", "Target Information", Category.HUD);
    }

    @EventListen(priority = EventPriority.LOWEST)
    public void onRender2D(Render2DEvent event) {
        Color bg = new Color(20,20,20,200);
        Color no = new Color(0,0,0,255);
        if (!(mc.currentScreen instanceof GuiChat) && TargetProcessor.getInstance().target == null) {
            return;
        }

        if (TargetProcessor.getInstance().target == null && mc.currentScreen instanceof GuiChat) {
            target = mc.thePlayer;
        } else if (TargetProcessor.getInstance().target instanceof EntityPlayer) {
            target = (EntityPlayer) TargetProcessor.getInstance().target;
        }

        if (target == null) return;

        float width = 130f;
        float height = 40f;
        float healthbar = width * (target.getHealth() / target.getMaxHealth());

        RenderUtil.rectangle(400,370,width,height,bg);
        RenderUtil.player_head((EntityPlayer) target, 400, 370, 36f);
        CFonts.SFPT_SEMIBOLD_20.drawStringWithShadow(target.getName(), 438, 374, Vanta.instance.moduleStorage.getT(Theme.class).colors[0]);
        CFonts.SFPT_SEMIBOLD_20.drawStringWithShadow(String.format("%.1f", target.getHealth()), 438, 385, Color.WHITE);
        RenderUtil.rectangle(400,406,healthbar,4f, Vanta.instance.moduleStorage.getT(Theme.class).colors[0]);
    }
}