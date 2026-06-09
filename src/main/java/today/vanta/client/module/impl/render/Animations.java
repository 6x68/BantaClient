package today.vanta.client.module.impl.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;
import today.vanta.client.event.impl.game.player.MotionEvent;
import today.vanta.client.event.impl.game.render.BobArmEvent;
import today.vanta.client.event.impl.game.render.PerformBlockEvent;
import today.vanta.client.module.Category;
import today.vanta.client.module.Module;
import today.vanta.client.setting.Setting;
import today.vanta.client.setting.impl.BooleanSetting;
import today.vanta.client.setting.impl.StringSetting;
import today.vanta.util.game.events.EventListen;
import today.vanta.util.game.events.EventState;

public class Animations extends Module {
    private final StringSetting mode = Setting.of("Mode", "1.7", "1.7", "Interia", "Exhibition", "Sigma", "Stella", "Smooth");

    private final BooleanSetting
            noBob = Setting.of("German No-bob", false),
            noSway = Setting.of("No hand sway", false);

    public Animations() {
        super("Animations", "Modifies Minecraft block animations.", Category.RENDER);
    }

    @EventListen
    private void onBob(BobArmEvent event) {
        event.cancelled = noSway.getValue();
    }

    @EventListen
    private void onMotion(MotionEvent event) {
        if (event.state == EventState.PRE && noBob.getValue()) {
            mc.thePlayer.distanceWalkedModified = 0.0F;
            mc.gameSettings.viewBobbing = true;
        }
    }

    @EventListen
    private void onPerform(PerformBlockEvent event) {
        event.cancelled = true;
        ItemRenderer renderer = event.renderer;

        float f = event.equippedProgress;
        float f1 = mc.thePlayer.getSwingProgress(event.partialTicks);

        float var9 = MathHelper.sin(MathHelper.sqrt_float(f1) * MathHelper.PI);

        switch (mode.getValue()) {
            case "1.7":
                renderer.transformFirstPersonItem(-0.1f, f1);
                renderer.doBlockTransformations();
                break;
            case "Sigma":
                renderer.transformFirstPersonItem(f * 0.5f, 0);
                GlStateManager.rotate(-var9 * 55 / 2.0F, -8.0F, -0.0F, 9.0F);
                GlStateManager.rotate(-var9 * 45, 1.0F, var9 / 2, -0.0F);
                renderer.doBlockTransformations();
                GL11.glTranslated(1.2, 0.3, 0.5);
                GL11.glTranslatef(-1, mc.thePlayer.isSneaking() ? -0.1F : -0.2F, 0.2F);
                break;
            case "Stella":
                renderer.transformFirstPersonItem(-0.1f, f1);
                GlStateManager.translate(-0.5F, 0.4F, -0.2F);
                GlStateManager.rotate(30.0F, 0.0F, 1.0F, 0.0F);
                GlStateManager.rotate(-70.0F, 1.0F, 0.0F, 0.0F);
                GlStateManager.rotate(40.0F, 0.0F, 1.0F, 0.0F);
                break;
            case "Exhibition":
                GL11.glTranslated(-0.04D, 0.13D, 0.0D);
                renderer.transformFirstPersonItem(f / 2.5F, 0.0f);
                GlStateManager.rotate(-var9 * 40.0F / 2.0F, var9 / 2.0F, 1.0F, 4.0F);
                GlStateManager.rotate(-var9 * 30.0F, 1.0F, var9 / 3.0F, -0.0F);
                renderer.doBlockTransformations();
                break;
            case "Interia":
                renderer.transformFirstPersonItem(0.05f, f1);
                GlStateManager.translate(-0.5F, 0.5F, 0.0F);
                GlStateManager.rotate(30.0F, 0.0F, 1.0F, 0.0F);
                GlStateManager.rotate(-80.0F, 1.0F, 0.0F, 0.0F);
                GlStateManager.rotate(60.0F, 0.0F, 1.0F, 0.0F);
                break;
            case "Smooth":
                renderer.transformFirstPersonItem(f / 1.5F, 0.0f);
                renderer.doBlockTransformations();
                GlStateManager.translate(-0.05f, 0.3f, 0.3f);
                GlStateManager.rotate(-var9 * 140.0f, 8.0f, 0.0f, 8.0f);
                GlStateManager.rotate(var9 * 90.0f, 8.0f, 0.0f, 8.0f);
                break;
        }
    }
}