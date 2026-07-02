package today.vanta.client.module.impl.hud;

import net.minecraft.client.gui.GuiChat;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Mouse;
import today.vanta.Vanta;
import today.vanta.client.event.impl.client.RenderScreenEvent;
import today.vanta.client.module.Category;
import today.vanta.client.module.Module;
import today.vanta.client.module.impl.client.Theme;
import today.vanta.client.processor.impl.TargetProcessor;
import today.vanta.client.setting.Setting;
import today.vanta.client.setting.impl.NumberSetting;
import today.vanta.client.setting.impl.StringSetting;
import today.vanta.util.game.events.EventListen;
import today.vanta.util.game.player.PlayerUtil;
import today.vanta.util.game.render.RenderUtil;
import today.vanta.util.game.render.Renderable;
import today.vanta.util.game.render.font.CFonts;
import today.vanta.util.game.render.shape.GradientMode;
import today.vanta.util.game.render.shape.impl.GradientRectangle;
import today.vanta.util.game.render.shape.impl.Rectangle;
import today.vanta.util.system.math.animation.Animation;
import today.vanta.util.system.math.animation.Easing;

import java.awt.*;

public class TargetHUD extends Module {
    private static final Color BACKGROUND = new Color(20, 20, 20, 200);
    private static final Color DARKER_BACKGROUND = new Color(20, 20, 20, 255);
    private static final Color PASSBACKGROUND = new Color(182, 215, 223);

    private EntityLivingBase localTarget;
    private String oldTarget;
    private boolean can;

    private float width = 130;
    private float height = 40;

    private boolean dragging;
    private float dragX, dragY;

    private final StringSetting mode = Setting.of("Mode", "Vanta", "Classic", "Vanta", "Adjust", "ID-Card", "aged");
    private final NumberSetting
            x = Setting.of("X position", 20, 0, 2000),
            y = Setting.of("Y position", 20, 0, 2000);

    public TargetHUD() {
        super("TargetHUD", "Target information.", Category.HUD);
    }

