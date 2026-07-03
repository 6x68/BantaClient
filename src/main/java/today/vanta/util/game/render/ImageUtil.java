package today.vanta.util.game.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ImageUtil {
    private static final Map<String, ResourceLocation> CACHE = new ConcurrentHashMap<>();

    public static ResourceLocation getTexture(String name) {
        return CACHE.computeIfAbsent(name,
                p -> new ResourceLocation("vanta", "images/" + p));
    }

    public static int bindAndGetId(ResourceLocation location) {
        TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
        ITextureObject texture = textureManager.mapTextureObjects.get(location);

        if (texture == null) {
            textureManager.bindTexture(location);
            texture = textureManager.mapTextureObjects.get(location);
        }

        return texture != null ? texture.getGlTextureId() : -1;
    }

    public static void clearCache() {
        CACHE.clear();
    }
}
