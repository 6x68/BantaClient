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
import today.vanta.client.module.impl.hud.arraylist.BitMapRenderer;
import today.vanta.client.processor.impl.TargetProcessor;
import today.vanta.client.setting.Setting;
import today.vanta.client.setting.impl.NumberSetting;
import today.vanta.client.setting.impl.StringSetting;
import today.vanta.util.game.events.EventListen;
import today.vanta.util.game.render.RenderUtil;
import today.vanta.util.game.render.font.CFonts;
import today.vanta.util.system.math.animation.Animation;
import today.vanta.util.system.math.animation.Easing;

import java.awt.*;

public class TargetHUD extends Module {
    BitMapRenderer font = new BitMapRenderer(mc.fontRendererObj);
    private EntityLivingBase localTarget;
    private String oldTarget;
    private boolean can;

    private static final Color BACKGROUND = new Color(20, 20, 20, 200);
    Color bgbutdarker = new Color(20, 20, 20, 255);

    private static final float WIDTH = 130;
    private static final float HEIHT = 40;
    private float mousingY;
    private float mousingX;

    private boolean dragging;
    private float dragX, dragY;

    private final StringSetting mode = Setting.of("Mode", "Vanta", "Classic", "Vanta");
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

        mousingX = event.mouseX;
        mousingY = event.mouseY;

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

    private float barWidth = WIDTH;
    private float ghostBarWidth = WIDTH;
    private float targetWidth = WIDTH;
    private float targetWidth2 = WIDTH;
    private Animation animation;
    private Animation ghostAnimation;

    private void draw() {
        switch (mode.getValue()) {
            case "Vanta":
                Color color = Vanta.instance.moduleStorage.getT(Theme.class).colors[0];
                Color color2 = Vanta.instance.moduleStorage.getT(Theme.class).colors[1];

                float x = this.x.getValue().floatValue();
                float y = this.y.getValue().floatValue();

                RenderUtil.rectangle(x, y, WIDTH, HEIHT, BACKGROUND);
                RenderUtil.player_head((EntityPlayer) localTarget, x, y, 36f);
                CFonts.SFPT_MEDIUM_20.drawStringWithShadow(localTarget.getName(), x + 38, y + 4, Color.WHITE);
                CFonts.SFPT_REGULAR_18.drawStringWithShadow(String.format("%.1f", localTarget.getHealth()), x + 38, y + 15, Color.WHITE);

                float healthWidth = WIDTH * (localTarget.getHealth() / localTarget.getMaxHealth());
                float ghostWidth = WIDTH * (localTarget.getHealth() / localTarget.getMaxHealth());

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

                RenderUtil.rectangle(x, y + 36, WIDTH, 4f, bgbutdarker);
                RenderUtil.rectangle(x, y + 36, ghostBarWidth, 4f, color.darker());
                RenderUtil.horizontal_grad(x, y + 36, barWidth, 4f, color2, color);

                if (dragging && mc.currentScreen instanceof GuiChat) {
                    RenderUtil.rectangle(x - 0.5, y - 0.5, WIDTH + 1, HEIHT + 1, false, color);
                }
                break;

            case "Classic":
                float xval = this.x.getValue().floatValue();
                float yval = this.y.getValue().floatValue();
                float width = 150f;
                float height = 55f;
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
                RenderUtil.rectangle(xval, yval, width, height, true, BACKGROUND);
                RenderUtil.RenderEntity((int) xval + 15,(int) yval + 52,25,-30,0,localTarget);
                RenderUtil.rectangle(xval + 37, yval - (10f / 2) + (height / 2) + 10,healthbarwidth , 10f,bgbutdarker);
                RenderUtil.rectangle(xval + 37, yval - (10f / 2) + (height / 2) + 10,healthbar , 10f,healthbarcol);
                font.drawString(health_str + " ❤", xval + 37,yval + 16,Color.WHITE,true);
                font.drawString(localTarget.getName(),xval + 37,yval + 2,Color.WHITE,true);
                break;
        }
    }
}