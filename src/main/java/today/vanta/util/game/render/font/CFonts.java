package today.vanta.util.game.render.font;

import today.vanta.Vanta;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

public class CFonts {
    public static CFontRenderer HN_MEDIUM_24 = getFont("HN-Medium", 24);
    public static CFontRenderer HN_REGULAR_48 = getFont("HN-Regular", 48);

    public static CFontRenderer SFPT_MEDIUM_18 = getFont("SFPT-Medium", 18);
    public static CFontRenderer SFPT_SEMIBOLD_20 = getFont("SFPT-Semibold", 20);
    public static CFontRenderer SFPT_MEDIUM_24 = getFont("SFPT-Medium", 24);
    public static CFontRenderer SFPT_SEMIBOLD_42 = getFont("SFPT-Semibold", 42);

    public static CFontRenderer getFont(String fontName, float size) {
        return new CFontRenderer(getAwtFont(fontName + ".otf", size));
    }

    private static Font getAwtFont(String fontName, float size) {
        Font customFont = Font.getFont("SansSerif");
        try (InputStream fontStream = CFonts.class.getResourceAsStream("/assets/vanta/fonts/" + fontName)) {
            if (fontStream != null) {
               customFont = getAwtFont(fontStream, size);
            }
        } catch (Exception e) {
        }
        return customFont;
    }

    private static Font getAwtFont(InputStream inputStream, float size) {
        String vmVendor = System.getProperty("java.vm.vendor", "");
        boolean isOracle = vmVendor.toLowerCase().contains("oracle");
        boolean isOpenJ9 = vmVendor.toLowerCase().contains("openj9");

        if (isOracle) {
            size = size / 2.0f;
        } else if (isOpenJ9) {
            size = size / 1.5f;
        }

        try {
            Font font = Font.createFont(Font.TRUETYPE_FONT, inputStream);
            return font.deriveFont(size);
        } catch (FontFormatException | IOException e) {
            return new Font("SansSerif", Font.PLAIN, Math.max(12, (int) size));
        }
    }
}