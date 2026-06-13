package today.vanta.client.module.impl.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;
import today.vanta.Vanta;
import today.vanta.client.event.impl.game.render.Render2DEvent;
import today.vanta.client.module.Category;
import today.vanta.client.module.Module;
import today.vanta.client.module.impl.client.Theme;
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
    private final StringSetting style = Setting.of("Style", "Vanta", "Vanta", "Compact", "Jello", "Char", "Exhi", "Adjust"); //travis scott reference? // shut the fuck up!
    private final BooleanSetting mcfont = Setting.of("Vanilla font", true).hide(() -> !style.getValue().equals("Exhi"));

    public Watermark() {
        super("Watermark", "Draws a watermark of the client.", Category.HUD);
        hideFromArraylist = true;
        setEnabled(true);
    }

    @EventListen(priority = EventPriority.LOWEST)
    private void onRender(Render2DEvent event) {
        String firstChar = String.valueOf(IClient.CLIENT_NAME.charAt(0));
        float firstCharWidth = CFonts.SFPT_SEMIBOLD_42.getStringWidth(firstChar) - 1;
        String watermarkText = IClient.CLIENT_NAME.substring(1);

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

            case "Char":
                CFonts.SFPT_SEMIBOLD_42.drawStringWithShadow(firstChar, x, y, colors[0]);
                CFonts.SFPT_SEMIBOLD_42.drawStringWithShadow(watermarkText, x + firstCharWidth, y, Color.WHITE);
                break;

            case "Exhi":
                Formatter format = new Formatter();
                Calendar gfg_calender = Calendar.getInstance();
                format.format("%tl:%tM %Tp", gfg_calender,
                        gfg_calender, gfg_calender);

                if (mcfont.getValue()) {
                    mc.fontRendererObj.drawString(firstChar, 2, 2, colors[0], true);
                    mc.fontRendererObj.drawString(watermarkText + EnumChatFormatting.GRAY + " [" + EnumChatFormatting.WHITE + format + EnumChatFormatting.GRAY + "] " + "[" + EnumChatFormatting.WHITE + mc.getDebugFPS() + " FPS" + EnumChatFormatting.GRAY + "]", 8, 2, Color.WHITE, true);
                } else {
                    mc.exhiFontRendererObj.drawString(firstChar, 2, 2, colors[0], true);
                    mc.exhiFontRendererObj.drawString(watermarkText + EnumChatFormatting.GRAY + " [" + EnumChatFormatting.WHITE + format + EnumChatFormatting.GRAY + "] " + "[" + EnumChatFormatting.WHITE + mc.getDebugFPS() + " FPS" + EnumChatFormatting.GRAY + "]", 9, 2, Color.WHITE, true);
                    mc.exhiFontRendererObj.drawString("BPS: " + MovementUtil.getBPS(), 2, 12, Color.WHITE, true);
                }
                break;

            case "Adjust":
                CFonts.SFPT_MEDIUM_20.drawStringWithShadow("§r§l" + firstChar + "§f" + watermarkText, 2, 2, colors[0]);
                break;
        }
    }
}