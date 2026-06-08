package today.vanta.client.module.impl.render;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import today.vanta.Vanta;
import today.vanta.client.event.impl.game.render.Render2DEvent;
import today.vanta.client.module.Category;
import today.vanta.client.module.Module;
import today.vanta.client.module.impl.client.Theme;
import today.vanta.client.setting.impl.MultiStringSetting;
import today.vanta.util.game.events.EventListen;
import today.vanta.util.game.render.RenderUtil;

import java.awt.*;

public class ESP extends Module {
    private final MultiStringSetting entities = MultiStringSetting.builder()
            .name("Entities")
            .value("Players")
            .values("Players", "Monsters", "Animals", "Local", "Invisibles")
            .build();

    public ESP() {
        super("ESP", "Extra-sensory perception", Category.RENDER);
    }

    @EventListen
    private void onRender(Render2DEvent event) {
        float ticks = event.partialTicks;
        ScaledResolution sr = event.scaledResolution;
        Color color = Vanta.instance.moduleStorage.getT(Theme.class).colors[0];

        for (Entity entity : mc.theWorld.loadedEntityList) {
            if (!(entity instanceof EntityLivingBase)) continue;
            if (!canRender(entity)) continue;
            if (entity == mc.thePlayer && mc.gameSettings.thirdPersonView == 0) continue;

            ScreenBounds bounds = projectBoundingBox(entity, ticks, sr);
            if (bounds == null) continue;

            float x = (float) bounds.minX;
            float y = (float) bounds.minY;
            float width = (float) (bounds.maxX - bounds.minX);
            float height = (float) (bounds.maxY - bounds.minY);
            if (width <= 0.0F || height <= 0.0F) continue;

            RenderUtil.rectangle(x - 0.5f, y - 0.5f, width + 1, height + 1, false, Color.BLACK);
            RenderUtil.rectangle(x, y, width, height, false, color);
            RenderUtil.rectangle(x + 0.5f, y + 0.5f, width - 1, height - 1, false, Color.BLACK);
        }
    }

    private boolean canRender(Entity living) {
        if (living == mc.thePlayer && entities.isEnabled("Local")) return true;
        if (living instanceof EntityAnimal && entities.isEnabled("Animals")) return true;
        if (living instanceof IMob && entities.isEnabled("Monsters")) return true;
        if (living.isInvisible() && entities.isEnabled("Invisibles")) return true;
        if (living instanceof EntityPlayer && entities.isEnabled("Player")) return true;
        return false;
    }

    private ScreenBounds projectBoundingBox(Entity entity, float partialTicks, ScaledResolution sr) {
        double entityX = interpolate(entity.prevPosX, entity.posX, partialTicks);
        double entityY = interpolate(entity.prevPosY, entity.posY, partialTicks);
        double entityZ = interpolate(entity.prevPosZ, entity.posZ, partialTicks);

        double viewerX = interpolate(mc.getRenderManager().viewerPosX, mc.getRenderManager().viewerPosX, partialTicks);
        double viewerY = interpolate(mc.getRenderManager().viewerPosY, mc.getRenderManager().viewerPosY, partialTicks);
        double viewerZ = interpolate(mc.getRenderManager().viewerPosZ, mc.getRenderManager().viewerPosZ, partialTicks);

        AxisAlignedBB box = entity.getEntityBoundingBox();

        AxisAlignedBB cameraSpaceBox = new AxisAlignedBB(
                box.minX - entity.posX + entityX - viewerX,
                box.minY - entity.posY + entityY - viewerY,
                box.minZ - entity.posZ + entityZ - viewerZ,
                box.maxX - entity.posX + entityX - viewerX,
                box.maxY - entity.posY + entityY - viewerY,
                box.maxZ - entity.posZ + entityZ - viewerZ
        );

        ScreenBounds bounds = new ScreenBounds();
        boolean projected = false;

        for (int i = 0; i < 8; i++) {
            double cornerX = (i & 1) == 0 ? cameraSpaceBox.minX : cameraSpaceBox.maxX;
            double cornerY = (i & 2) == 0 ? cameraSpaceBox.minY : cameraSpaceBox.maxY;
            double cornerZ = (i & 4) == 0 ? cameraSpaceBox.minZ : cameraSpaceBox.maxZ;

            Vec3 screen = project(cornerX, cornerY, cornerZ, sr);

            if (screen != null) {
                bounds.include(screen.xCoord, screen.yCoord);
                projected = true;
            }
        }

        if (!projected) {
            return null;
        }

        bounds.clamp(sr.getScaledWidth_double(), sr.getScaledHeight_double());
        return bounds.isVisible() ? bounds : null;
    }

    private Vec3 project(double x, double y, double z, ScaledResolution sr) {
        Vec3 projected = ActiveRenderInfo.projectWorldToScreen(x, y, z);

        if (projected == null || projected.zCoord < 0.0D || projected.zCoord > 1.0D) {
            return null;
        }

        double scale = sr.getScaleFactor();
        double screenX = projected.xCoord / scale;
        double screenY = sr.getScaledHeight_double() - projected.yCoord / scale;

        return new Vec3(screenX, screenY, projected.zCoord);
    }

    private double interpolate(double previous, double current, float partialTicks) {
        return previous + (current - previous) * partialTicks;
    }

    private static class ScreenBounds {
        private double minX = Double.MAX_VALUE;
        private double minY = Double.MAX_VALUE;
        private double maxX = -Double.MAX_VALUE;
        private double maxY = -Double.MAX_VALUE;

        private void include(double x, double y) {
            this.minX = Math.min(this.minX, x);
            this.minY = Math.min(this.minY, y);
            this.maxX = Math.max(this.maxX, x);
            this.maxY = Math.max(this.maxY, y);
        }

        private void clamp(double screenWidth, double screenHeight) {
            this.minX = MathHelper.clamp_double(this.minX, 0.0D, screenWidth);
            this.minY = MathHelper.clamp_double(this.minY, 0.0D, screenHeight);
            this.maxX = MathHelper.clamp_double(this.maxX, 0.0D, screenWidth);
            this.maxY = MathHelper.clamp_double(this.maxY, 0.0D, screenHeight);
        }

        private boolean isVisible() {
            return this.maxX > this.minX && this.maxY > this.minY;
        }
    }
}
