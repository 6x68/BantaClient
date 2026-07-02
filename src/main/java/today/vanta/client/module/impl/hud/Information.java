package today.vanta.client.module.impl.hud;

import net.minecraft.client.gui.GuiChat;
import today.vanta.client.event.impl.game.render.Render2DEvent;
import today.vanta.client.module.Category;
import today.vanta.client.module.Module;
import today.vanta.util.game.events.EventListen;
import today.vanta.util.game.player.MovementUtil;
import today.vanta.util.game.player.PlayerUtil;

import java.awt.*;

public class Information extends Module {
    public Information() {
        super("Information", "Provides information on the player.", Category.HUD);
    }

    @EventListen
    public void onRender2D(Render2DEvent event) {
        float y = event.scaledResolution.getScaledHeight() - 19;
        if (mc.currentScreen instanceof GuiChat) {
            y = event.scaledResolution.getScaledHeight() - 230;
        }
        mc.exhiFontRendererObj.drawString("Ping: " + PlayerUtil.getPing(mc.thePlayer), 2, y, Color.WHITE,true);
        mc.exhiFontRendererObj.drawString("BPS: " + MovementUtil.getBPS(), 2, y + 10, Color.WHITE,true);
    }
}