    @EventListen
    private void onDrawScreen(RenderScreenEvent event) {
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

        draw(event);
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
    private float abarwidth = width - 37;
    private float atargetwidth = width - 37;

    private Animation animation;
    private Animation ghostAnimation;

    private void draw(Renderable renderable) {
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
                        .color(new Color(28, 29, 33))
                        .draw(renderable);

                RenderUtil.renderHead(renderable, (EntityPlayer) localTarget, x, y, 36f);
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
                        .draw(renderable);

                Rectangle
                        .create(x, y + 36, ghostBarWidth, 4f)
                        .color(color.darker())
                        .draw(renderable);

                GradientRectangle
                        .create(x, y + 36, barWidth, 4f)
                        .firstColor(color2)
                        .secondColor(color)
                        .draw(renderable);
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
                        .draw(renderable);

                RenderUtil.renderEntity((int) x + 15, (int) y + 52, 25, -30, 0, localTarget);

                Rectangle
                        .create(x + 37, y - (10f / 2) + (height / 2) + 10, healthbarwidth, 10f)
                        .color(DARKER_BACKGROUND)
                        .draw(renderable);

                Rectangle
                        .create(x + 37, y - (10f / 2) + (height / 2) + 10, healthbar, 10f)
                        .color(healthbarcol)
                        .draw(renderable);

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
                        .draw(renderable);

                RenderUtil.renderHead(renderable, (EntityPlayer) localTarget, x + 2, y + 2, 20f);
                CFonts.getFont("T-Regular", 16).drawStringWithShadow(localTarget.getName(), x + 24, y + 1, Color.WHITE);

                Rectangle
                        .create(x + 2, y + space, width - 4, 3f)
                        .color(DARKER_BACKGROUND)
                        .draw(renderable);

                Rectangle
                        .create(x + 2, y + space, adghostBarWidth, 3f)
                        .color(color.darker())
                        .draw(renderable);

                Rectangle
                        .create(x + 2, y + space, adbarWidth, 3f)
                        .color(color)
                        .draw(renderable);

                float itemX = x + 10 + 2;
                float itemY = y + 10;

                ItemStack currentItem = ((EntityPlayer) localTarget).inventory.getCurrentItem();
                if (currentItem != null) {
                    itemX += 10 + 1;
                    RenderUtil.renderScaledItem(currentItem, itemX + 1, itemY + 1, 0.65f);
                }

                ItemStack slot3 = ((EntityPlayer) localTarget).inventory.armorItemInSlot(3);
                if (slot3 != null) {
                    itemX += 10 + 1;
                    RenderUtil.renderScaledItem(slot3, itemX, itemY, 0.75f);
                }

                ItemStack slot2 = ((EntityPlayer) localTarget).inventory.armorItemInSlot(2);
                if (slot2 != null) {
                    itemX += 10 + 1;
                    RenderUtil.renderScaledItem(slot2, itemX, itemY, 0.75f);
                }

                ItemStack slot1 = ((EntityPlayer) localTarget).inventory.armorItemInSlot(1);
                if (slot1 != null) {
                    itemX += 10 + 1;
                    RenderUtil.renderScaledItem(slot1, itemX, itemY, 0.75f);
                }

                ItemStack slot0 = ((EntityPlayer) localTarget).inventory.armorItemInSlot(0);
                if (slot0 != null) {
                    itemX += 10 + 1;
                    RenderUtil.renderScaledItem(slot0, itemX, itemY, 0.75f);
                }

                CFonts.getFont("T-Regular", 14).drawStringWithShadow(String.format("%.1f", mc.thePlayer.getHealth() - localTarget.getHealth()), x + width - (length) - 4, y + 15, Color.WHITE);
                break;

            case "ID-Card":
                width = 211;
                height = 100;
                String entitytype;
                if (PlayerUtil.checkIllegal(localTarget)) {
                    entitytype = "Bot";
                } else {
                    entitytype = "Player";
                }
                String firstChar = String.valueOf(localTarget.getName().charAt(0)).toUpperCase();
                Rectangle
                        .create(x, y, width, height)
                        .color(PASSBACKGROUND)
                        .draw(renderable);

                CFonts.OCRB_10.drawString("NORGE NOREG NORGA", x + 2, y + 2, Color.RED, false);
                CFonts.OCRB_10.drawString("NORWAY", x + 2, y + 9, Color.RED, false);
                CFonts.OCRB_10.drawString("ID-KORT ID-DUODASTUS", x + 118, y + 2, Color.RED, false);
                CFonts.OCRB_10.drawString("IDENTITY CARD", x + 150, y + 9, Color.RED, false);
                CFonts.OCRB_8.drawString("Etternavn/Etternamn/Sohkanamma/Surname", x + 71, y + 20, Color.RED, false);
                CFonts.OCRB_18.drawString(entitytype.toUpperCase(), x + 71, y + 25, Color.BLACK, false);
                CFonts.OCRB_8.drawString("Fornavn/Førenamn/Ovdanamma/Given Name", x + 71, y + 41, Color.RED, false);
                CFonts.OCRB_18.drawString(localTarget.getName().toUpperCase(), x + 71, y + 47, Color.BLACK, false);
                CFonts.OCRB_8.drawString("Kjønn/Sokhabeali/Sex", x + 71, y + 63, Color.RED, false);
                CFonts.OCRB_18.drawString("MINECRAFT", x + 71, y + 69, Color.BLACK, false);
                CFonts.RUSTICROADWAY_22.drawString(firstChar + ". " + entitytype, x + 71, y + 83, Color.BLACK, false);
                RenderUtil.renderHead(renderable, (EntityPlayer) localTarget, x + 3, y + 22, 64);
                break;
            case "aged":
                width = 150f;
                height = 35f;

                whatever = (localTarget.getHealth() / localTarget.getMaxHealth());
                healthbar = (width - 37) * whatever;

                if (healthbar != atargetwidth) {
                    atargetwidth = healthbar;
                    if (oldTarget != localTarget.getName()) {
                        abarwidth = healthbar;
                        oldTarget = localTarget.getName();
                        return;
                    }

                    animation = Animation.create(
                            abarwidth,
                            atargetwidth,
                            100,
                            Easing.LINEAR,
                            val -> abarwidth = val
                    );

                    animation.start();
                }

                color = Vanta.instance.moduleStorage.getT(Theme.class).colors[0];
                color2 = Vanta.instance.moduleStorage.getT(Theme.class).colors[0].darker();

                health_str = String.format("%.1f", localTarget.getHealth());
                String distance_str = String.format("%.1f", mc.thePlayer.getDistanceToEntity(localTarget));

                Rectangle
                        .create(x, y, width, height)
                        .color(BACKGROUND)
                        .draw(renderable);

                RenderUtil.renderHead(renderable, (EntityPlayer) localTarget, x + 2, y + 2, 31);

                Rectangle
                        .create(x + 35, y - (10f / 2) + (height / 2) + 10, width - 37, 10f)
                        .color(DARKER_BACKGROUND)
                        .draw(renderable);

                GradientRectangle
                        .create(x + 35, y - (10f / 2) + (height / 2) + 10, abarwidth, 10f)
                        .firstColor(color)
                        .secondColor(color2)
                        .gradientMode(GradientMode.VERTICAL)
                        .draw(renderable);

                mc.exhiFontRendererObj.drawString(health_str, x + 35, y + 12, Color.WHITE);
                mc.exhiFontRendererObj.drawString(localTarget.getName(), x + 35, y + 2, Color.WHITE);
                mc.exhiFontRendererObj.drawString(distance_str, x + width - mc.exhiFontRendererObj.getStringWidth(distance_str) - 2, y + 2, Color.WHITE);
                break;
        }

        if (dragging && mc.currentScreen instanceof GuiChat) {
            if (mode.getValue().equals("aged")) {
                GradientRectangle
                        .create(x - 0.5, y - 0.5, width + 1, height + 1)
                        .firstColor(color)
                        .secondColor(color2)
                        .outline(true)
                        .draw(renderable);
            } else {
                Rectangle
                        .create(x - 0.5, y - 0.5, width + 1, height + 1)
                        .color(color)
                        .outline(true)
                        .draw(renderable);
            }
        }
    }

    @Override
    public String getSuffix() {
        return mode.getValue();
    }
}