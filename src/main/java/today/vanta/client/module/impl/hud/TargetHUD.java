package today.vanta.client.module.impl.hud;

import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import org.lwjgl.input.Mouse;
import today.vanta.Vanta;
import today.vanta.client.event.impl.game.render.DrawScreenEvent;
import today.vanta.client.module.Category;
import today.vanta.client.module.Module;
import today.vanta.client.module.impl.client.Theme;
import today.vanta.client.processor.impl.TargetProcessor;
import today.vanta.client.setting.Setting;
import today.vanta.client.setting.impl.NumberSetting;
import today.vanta.client.setting.impl.StringSetting;
import today.vanta.util.game.events.EventListen;
import today.vanta.util.game.player.ChatUtil;
import today.vanta.util.game.player.InventoryUtil;
import today.vanta.util.game.render.RenderUtil;
import today.vanta.util.game.render.font.CFonts;
import today.vanta.util.game.render.shape.impl.GradientRectangle;
import today.vanta.util.game.render.shape.impl.Rectangle;
import today.vanta.util.system.math.animation.Animation;
import today.vanta.util.system.math.animation.Easing;

import java.awt.*;
import java.util.Arrays;

public class TargetHUD extends Module {
    private static final Color BACKGROUND = new Color(20, 20, 20, 200);
    private static final Color DARKER_BACKGROUND = new Color(20, 20, 20, 255);

    private EntityLivingBase localTarget;
    private String oldTarget;
    private boolean can;

    private float width = 130;
    private float height = 40;

    private boolean dragging;
    private float dragX, dragY;

