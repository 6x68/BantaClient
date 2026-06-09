package today.vanta.client.module.impl.hud;

import net.minecraft.client.gui.GuiChat;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
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
import today.vanta.util.game.render.RenderUtil;
import today.vanta.util.game.render.font.CFonts;

import java.awt.*;

public class TargetHUD extends Module {
    private EntityLivingBase localTarget;

    private static final Color BACKGROUND = new Color(20, 20, 20, 200);

    private static final float WIDTH = 130;
    private static final float HEIHT = 40;

    private boolean dragging;
    private float dragX, dragY;

    private final NumberSetting
            x = Setting.of("X position", 20, 0, 2000),
            y = Setting.of("Y position", 20, 0, 2000);

    public TargetHUD() {
        super("TargetHUD", "Target information.", Category.HUD);
    }

    @EventListen
    private void onDrawScreen(DrawScreenEvent event) {
        if (mc.thePlayer == null) return;

        if (!(mc.currentScreen instanceof GuiChat) && TargetProcessor.getInstance().target == null) {
            return;
        }

        localTarget = null;

        if (TargetProcessor.getInstance().target == null && mc.currentScreen instanceof GuiChat) {
            localTarget = mc.thePlayer;
        } else if (TargetProcessor.getInstance().target instanceof EntityPlayer) {
            localTarget = TargetProcessor.getInstance().target;
        }

        if (localTarget == null) return;

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
        float healthWidth = WIDTH * (localTarget.getHealth() / localTarget.getMaxHealth());
        Color color = Vanta.instance.moduleStorage.getT(Theme.class).colors[0];

        float x = this.x.getValue().floatValue();
        float y = this.y.getValue().floatValue();

        RenderUtil.rectangle(x, y, WIDTH, HEIHT, BACKGROUND);
        RenderUtil.player_head((EntityPlayer) localTarget, x, y, 36f);
        CFonts.SFPT_SEMIBOLD_20.drawStringWithShadow(localTarget.getName(), x + 38, y + 4,color );
        CFonts.SFPT_SEMIBOLD_20.drawStringWithShadow(String.format("%.1f", localTarget.getHealth()), x + 38, y + 15, Color.WHITE);
        RenderUtil.rectangle(x, y + 36, healthWidth, 4f, color);

        if (dragging) {
            RenderUtil.rectangle(x - 0.5, y - 0.5, WIDTH + 1, HEIHT + 1, false, color);
        }
    }
}