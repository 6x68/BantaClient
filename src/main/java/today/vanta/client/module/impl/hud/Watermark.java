package today.vanta.client.module.impl.hud;

import net.minecraft.util.EnumChatFormatting;
import today.vanta.Vanta;
import today.vanta.client.event.impl.client.RenderOverlayEvent;
import today.vanta.client.module.Category;
import today.vanta.client.module.Module;
import today.vanta.client.module.impl.client.Theme;
import today.vanta.client.setting.Setting;
import today.vanta.client.setting.impl.BooleanSetting;
import today.vanta.client.setting.impl.StringSetting;
import today.vanta.util.client.IClient;
import today.vanta.util.game.events.EventListen;
import today.vanta.util.game.events.EventPriority;
import today.vanta.util.game.render.ImageUtil;
import today.vanta.util.game.render.font.CFonts;
import today.vanta.util.game.render.shape.impl.GradientRectangle;
import today.vanta.util.game.render.shape.impl.ImageRectangle;
import today.vanta.util.game.render.shape.impl.Rectangle;

import java.awt.*;
import java.util.Calendar;
import java.util.Formatter;

public class Watermark extends Module {
    private final StringSetting style = Setting.of("Style", "Vanta", "Vanta", "Jello", "Char", "Exhi", "Adjust" , "Vestige", "Fanta");
    private final BooleanSetting mcfont = Setting.of("Vanilla font", true).hide(() -> !style.getValue().equals("Exhi"));
    private static final Color BACKGROUND = new Color(20, 20, 20, 190);

    public Watermark() {
        super("Watermark", "Draws a watermark of the client.", Category.HUD);
        hideFromArraylist = true;
        setEnabled(true);
    }

    @EventListen(priority = EventPriority.LOWEST)
    private void onRender(RenderOverlayEvent event) {
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
                }
                break;

            case "Adjust":
                CFonts.getFont("T-Regular", 20).drawStringWithShadow("§r" + firstChar + "§f" + watermarkText, 2, 2, colors[0]);
                break;
            case "Vestige":
                float length = CFonts.SFPT_MEDIUM_24.getStringWidth(IClient.CLIENT_NAME + " v"+ IClient.CLIENT_VERSION);
                GradientRectangle
                        .create(2,2,length + 2,2)
                        .firstColor(colors[0])
                        .secondColor(colors[1])
                        .push(event);
                Rectangle
                        .create(2,4,length + 2,14)
                        .color(BACKGROUND)
                        .push(event);

                CFonts.SFPT_MEDIUM_24.drawString(IClient.CLIENT_NAME + " v"+ IClient.CLIENT_VERSION, 2,4,Color.WHITE,false);
                break;
            case "Fanta":
                ImageRectangle
                        .create(2, 2 , 100, 100, -1)
                        .resource(ImageUtil.getTexture("fanta.png"))
                        .push(event);
                break;
        }
    }
}