package today.vanta.client.module.impl.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;
import today.vanta.client.event.impl.game.render.PerformBlockEvent;
import today.vanta.client.module.Category;
import today.vanta.client.module.Module;
import today.vanta.client.setting.impl.StringSetting;
import today.vanta.util.game.events.EventListen;

public class Animations extends Module {
    private final StringSetting mode = StringSetting.builder()
            .name("Mode")
            .value("Exhibition")
            .values("Exhibition", "Interia")
            .build();

    public Animations() {
        super("Animations", "Modifies Minecraft block animations.", Category.RENDER);
    }

    @EventListen
    private void onPerform(PerformBlockEvent event) {
        event.cancelled = true;
        ItemRenderer renderer = event.renderer;

        float f = event.equippedProgress;
        float f1 = mc.thePlayer.getSwingProgress(event.partialTicks);

        float var9 = MathHelper.sin(MathHelper.sqrt_float(f1) * MathHelper.PI);

        switch (mode.getValue()) {
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
        }
    }
}