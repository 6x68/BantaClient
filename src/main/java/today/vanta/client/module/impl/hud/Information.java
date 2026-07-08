package today.vanta.client.module.impl.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import org.lwjgl.input.Mouse;
import org.w3c.dom.css.Rect;
import today.vanta.Vanta;
import today.vanta.client.event.impl.client.RenderOverlayEvent;
import today.vanta.client.event.impl.client.RenderScreenEvent;
import today.vanta.client.module.Category;
import today.vanta.client.module.Module;
import today.vanta.client.module.impl.client.Theme;
import today.vanta.client.setting.Setting;
import today.vanta.client.setting.impl.NumberSetting;
import today.vanta.client.setting.impl.StringSetting;
import today.vanta.util.game.events.EventListen;
import today.vanta.util.game.player.MovementUtil;
import today.vanta.util.game.player.PlayerUtil;
import today.vanta.util.game.render.RenderUtil;
import today.vanta.util.game.render.Renderable;
import today.vanta.util.game.render.font.CFonts;
import today.vanta.util.game.render.shape.impl.GradientRectangle;
import today.vanta.util.game.render.shape.impl.Rectangle;
import today.vanta.util.system.math.Counter;
import today.vanta.util.system.math.MathUtil;

import java.awt.*;

public class Information extends Module {
    private final StringSetting mode = Setting.of("Mode", "Text", "Window", "Text");
    private final NumberSetting
            x = Setting.of("X position", 20, 0, 2000),
            y = Setting.of("Y position", 20, 0, 2000);
    private float WIDTH = 75;
    private float HEIGHT = 50;
    private boolean dragging;
    private float dragX, dragY;
    private Color color1 = Vanta.instance.moduleStorage.getT(Theme.class).colors[0];
    private Color color2 = Vanta.instance.moduleStorage.getT(Theme.class).colors[1];
    private static final Color WINDOWBG = new Color(20, 20, 20, 220);
    private static final Color BACKGROUND = new Color(20, 20, 20, 190);
    private Counter playTime = new Counter();
    private String oldServer;
    private float totalHeight;
    private float totalWidth;
    private float outlineWidth;
    public Information() {
        super("Information", "Provides information on the player.", Category.HUD);
    }

    private void handleDragging(float mouseX, float mouseY) {
        if (mode.getValue().equals("Text")) return;
        if (Mouse.isButtonDown(0)) {
            if (!dragging && RenderUtil.hovered(mouseX, mouseY, x.getValue().floatValue(), y.getValue().floatValue(), WIDTH, HEIGHT)) {
                dragging = true;
                dragX = mouseX - x.getValue().floatValue();
                dragY = mouseY - y.getValue().floatValue();
            }

            if (dragging) {
                x.setValue(mouseX - dragX);
                y.setValue(mouseY - dragY);
            }
        } else {
            dragging = false;
        }
    }

    @EventListen
    public void onDrawScreen(RenderScreenEvent event) {
        if (mc.currentScreen instanceof GuiChat) {
            handleDragging(event.mouseX, event.mouseY);
        }
    }

    @EventListen
    public void onRender2D(RenderOverlayEvent event) {
        color1 = Vanta.instance.moduleStorage.getT(Theme.class).colors[0];
        color2 = Vanta.instance.moduleStorage.getT(Theme.class).colors[1];
        if (!mc.isSingleplayer() && mc.getCurrentServerData().serverIP != null) {
            if (oldServer != mc.getCurrentServerData().serverIP) {
                playTime.reset();
                oldServer = mc.getCurrentServerData().serverIP;

            }
        }
        switch (mode.getValue()) {
            case "Text":
                totalHeight = HEIGHT;
                totalWidth = WIDTH;
                outlineWidth = 0;
                float ydraw = event.scaledResolution.getScaledHeight() - 19;
                if (mc.currentScreen instanceof GuiChat) {
                    ydraw = event.scaledResolution.getScaledHeight() - 230;
                }
                mc.exhiFontRendererObj.drawString("Ping: " + PlayerUtil.getPing(mc.thePlayer), 2, ydraw, Color.WHITE,true);
                mc.exhiFontRendererObj.drawString("BPS: " + MovementUtil.getBPS(), 2, ydraw + 10, Color.WHITE,true);
                break;
            case "Window":
                WIDTH = 145;
                HEIGHT = 51;
                totalHeight = RenderUtil.getTotalWindowHeight(HEIGHT);
                totalWidth = RenderUtil.getTotalWindowWidth(WIDTH);
                outlineWidth = RenderUtil.getOutlineWidth();
                RenderUtil.drawWindowRectangle(event,"Information",x.getValue().floatValue(),y.getValue().floatValue(),WIDTH,HEIGHT);


//                Rectangle
//                        .create(x.getValue().floatValue(),y.getValue().floatValue(),WIDTH,12)
//                        .color(WINDOWBG)
//                        .push(event);
//                CFonts.SFPT_REGULAR_18.drawStringWithShadow("Information", x.getValue().floatValue() + 1, y.getValue().floatValue(), Color.white);
//                Rectangle
//                        .create(x.getValue().floatValue(),y.getValue().floatValue() + 12,WIDTH,HEIGHT - 12)
//                        .color(BACKGROUND)
//                        .push(event);

                RenderUtil.renderHead(event,mc.thePlayer,x.getValue().floatValue() + 2,y.getValue().floatValue() + 14,47);
                CFonts.SFPT_REGULAR_18.drawStringWithShadow(mc.thePlayer.getName(), x.getValue().floatValue() + 51, y.getValue().floatValue() + 11, Color.WHITE);
                CFonts.SFPT_REGULAR_18.drawStringWithShadow("FPS: "+ Minecraft.getDebugFPS(), x.getValue().floatValue() + 51, y.getValue().floatValue() + 21, Color.WHITE);
                CFonts.SFPT_REGULAR_18.drawStringWithShadow("BPS: "+ MovementUtil.getBPS(), x.getValue().floatValue() + 51, y.getValue().floatValue() + 31, Color.WHITE);
                CFonts.SFPT_REGULAR_18.drawStringWithShadow("Ping: "+ PlayerUtil.getPing(mc.thePlayer), x.getValue().floatValue() + 51, y.getValue().floatValue() + 41, Color.WHITE);
                CFonts.SFPT_REGULAR_18.drawStringWithShadow("Session: " + MathUtil.formatDuration(playTime.getElapsedTime()), x.getValue().floatValue() + 51, y.getValue().floatValue() + 51, Color.WHITE);

                break;

        }

        if (dragging && mc.currentScreen instanceof GuiChat) {
                Rectangle
                        .create(x.getValue().floatValue() - outlineWidth - 0.5f, y.getValue().floatValue() - outlineWidth -  0.5f, totalWidth + 1, totalHeight + 1)
                        .color(color1)
                        .outline(true)
                        .push(event);
        }
    }
}
