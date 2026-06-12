package today.vanta.util.game.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import today.vanta.Vanta;

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

    public static void rectangle(double x, double y, double width, double height, boolean filled, Color color, float lineWidth) {
        start();

        if (color != null)
            color(color);

        GL11.glLineWidth(lineWidth);
        GlStateManager.glBegin(filled ? GL_TRIANGLE_FAN : GL_LINE_LOOP);

        GL11.glVertex2d(x, y);
        GL11.glVertex2d(x + width, y);
        GL11.glVertex2d(x + width, y + height);
        GL11.glVertex2d(x, y + height);

        GlStateManager.glEnd();
        stop();
    }

    public static void rectangle(double x, double y, double width, double height, boolean filled, Color color) {
        rectangle(x, y, width, height, filled, color, 2.0f);
    }

    public static void rectangle(double x, double y, double width, double height, Color color) {
        rectangle(x, y, width, height, true, color);
    }

    public static void rectangle(double x, double y, double width, double height, int color) {
        rectangle(x, y, width, height, true, new Color(color));
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

    public static void image(ResourceLocation resourceLocation, float x, float y, float width, float height, Color color) {
        image(resourceLocation, x, y, width, height, color.getRGB());
    }

    public static void image(ResourceLocation resourceLocation, float x, float y, float width, float height, int color) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(resourceLocation);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        float alpha = (color >> 24 & 255) / 255.0F;
        float red = (color >> 16 & 255) / 255.0F;
        float green = (color >> 8 & 255) / 255.0F;
        float blue = (color & 255) / 255.0F;
        GlStateManager.color(red, green, blue, alpha);

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 0);
        GlStateManager.scale(width, height, 1);

        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(0, 0);
        GL11.glVertex2f(0, 0);
        GL11.glTexCoord2f(1, 0);
        GL11.glVertex2f(1, 0);
        GL11.glTexCoord2f(1, 1);
        GL11.glVertex2f(1, 1);
        GL11.glTexCoord2f(0, 1);
        GL11.glVertex2f(0, 1);
        GL11.glEnd();

        GlStateManager.popMatrix();
        GlStateManager.disableBlend();
    }

    public static void image(int textureId, float x, float y, float width, float height, float u, float v, float tW, float tH) {
        color(new Color(255, 255, 255));
        TextureUtil.bindTexture(textureId);
        Gui.drawModalRectWithCustomSizedTexture((int) x, (int) y, u, v, (int) width, (int) height, tW, tH);
        color(new Color(255, 255, 255));
    }

    public static void image(int textureId, float x, float y, float width, float height) {
        image(textureId, x, y, width, height, 0, 0, width, height);
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
        GlStateManager.enableAlpha();
        GlStateManager.enableDepth();

        GlStateManager.enableCull();
        GlStateManager.enableTexture2D();

        GlStateManager.disableBlend();
        color(Color.WHITE);
        GlStateManager.popMatrix();
    }

    public static void rectangleGradientVertical(double x, double y, double width, double height, Color topColor, Color bottomColor) {
        start();

        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glBegin(GL11.GL_QUADS);

        { // Top left
            color(topColor);
            GL11.glVertex2d(x, y);
        }

        { // Top right
            color(topColor);
            GL11.glVertex2d(x + width, y);
        }

        { // Bottom right
            color(bottomColor);
            GL11.glVertex2d(x + width, y + height);
        }

        { // Bottom left
            color(bottomColor);
            GL11.glVertex2d(x, y + height);
        }

        GL11.glEnd();
        GL11.glShadeModel(GL11.GL_FLAT);

        stop();
    }

    public static void rectangleGradientHorizontal(double x, double y, double width, double height, Color topColor, Color bottomColor) {
        start();

        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glBegin(GL11.GL_QUADS);

        { // Top left
            color(topColor);
            GL11.glVertex2d(x, y);
        }

        { // Top right
            color(bottomColor);
            GL11.glVertex2d(x + width, y);
        }

        { // Bottom right
            color(bottomColor);
            GL11.glVertex2d(x + width, y + height);
        }

        { // Bottom left
            color(topColor);
            GL11.glVertex2d(x, y + height);
        }

        GL11.glEnd();
        GL11.glShadeModel(GL11.GL_FLAT);

        stop();
    }

    public static void rectangleGradientHorizontal(double x, double y, double width, double height, float lineWidth, Color leftColor, Color rightColor) {
        start();

        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glLineWidth(lineWidth);

        GL11.glBegin(GL11.GL_LINE_LOOP);

        { // Top left
            color(leftColor);
            GL11.glVertex2d(x, y);
        }

        { // Top right
            color(rightColor);
            GL11.glVertex2d(x + width, y);
        }

        { // Bottom right
            color(rightColor);
            GL11.glVertex2d(x + width, y + height);
        }

        { // Bottom left
            color(leftColor);
            GL11.glVertex2d(x, y + height);
        }

        GL11.glEnd();
        GL11.glShadeModel(GL11.GL_FLAT);

        stop();
    }

    public static void renderEntity(int posX, int posY, int scale, float mouseX, float mouseY, EntityLivingBase entity) {
        GuiInventory.drawEntityOnScreen(posX, posY, scale, mouseX, mouseY, entity);
    }

    public static void renderHead(EntityPlayer target, float x, float y, float headSize) {
        ResourceLocation skinLocation = ((AbstractClientPlayer) target).getLocationSkin();
        mc.getTextureManager().bindTexture(skinLocation);

        Gui.drawScaledCustomSizeModalRect((int) x, (int) y, 8, 8, 8, 8, (int) headSize, (int) headSize, 64, 64);
        Gui.drawScaledCustomSizeModalRect((int) x, (int) y, 40, 8, 8, 8, (int) headSize, (int) headSize, 64, 64);
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