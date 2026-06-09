package today.vanta.client.module.impl.hud;

import net.minecraft.client.gui.GuiChat;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import today.vanta.Vanta;
import today.vanta.client.event.impl.game.render.DrawScreenEvent;
import today.vanta.client.module.Category;
import today.vanta.client.module.Module;
import today.vanta.client.module.impl.client.Theme;
import today.vanta.client.processor.impl.TargetProcessor;
import today.vanta.client.setting.impl.NumberSetting;
import today.vanta.util.game.events.EventListen;
import today.vanta.util.game.player.InventoryUtil;
import today.vanta.util.game.render.RenderUtil;
import today.vanta.util.game.render.font.CFonts;

import java.awt.*;

public class BlockCounter extends Module {
    private static final Color BACKGROUND = new Color(20, 20, 20, 200);

    private static final float WIDTH = 90;
    private static final float HEIHT = 40;

    private boolean dragging;
    private float dragX, dragY;

    private final NumberSetting
            x = NumberSetting.of("X position", 20, 0, 2000),
            y = NumberSetting.of("Y position", 70, 0, 2000);

    public BlockCounter() {
        super("BlockCounter", "Block information.", Category.HUD);
    }

    @EventListen
    private void onDrawScreen(DrawScreenEvent event) {
        if (mc.thePlayer == null) return;

        if (!(mc.currentScreen instanceof GuiChat) && !TargetProcessor.getInstance().scaffold.isEnabled()) {
            return;
        }

        ItemStack heldItem = mc.thePlayer.getCurrentEquippedItem();

        if (heldItem == null || !(heldItem.getItem() instanceof ItemBlock)) {
            return;
        }

        if (mc.currentScreen instanceof GuiChat) {
            handleDragging(event.mouseX, event.mouseY);
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
        int blocksinHotbar = InventoryUtil.getHotbarBlockCount();

        float x = this.x.getValue().floatValue();
        float y = this.y.getValue().floatValue();

        RenderUtil.rectangle(x, y, WIDTH, HEIHT, BACKGROUND);
        double scale = 2.4;

        double itemX = x;
        double itemY = y - 0.5;

        GL11.glPushMatrix();
        GL11.glTranslated(itemX, itemY, 0);
        GL11.glScaled(scale, scale, 1.0);
        GL11.glTranslated(-itemX, -itemY, 0);
        mc.renderItem.renderItemIntoGUIFullBright(mc.thePlayer.getCurrentEquippedItem(), (int) x, (int) y);
        GL11.glPopMatrix();

        CFonts.SFPT_SEMIBOLD_20.drawStringWithShadow("Blocks", x + 38, y + 4, color);
        CFonts.SFPT_SEMIBOLD_20.drawStringWithShadow(String.valueOf(blocksinHotbar), x + 38, y + 15, Color.WHITE);

        if (dragging) {
            RenderUtil.rectangle(x - 0.5, y - 0.5, WIDTH + 1, HEIHT + 1, false, color);
        }
    }
}