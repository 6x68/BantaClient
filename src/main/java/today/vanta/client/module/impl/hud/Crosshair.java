package today.vanta.client.module.impl.hud;

import today.vanta.client.event.impl.game.render.Render2DEvent;
import today.vanta.client.module.Category;
import today.vanta.client.module.Module;
import today.vanta.client.setting.Setting;
import today.vanta.client.setting.impl.NumberSetting;
import today.vanta.util.game.events.EventListen;
import today.vanta.util.game.player.MovementUtil;
import today.vanta.util.game.render.RenderUtil;

import java.awt.*;

public class Crosshair extends Module {
    private final NumberSetting length = Setting.of("Length", 7,4,10,0);
    private final NumberSetting space = Setting.of("Static Space", 5,1,15);
    private final NumberSetting spacemove = Setting.of("Moving Space", 7,2,16);
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
        float spacing = space.getValue().intValue();
        Color color = Color.WHITE;
        float y = (float) mc.displayHeight / 4;
        float x = (float) mc.displayWidth / 4;
        if (MovementUtil.isMoving()) {
            spacing = spacemove.getValue().intValue();
        }
        RenderUtil.rectangle(x + spacing ,y - 0.5f,length.getValue().doubleValue(),1,color);
        RenderUtil.rectangle(x - spacing - length.getValue().doubleValue(),y - 0.5f,length.getValue().doubleValue(),1,color);
        RenderUtil.rectangle(x - 0.5f,y + spacing,1,length.getValue().doubleValue(),color);
        RenderUtil.rectangle(x - 0.5f,y - spacing - length.getValue().doubleValue(),1,length.getValue().doubleValue(),color);
    }
}