    private final StringSetting mode = Setting.of("Mode", "Vanta", "Classic", "Vanta", "Adjust");
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
        } else if (mc.currentScreen != null) {
            return;
        }

        draw();
    }

    private void handleDragging(float mouseX, float mouseY) {
        if (Mouse.isButtonDown(0)) {
            if (!dragging && RenderUtil.hovered(mouseX, mouseY, x.getValue().floatValue(), y.getValue().floatValue(), width, height)) {
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

    private float barWidth = width;
    private float ghostBarWidth = width;
    private float targetWidth = width;
    private float targetWidth2 = width;
    private float adtargetWidth = width - 4f;
    private float adtargetWidth2 = width - 4f;
    private float adghostBarWidth = width - 4f;
    private float adbarWidth = width - 4f;

    private Animation animation;
    private Animation ghostAnimation;

    private void draw() {
        float x = this.x.getValue().floatValue();
        float y = this.y.getValue().floatValue();

        Color color = Vanta.instance.moduleStorage.getT(Theme.class).colors[0];
        Color color2 = Vanta.instance.moduleStorage.getT(Theme.class).colors[1];

        switch (mode.getValue()) {
            case "Vanta":
                width = 130;
                height = 40;

                Rectangle
                        .create(x, y, width, height)
                        .color(BACKGROUND)
                        .draw();

                RenderUtil.renderHead((EntityPlayer) localTarget, x, y, 36f);
                CFonts.SFPT_MEDIUM_20.drawStringWithShadow(localTarget.getName(), x + 38, y + 4, Color.WHITE);
                CFonts.SFPT_REGULAR_18.drawStringWithShadow(String.format("%.1f", localTarget.getHealth()), x + 38, y + 15, Color.WHITE);

                float healthWidth = width * (localTarget.getHealth() / localTarget.getMaxHealth());
                float ghostWidth = width * (localTarget.getHealth() / localTarget.getMaxHealth());

                if (healthWidth != targetWidth) {
                    targetWidth = healthWidth;
                    if (oldTarget != localTarget.getName()) {
                        barWidth = targetWidth;
                        can = true;
                        oldTarget = localTarget.getName();
                        return;
                    }

                    animation = Animation.create(
                            barWidth,
                            targetWidth,
                            150,
                            Easing.LINEAR,
                            val -> barWidth = val
                    );

                    animation.start();
                }
                if (ghostWidth != targetWidth2) {
                    targetWidth2 = ghostWidth;

                    if (can) {
                        ghostBarWidth = targetWidth2;
                        oldTarget = localTarget.getName();
                        can = false;
                        return;
                    }

                    ghostAnimation = Animation.create(
                            ghostBarWidth,
                            targetWidth2,
                            450,
                            Easing.LINEAR,
                            val -> ghostBarWidth = val
                    );

                    ghostAnimation.start();
                }

                Rectangle
                        .create(x, y + 36, width - 2.5f, 4f)
                        .color(DARKER_BACKGROUND)
                        .draw();

                Rectangle
                        .create(x, y + 36, ghostBarWidth, 4f)
                        .color(color.darker())
                        .draw();

                GradientRectangle
                        .create(x, y + 36, barWidth, 4f)
                        .firstColor(color2)
                        .secondColor(color)
                        .draw();
                break;

            case "Classic":
                width = 150f;
                height = 55f;

                float healthbarwidth = 100f;
                float whatever = (localTarget.getHealth() / localTarget.getMaxHealth());
                float healthbar = healthbarwidth * whatever;

                String health_str = String.format("%.1f", localTarget.getHealth());
                Color healthbarcol = new Color(48, 246, 6);

                if (whatever < 0.5 && whatever > 0.33) {
                    healthbarcol = new Color(221, 244, 2);
                }

                if (whatever <= 0.33) {
                    healthbarcol = new Color(250, 42, 68);
                }

                Rectangle
                        .create(x, y, width, height)
                        .color(BACKGROUND)
                        .draw();

                RenderUtil.renderEntity((int) x + 15, (int) y + 52, 25, -30, 0, localTarget);

                Rectangle
                        .create(x + 37, y - (10f / 2) + (height / 2) + 10, healthbarwidth, 10f)
                        .color(DARKER_BACKGROUND)
                        .draw();

                Rectangle
                        .create(x + 37, y - (10f / 2) + (height / 2) + 10, healthbar, 10f)
                        .color(healthbarcol)
                        .draw();

                mc.fontRendererObj.drawStringWithShadow(health_str + " ❤", x + 37, y + 16, Color.WHITE);
                mc.fontRendererObj.drawStringWithShadow(localTarget.getName(), x + 37, y + 2, Color.WHITE);
                break;
            case "Adjust":
                width = 100;
                height = 30;
                float barrrrwidth = width - 4f;

                float adhealthWidth = barrrrwidth * (localTarget.getHealth() / localTarget.getMaxHealth());
                float adghostWidth = barrrrwidth * (localTarget.getHealth() / localTarget.getMaxHealth());

                if (adhealthWidth != adtargetWidth) {
                    adtargetWidth = adhealthWidth;
                    if (oldTarget != localTarget.getName()) {
                        adbarWidth = adtargetWidth;
                        can = true;
                        oldTarget = localTarget.getName();
                        return;
                    }

                    animation = Animation.create(
                            adbarWidth,
                            adtargetWidth,
                            150,
                            Easing.LINEAR,
                            val -> adbarWidth = val
                    );

                    animation.start();
                }
                if (adghostWidth != adtargetWidth2) {
                    adtargetWidth2 = adghostWidth;

                    if (can) {
                        adghostBarWidth = adtargetWidth2;
                        oldTarget = localTarget.getName();
                        can = false;
                        return;
                    }

                    ghostAnimation = Animation.create(
                            adghostBarWidth,
                            adtargetWidth2,
                            450,
                            Easing.LINEAR,
                            val -> adghostBarWidth = val
                    );

                    ghostAnimation.start();
                }

                float space = 24.5f;
                float length = CFonts.getFont("T-Regular", 14).getStringWidth(String.format("%.1f", mc.thePlayer.getHealth() - localTarget.getHealth()));

                Rectangle
                        .create(x, y, width, height)
                        .color(BACKGROUND)
                        .draw();

                RenderUtil.renderHead((EntityPlayer) localTarget, x + 2, y + 2, 20f);
                CFonts.getFont("T-Regular", 16).drawStringWithShadow(localTarget.getName(), x + 24, y + 1, Color.WHITE);

                Rectangle
                        .create(x + 2, y + space, width - 4, 3f)
                        .color(DARKER_BACKGROUND)
                        .draw();

                Rectangle
                        .create(x + 2, y + space, adghostBarWidth, 3f)
                        .color(color.darker())
                        .draw();

                Rectangle
                        .create(x + 2, y + space, adbarWidth, 3f)
                        .color(color)
                        .draw();

                float itemX = x + 10 + 2;
                float itemY = y + 10;

                ItemStack currentItem = ((EntityPlayer) localTarget).inventory.getCurrentItem();
                if (currentItem != null) {
                    itemX += 10 + 1;
                    renderScaledItem(currentItem, (int) itemX + 1, (int) itemY + 1, 0.65f);
                }

                ItemStack slot3 = ((EntityPlayer) localTarget).inventory.armorItemInSlot(3);
                if (slot3 != null) {
                    itemX += 10 + 1;
                    renderScaledItem(slot3, (int) itemX, (int) itemY, 0.75f);
                }

                ItemStack slot2 = ((EntityPlayer) localTarget).inventory.armorItemInSlot(2);
                if (slot2 != null) {
                    itemX += 10 + 1;
                    renderScaledItem(slot2, (int) itemX, (int) itemY, 0.75f);
                }

                ItemStack slot1 = ((EntityPlayer) localTarget).inventory.armorItemInSlot(1);
                if (slot1 != null) {
                    itemX += 10 + 1;
                    renderScaledItem(slot1, (int) itemX, (int) itemY, 0.75f);
                }

                ItemStack slot0 = ((EntityPlayer) localTarget).inventory.armorItemInSlot(0);
                if (slot0 != null) {
                    itemX += 10 + 1;
                    renderScaledItem(slot0, (int) itemX, (int) itemY, 0.75f);
                }

                CFonts.getFont("T-Regular", 14).drawStringWithShadow(String.format("%.1f", mc.thePlayer.getHealth() - localTarget.getHealth()), x + width - (length) - 4, y + 15, Color.WHITE);
                break;
        }

        if (dragging && mc.currentScreen instanceof GuiChat) {
            Rectangle
                    .create(x - 0.5, y - 0.5, width + 1, height + 1)
                    .color(color)
                    .outline(true)
                    .draw();
        }
    }

    private void renderScaledItem(ItemStack stack, float x, float y, float scale) {
        if (stack == null) return;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 0);
        GlStateManager.scale(scale, scale, scale);

        mc.renderItem.renderItemIntoGUIFullBright(stack, 0, 0);

        GlStateManager.popMatrix();
    }
}