package today.vanta.client.module.impl.hud;

import today.vanta.Vanta;
import today.vanta.client.event.impl.game.render.Render2DEvent;
import today.vanta.client.event.impl.game.render.RenderCrosshairEvent;
import today.vanta.client.module.Category;
import today.vanta.client.module.Module;
import today.vanta.client.module.impl.client.Theme;
import today.vanta.client.setting.Setting;
import today.vanta.client.setting.impl.BooleanSetting;
import today.vanta.client.setting.impl.NumberSetting;
import today.vanta.client.setting.impl.StringSetting;
import today.vanta.util.game.events.EventListen;
import today.vanta.util.game.player.MovementUtil;
import today.vanta.util.game.render.shape.impl.Rectangle;
import today.vanta.util.system.math.animation.Animation;
import today.vanta.util.system.math.animation.Easing;

import java.awt.*;

public class Crosshair extends Module {
    private final NumberSetting length = Setting.of("Length", 7, 4, 10, 0);
    private final NumberSetting width = Setting.of("Width", 0.5f, 0.5f, 2, 1);
    private final NumberSetting space = Setting.of("Static space", 5, 0, 15);
    private final NumberSetting spaceMove = Setting.of("Moving space", 7, 1, 16);
    private final StringSetting colorMode = Setting.of("Main Crosshair Color", "White", "Theme", "White");
    private final BooleanSetting outline = Setting.of("Outline", true);

    Animation animation;

    public Crosshair() {
        super("Crosshair", "Looks like CSGO.", Category.HUD);
    }

    @EventListen
    private void onRenderCrosshair(RenderCrosshairEvent event) {
        event.cancelled = true;
    }

    float actualspacing = spaceMove.getValue().floatValue();
    float targetspacing = spaceMove.getValue().floatValue();
    @EventListen
    private void onRender2D(Render2DEvent event) {
        if (mc.gameSettings.thirdPersonView != 0) {
            return;
        }

        float spacing = space.getValue().floatValue();
        Color color = Color.WHITE;
        if (colorMode.getValue().equals("Theme")) {
            color = Vanta.instance.moduleStorage.getT(Theme.class).colors[0];
        } else {
            color = Color.WHITE;
        }
        Color black = Color.BLACK;
        float y = (float) event.scaledResolution.getScaledHeight() / 2;
        float x = (float) event.scaledResolution.getScaledWidth() / 2;
        float w = width.getValue().floatValue();
        float wout = w + 1;
        if (MovementUtil.isMoving()) {
            spacing = spaceMove.getValue().intValue();
        }
        if (spacing != targetspacing) {
            targetspacing = spacing;

            animation = Animation.create(
                    actualspacing,
                    targetspacing,
                    125,
                    Easing.EASE_IN_OUT_QUAD,
                    val -> actualspacing = val
            );

            animation.start();
        }



        double len = length.getValue().doubleValue();

// Outline

        if (outline.getValue()) {
            Rectangle
                    .create(x + actualspacing - 1, y - (w / 2) - 1, len + 2, w + 2)
                    .color(black)
                    .draw();

            Rectangle
                    .create(x - actualspacing - len - 1, y - (w / 2) - 1, len + 2, w + 2)
                    .color(black)
                    .draw();

            Rectangle
                    .create(x - (w / 2) - 1, y + actualspacing - 1, w + 2, len + 2)
                    .color(black)
                    .draw();

            Rectangle
                    .create(x - (w / 2) - 1, y - actualspacing - len - 1, w + 2, len + 2)
                    .color(black)
                    .draw();
        }
        // Main part

        Rectangle
                .create(x + actualspacing, y - (w / 2), len, w)
                .color(color)
                .draw();

        Rectangle
                .create(x - actualspacing - len, y - (w / 2), len, w)
                .color(color)
                .draw();

        Rectangle
                .create(x - (w / 2), y + actualspacing, w, len)
                .color(color)
                .draw();

        Rectangle
                .create(x - (w / 2), y - actualspacing - len, w, len)
                .color(color)
                .draw();
    }
}
