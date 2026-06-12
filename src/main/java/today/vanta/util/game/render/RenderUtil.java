package today.vanta.util.game.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;
import today.vanta.Vanta;
import today.vanta.util.game.render.shape.impl.ImageRectangle;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.Base64;

import static org.lwjgl.opengl.GL11.*;

public class RenderUtil {
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static boolean hovered(float mouseX, float mouseY, float x, float y, float width, float height) {
        return mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
    }

    private static void start_scissor() {
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
    }

    private static void end_scissor() {
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    public static void scissor(double x, double y, double width, double height, Runnable runnable) {
        ScaledResolution sr = new ScaledResolution(mc);
        double scaleFactor = sr.getScaleFactor();

        int sx = (int) (x * scaleFactor);
        int sw = (int) (width * scaleFactor);
        int sh = (int) (height * scaleFactor);

        int sy = (int) ((sr.getScaledHeight() - y - height) * scaleFactor);

        start_scissor();
        GL11.glScissor(sx, sy, sw, sh);

        try {
            runnable.run();
        } finally {
            end_scissor();
        }
    }

    public static void color(Color color) {
        GlStateManager.color(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, color.getAlpha() / 255.0F);
    }

    public static void color(float r, float g, float b, float a) {
        GlStateManager.color(r, g, b, a);
    }

    public static void start() {
        GlStateManager.pushMatrix();

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        GlStateManager.disableTexture2D();
        GlStateManager.disableCull();

        GlStateManager.disableAlpha();
        GlStateManager.disableDepth();
    }

    public static void stop() {
        GL11.glLineWidth(1.0f);
        GlStateManager.enableAlpha();
        GlStateManager.enableDepth();

        GlStateManager.enableCull();
        GlStateManager.enableTexture2D();

        GlStateManager.disableBlend();
        color(Color.WHITE);
        GlStateManager.popMatrix();
    }

    public static void renderEntity(int posX, int posY, int scale, float mouseX, float mouseY, EntityLivingBase entity) {
        GuiInventory.drawEntityOnScreen(posX, posY, scale, mouseX, mouseY, entity);
    }

    public static void renderHead(EntityPlayer target, float x, float y, float headSize) {
        ImageRectangle
                .create(x, y, headSize, headSize, -1)
                .uv(8, 8)
                .uvSize(8, 8)
                .tileSize(64, 64)
                .textureId(((AbstractClientPlayer) target).getLocationSkin())
                .draw();

        ImageRectangle
                .create(x, y, headSize, headSize, -1)
                .uv(40, 8)
                .uvSize(8, 8)
                .tileSize(64, 64)
                .textureId(((AbstractClientPlayer) target).getLocationSkin())
                .draw();
    }

    public static BufferedImage base64ToBufferedImage(String base64Image) {
        base64Image = base64Image.replace("\\u003d", "=");
        try {
            if (base64Image.startsWith("data:image")) {
                base64Image = base64Image.split(",")[1];
            }

            byte[] imageBytes = Base64.getDecoder().decode(base64Image);

            return ImageIO.read(new ByteArrayInputStream(imageBytes));
        } catch (Exception e) {
            Vanta.instance.logger.error("Failed to create an image from base64.");
            return null;
        }
    }
}