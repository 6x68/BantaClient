package today.vanta.client.module.impl.hud;

import today.vanta.client.event.impl.game.render.Render2DEvent;
import today.vanta.client.module.Category;
import today.vanta.client.module.Module;
import today.vanta.util.game.events.EventListen;
import today.vanta.util.game.player.MovementUtil;
import today.vanta.util.game.render.RenderUtil;

import java.awt.*;

public class Crosshair extends Module {
    public Crosshair() {
        super("Crosshair", "Looks like CSGO.", Category.HUD);
    }

    @EventListen
    public void onRender2D(Render2DEvent event) {
        if (mc.gameSettings.thirdPersonView == 1) {
            return;
        }
        if (mc.gameSettings.thirdPersonView == 2) {
            return;
        }
        float spacing = 3;
        Color color = Color.WHITE;
        float y = (float) mc.displayHeight / 4;
        float x = (float) mc.displayWidth / 4;
        if (MovementUtil.isMoving()) {
            spacing = 6;
        }
        RenderUtil.rectangle(x + spacing,y - 1,7,2,color);
        RenderUtil.rectangle(x - spacing - 7,y - 1,7,2,color);
        RenderUtil.rectangle(x - 1,y + spacing,2,7,color);
        RenderUtil.rectangle(x - 1,y - spacing - 7,2,7,color);
    }
}
