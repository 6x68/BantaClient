package today.vanta.client.module.impl.hud;

import today.vanta.client.event.impl.game.render.Render2DEvent;
import today.vanta.client.event.impl.game.render.RenderCrosshairEvent;
import today.vanta.client.module.Category;
import today.vanta.client.module.Module;
import today.vanta.client.setting.Setting;
import today.vanta.client.setting.impl.NumberSetting;
import today.vanta.util.game.events.EventListen;
import today.vanta.util.game.player.MovementUtil;
import today.vanta.util.game.render.shape.impl.Rectangle;

import java.awt.*;

public class Crosshair extends Module {
    private final NumberSetting length = Setting.of("Length", 7, 4, 10, 0);
    private final NumberSetting width = Setting.of("Width", 0.5f, 0.5f, 2, 5);
    private final NumberSetting space = Setting.of("Static space", 5, 0, 15);
    private final NumberSetting spaceMove = Setting.of("Moving space", 7, 1, 16);

    public Crosshair() {
        super("Crosshair", "Looks like CSGO.", Category.HUD);
    }

    @EventListen
    private void onRenderCrosshair(RenderCrosshairEvent event) {
        event.cancelled = true;
    }

    @EventListen
    private void onRender2D(Render2DEvent event) {
        if (mc.gameSettings.thirdPersonView != 0) {
            return;
        }

        float spacing = space.getValue().floatValue();
        Color color = Color.WHITE;
        Color black = Color.BLACK;
        float y = (float) mc.displayHeight / 4;
        float x = (float) mc.displayWidth / 4;

        if (MovementUtil.isMoving()) {
            spacing = spaceMove.getValue().intValue();
        }

        Rectangle
                .create(x + spacing, y - 0.5f, length.getValue().doubleValue(), 1)
                .color(color)
                .draw();

        Rectangle
                .create(x - spacing - length.getValue().doubleValue(), y - 0.5f, length.getValue().doubleValue(), 1)
                .color(color)
                .draw();

        Rectangle
                .create(x - 0.5f, y + spacing, 1, length.getValue().doubleValue())
                .color(color)
                .draw();

        Rectangle
                .create(x - 0.5f, y - spacing - length.getValue().doubleValue(), 1, length.getValue().doubleValue())
                .color(color)
                .draw();
    }
}
