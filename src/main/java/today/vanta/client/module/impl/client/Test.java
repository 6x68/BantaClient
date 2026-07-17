package today.vanta.client.module.impl.client;

import today.vanta.Vanta;
import today.vanta.client.event.impl.client.RenderOverlayEvent;
import today.vanta.client.module.Category;
import today.vanta.client.module.Module;
import today.vanta.client.processor.impl.TargetProcessor;
import today.vanta.client.setting.Setting;
import today.vanta.client.setting.impl.NumberSetting;
import today.vanta.util.game.events.EventListen;
import today.vanta.util.game.render.font.CFont;
import today.vanta.util.game.render.font.CFonts;
import today.vanta.util.game.render.shape.impl.Rectangle;
import today.vanta.util.game.render.shape.impl.Triangle;

import java.awt.*;

public class Test extends Module {
    private final NumberSetting
            x = Setting.of("X position", 20, 0, 2000),
            y = Setting.of("Y position", 20, 0, 2000);
    private float WIDTH = 100f;
    private float HEIGHT = 40f;

    public Test() {
        super("Test", "Test module for developers.", Category.CLIENT);
        hideFromArraylist = true;
    }

    @EventListen
    private void onRenderOverlay(RenderOverlayEvent event) {
        TargetProcessor targetProcessor = TargetProcessor.getInstance();
        float addition = 0;
        for (int i = 0; i < targetProcessor.playerlist.size(); i++) {
            Rectangle
                    .create(x.getValue().floatValue() + addition, y.getValue().floatValue(),WIDTH,HEIGHT)
                    .color(new Color(10,10,10,150))
                    .push(event);
            addition += WIDTH + 2;
        }

        CFonts.SFPT_MEDIUM_24.drawStringWithShadow(String.valueOf(mc.timer.timerSpeed), event.scaledResolution.getScaledWidth() / 2 - (CFonts.SFPT_REGULAR_24.getStringWidth(String.valueOf(mc.timer.timerSpeed))),event.scaledResolution.getScaledHeight() / 2 - (CFonts.SFPT_REGULAR_24.getFontHeight() / 2) - 2.5f,Color.WHITE);
    }
}
