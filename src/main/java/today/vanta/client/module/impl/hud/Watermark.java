package today.vanta.client.module.impl.hud;

import today.vanta.Vanta;
import today.vanta.client.event.impl.game.render.Render2DEvent;
import today.vanta.client.event.impl.game.world.UpdateEvent;
import today.vanta.client.module.Category;
import today.vanta.client.module.Module;
import today.vanta.client.module.impl.client.Theme;
import today.vanta.client.setting.impl.StringSetting;
import today.vanta.util.client.IClient;
import today.vanta.util.game.events.EventListen;
import today.vanta.util.game.events.EventPriority;
import today.vanta.util.game.render.font.CFonts;

import java.awt.*;

public class Watermark extends Module {
    private static Color epstein = new Color(0, 0, 0, 255);
    private float tick;

    private final StringSetting style = StringSetting.builder()
            .name("Style")
            .value("Vanta")
            .values("Vanta", "Jello", "My Eyes")
            .build();

    public Watermark() {
        super("Watermark", "Draws a watermark of the client.", Category.HUD);
        hideFromArraylist = true;
        setEnabled(true);
    }

    @EventListen(priority = EventPriority.LOWEST)
    private void onRender(Render2DEvent event) {
        switch (style.getValue()) {
            case "Vanta":
                CFonts.SFPT_SEMIBOLD_42.drawStringWithShadow(IClient.CLIENT_NAME, 5, -3 + 3, Vanta.instance.moduleStorage.getT(Theme.class).colors[0]);
                CFonts.SFPT_MEDIUM_18.drawStringWithShadow(IClient.CLIENT_VERSION, 5, 18 + 3, Color.WHITE);
                break;
            case "Jello":
                CFonts.HN_REGULAR_48.drawString(IClient.CLIENT_NAME, 5, 5, new Color(255, 255, 255, 185));
                CFonts.HN_MEDIUM_24.drawString("Jello", 5, 5 + CFonts.HN_REGULAR_48.getFontHeight() - 1, new Color(255, 255, 255, 185));
                break;
            case "My Eyes":
                CFonts.ARABICLOOKINGFONT.drawStringWithShadow("Abdelrahman Al-Rashid Client (Miniblox Bypass Edition)", 5, 0, epstein);
                break;
        }
    }

    @EventListen
    private void onUpdate(UpdateEvent event) {
        tick++;

        if (tick == 1) {
            epstein = Color.WHITE;
        }

        if (tick == 3) {
            epstein = Color.GREEN;
        }

        if (tick == 5) {
            epstein = Color.PINK;
        }

        if (tick == 7) {
            epstein = Color.CYAN;
        }

        if (tick == 10) {
            epstein = Color.LIGHT_GRAY;
        }

        if (tick == 12) {
            epstein = Color.BLACK;
        }

        if (tick == 14) {
            epstein = Color.MAGENTA;
        }
        if (tick == 16) {
            epstein = Color.ORANGE;
        }

        if (tick == 18) {
            epstein = Color.RED;
        }

        if (tick == 20) {
            epstein = Color.yellow;
        }

        if (tick == 22) {
            tick = 1;
        }
    }
}