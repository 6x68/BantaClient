package today.vanta.client.module.impl.hud;

import net.minecraft.client.gui.GuiChat;
import org.lwjgl.input.Mouse;
import today.vanta.Vanta;
import today.vanta.client.event.impl.game.render.DrawScreenEvent;
import today.vanta.client.module.Category;
import today.vanta.client.module.Module;
import today.vanta.client.module.impl.client.Theme;
import today.vanta.client.processor.impl.TargetProcessor;
import today.vanta.client.setting.Setting;
import today.vanta.client.setting.impl.NumberSetting;
import today.vanta.util.game.events.EventListen;
import today.vanta.util.game.player.InventoryUtil;
import today.vanta.util.game.render.RenderUtil;
import today.vanta.util.game.render.font.CFonts;
import today.vanta.util.game.render.shape.impl.Rectangle;

import java.awt.*;

public class BlockCounter extends Module {
    private static final Color BACKGROUND = new Color(20, 20, 20, 200);

    private static final float WIDTH = 90;
    private static final float HEIHT = 40;

    private boolean dragging;
    private float dragX, dragY;

    private final NumberSetting
            x = Setting.of("X position", 20, 0, 2000),
            y = Setting.of("Y position", 70, 0, 2000);

    public BlockCounter() {
        super("BlockCounter", "Block information.", Category.HUD);
    }

    @EventListen
    private void onDrawScreen(DrawScreenEvent event) {
        if (mc.thePlayer == null) return;

        if (!(mc.currentScreen instanceof GuiChat) && !TargetProcessor.getInstance().scaffold.isEnabled()) {
            return;
        }

        if (InventoryUtil.getHotbarBlockCount() == 0) {
            return;
        }

        if (mc.currentScreen instanceof GuiChat) {
            handleDragging(event.mouseX, event.mouseY);
        } else if (mc.currentScreen != null) {
            return;
        } else if (!TargetProcessor.getInstance().scaffold.isEnabled()) {
            return;
        }

        draw();
    }

    private void handleDragging(float mouseX, float mouseY) {
        if (Mouse.isButtonDown(0)) {
            if (!dragging && RenderUtil.hovered(mouseX, mouseY, x.getValue().floatValue(), y.getValue().floatValue(), WIDTH, HEIHT)) {
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

    private void draw() {
        Color color = Vanta.instance.moduleStorage.getT(Theme.class).colors[0];
        int blocks = InventoryUtil.getHotbarBlockCount();

        float x = this.x.getValue().floatValue();
        float y = this.y.getValue().floatValue();

        Rectangle
                .create(x, y, WIDTH, HEIHT)
                .color(BACKGROUND)
                .draw();

        RenderUtil.renderScaledItem(InventoryUtil.getBestBlockStack(), x, y + 0.5f, 2.4f);

        CFonts.SFPT_SEMIBOLD_20.drawStringWithShadow("Blocks", x + 38, y + 4, color);
        CFonts.SFPT_SEMIBOLD_20.drawStringWithShadow(String.valueOf(blocks), x + 38, y + 15, Color.WHITE);

        if (dragging && mc.currentScreen instanceof GuiChat) {
            Rectangle
                    .create(x - 0.5, y - 0.5, WIDTH + 1, HEIHT + 1)
                    .outline(true)
                    .color(color)
                    .draw();
        }
    }
}