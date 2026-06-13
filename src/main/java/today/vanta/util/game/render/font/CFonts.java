package today.vanta.util.game.render.font;

import today.vanta.util.game.render.font.impl.GlyphFontRenderer;

import java.awt.*;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CFonts {
    private static final Map<String, Font> FONT_CACHE = new ConcurrentHashMap<>();
    private static final Map<String, GlyphFontRenderer> RENDERER_CACHE = new ConcurrentHashMap<>();

    // Helvetica Neue
    public static GlyphFontRenderer HN_MEDIUM_24 = getFont("HN-Medium", 24);
    public static GlyphFontRenderer HN_REGULAR_48 = getFont("HN-Regular", 48);

    // SF Pro Text
    public static GlyphFontRenderer SFPT_MEDIUM_18 = getFont("SFPT-Medium", 18);
    public static GlyphFontRenderer SFPT_MEDIUM_20 = getFont("SFPT-Medium", 20);
    public static GlyphFontRenderer SFPT_SEMIBOLD_20 = getFont("SFPT-Semibold", 20);
    public static GlyphFontRenderer SFPT_MEDIUM_24 = getFont("SFPT-Medium", 24);
    public static GlyphFontRenderer SFPT_SEMIBOLD_42 = getFont("SFPT-Semibold", 42);
    public static GlyphFontRenderer SFPT_REGULAR_18 = getFont("SFPT-Regular", 18);
    public static GlyphFontRenderer SFPT_REGULAR_16 = getFont("SFPT-Regular", 16);
    public static GlyphFontRenderer SFPT_REGULAR_15 = getFont("SFPT-Regular", 15);
    public static GlyphFontRenderer SFPT_REGULAR_14 = getFont("SFPT-Regular", 14);
    public static GlyphFontRenderer SFPT_REGULAR_20 = getFont("SFPT-Regular", 20);
    public static GlyphFontRenderer SFPT_REGULAR_24 = getFont("SFPT-Regular", 24);

    // Ramadhan Karim
    public static GlyphFontRenderer RK_REGULAR_50 = getFont("RK-Regular", 50);

    // Creato-Display
    //public static GlyphFontRenderer CD_REGULAR_20 = getFont("CD-Regular", 20);
    //public static GlyphFontRenderer CD_MEDIUM_20 = getFont("CD-Medium", 20);
    //public static GlyphFontRenderer CD_LIGHT_20 = getFont("CD-LIGHT", 20);

    // Arial
    //public static GlyphFontRenderer A_REGULAR_18 = getFont("A-REGULAR", 18);

    public static GlyphFontRenderer getFont(String fontName, float size) {
        String key = fontName + ":" + size;

        return RENDERER_CACHE.computeIfAbsent(
                key,
                k -> new GlyphFontRenderer(getAwtFont(fontName + ".otf", size))
        );
    }

    private static Font getAwtFont(String fontName, float size) {
        Font baseFont = FONT_CACHE.computeIfAbsent(fontName, name -> {
            try (InputStream fontStream = CFonts.class.getResourceAsStream("/assets/vanta/fonts/" + name)) {
                if (fontStream != null) {
                    return Font.createFont(Font.TRUETYPE_FONT, fontStream);
                }
            } catch (Exception ignored) {
            }

            return new Font("SansSerif", Font.PLAIN, 12);
        });

        return baseFont.deriveFont(adjustSize(size));
    }

    private static float adjustSize(float size) {
        String vmVendor = System.getProperty("java.vm.vendor", "").toLowerCase();

        if (vmVendor.contains("oracle")) {
            return size / 2.0f;
        }

        if (vmVendor.contains("openj9")) {
            return size / 1.5f;
        }

        return size;
    }
}