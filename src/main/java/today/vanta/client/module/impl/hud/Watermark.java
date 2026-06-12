package today.vanta.client.module.impl.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;
import today.vanta.Vanta;
import today.vanta.client.event.impl.game.render.Render2DEvent;
import today.vanta.client.module.Category;
import today.vanta.client.module.Module;
import today.vanta.client.module.impl.client.Theme;
import today.vanta.client.module.impl.hud.arraylist.BitMapRenderer;
import today.vanta.client.setting.Setting;
import today.vanta.client.setting.impl.BooleanSetting;
import today.vanta.client.setting.impl.StringSetting;
import today.vanta.util.client.IClient;
import today.vanta.util.game.events.EventListen;
import today.vanta.util.game.events.EventPriority;
import today.vanta.util.game.player.MovementUtil;
import today.vanta.util.game.render.font.CFonts;
import today.vanta.util.game.render.shape.impl.Rectangle;

import java.awt.*;
import java.util.Calendar;
import java.util.Formatter;

public class Watermark extends Module {

    private final StringSetting style = Setting.of("Style", "Vanta", "Vanta", "Compact", "Jello", "My Eyes", "Char", "Exhi"); //travis scott reference? // shut the fuck up!
    private final BooleanSetting mcfont = Setting.of("Standard Minecraft Font", true).hide(() -> !style.getValue().equals("Exhi"));

    public Watermark() {
        super("Watermark", "Draws a watermark of the client.", Category.HUD);
        hideFromArraylist = true;
        setEnabled(true);
    }

    @EventListen(priority = EventPriority.LOWEST)
    private void onRender(Render2DEvent event) {
        BitMapRenderer exhiFont = new BitMapRenderer(mc.exhiFontRendererObj);
        BitMapRenderer mcFont = new BitMapRenderer(mc.fontRendererObj);
        String firstCharacter = String.valueOf(IClient.CLIENT_NAME.charAt(0));
        float length = CFonts.SFPT_SEMIBOLD_42.getStringWidth(firstCharacter) - 1;
        String watermarkfull = IClient.CLIENT_NAME.substring(1);
        Color[] colors = Vanta.instance.moduleStorage.getT(Theme.class).colors;

        float x = 5;
        float y = 5;

        switch (style.getValue()) {
            case "Vanta":
                CFonts.SFPT_SEMIBOLD_42.drawStringWithShadow(IClient.CLIENT_NAME, x, y, colors[0]);
                CFonts.SFPT_MEDIUM_18.drawStringWithShadow(IClient.CLIENT_VERSION, x, y + 18 + 3, Color.WHITE);
                break;
            case "Compact":
                String text = "§fV§rA§fNTA" + " | Steve | §r120§f FPS | mc.hypixel.net";
                text = text.replace("Steve", mc.session.getUsername());
                text = text.replace("120", String.valueOf(Minecraft.getDebugFPS()));
                text = text.replace("mc.hypixel.net", mc.getCurrentServerData() == null ? "singleplayer" : mc.getCurrentServerData().serverIP);
                text = text.toLowerCase();

                float fontWidth = CFonts.SFPT_MEDIUM_18.getStringWidth(text);
                //float fontHeight = CFonts.SFPT_MEDIUM_18.getFontHeight();

                float boxWidth = fontWidth + 4 + 4;
                float boxHeight = 16;

                Rectangle
                        .create(x, y, boxWidth, boxHeight)
                        .color(new Color(0, 0, 0, 100))
                        .draw();

                Rectangle
                        .create(x, y - 1, boxWidth, 1)
                        .color(colors[0])
                        .draw();

                Rectangle
                        .create(x - 1, y - 1, 1, 1)
                        .color(colors[0])
                        .draw();

                Rectangle
                        .create(x + boxWidth, y - 1, 1, 1)
                        .color(colors[0])
                        .draw();

                Rectangle
                        .create(x - 1, y, 1, boxHeight + 1)
                        .color(colors[0])
                        .draw();

                Rectangle
                        .create(x + boxWidth, y, 1, boxHeight + 1)
                        .color(colors[0])
                        .draw();

                Rectangle
                        .create(x, y + boxHeight, boxWidth, 1)
                        .color(colors[0])
                        .draw();

                float textX = x + 4;

                CFonts.SFPT_MEDIUM_18.drawString(text, textX, y + 2.5f, colors[0]);
                break;
            case "Jello":
                CFonts.HN_REGULAR_48.drawString(IClient.CLIENT_NAME, x, y, new Color(255, 255, 255, 185));
                CFonts.HN_MEDIUM_24.drawString("Jello", x, y + CFonts.HN_REGULAR_48.getFontHeight() - 1, new Color(255, 255, 255, 185));
                break;
            case "My Eyes":
                CFonts.RK_REGULAR_50.drawHorizontalGradientString("Abdelrahman Al-Rashid Client (Miniblox Bypass Edition)", x, y, colors[0], colors[1], 1, 150);
                break;
            case "Char":
                CFonts.SFPT_SEMIBOLD_42.drawStringWithShadow(firstCharacter, x, y, colors[0]);
                CFonts.SFPT_SEMIBOLD_42.drawStringWithShadow(watermarkfull, x + length, y, Color.WHITE);
                break;
            case "Exhi":
                Formatter format = new Formatter();
                Calendar gfg_calender = Calendar.getInstance();
                format.format("%tl:%tM %Tp", gfg_calender,
                        gfg_calender, gfg_calender);
                if (mcfont.getValue()) {
                    mcFont.drawString(firstCharacter, 2, 2, colors[0], true);
                    mcFont.drawString(watermarkfull + EnumChatFormatting.GRAY + " [" + EnumChatFormatting.WHITE + format + EnumChatFormatting.GRAY + "] " + "[" + EnumChatFormatting.WHITE + mc.getDebugFPS() + " FPS" + EnumChatFormatting.GRAY + "]", 8, 2, Color.WHITE, true);
                } else {
                    exhiFont.drawString(firstCharacter, 2, 2, colors[0], true);
                    exhiFont.drawString(watermarkfull + EnumChatFormatting.GRAY + " [" + EnumChatFormatting.WHITE + format + EnumChatFormatting.GRAY + "] " + "[" + EnumChatFormatting.WHITE + mc.getDebugFPS() + " FPS" + EnumChatFormatting.GRAY + "]", 9, 2, Color.WHITE, true);
                    exhiFont.drawString("BPS: " + MovementUtil.getBPS(), 2, 12, Color.WHITE, true);
                }
                break;
        }
    }

    //private static Color epstein = new Color(0, 0, 0, 255);
    //private float tick;

    /*
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
     */
}