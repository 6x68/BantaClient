package today.vanta.util.system.math;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ColorUtil {
    public static int applyAlpha(int color, float alpha) {
        return (int) (alpha * 255.0F) << 24 | color & 16777215;
    }

    public static float calculateDarkPixelRatio(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int totalPixels = width * height;
        int darkPixelCount = 0;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Color color = new Color(image.getRGB(x, y), true);

                if (isDarkOrBrown(color)) {
                    darkPixelCount++;
                }
            }
        }

        return (float) darkPixelCount / totalPixels;
    }

    public static boolean isDarkOrBrown(Color color) {
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();

        boolean isDark = (r < 90 && g < 90 && b < 90);

        boolean isBrown = (r > 80 && g > 40 && b < 100 && (r - g) > 20);

        return isDark || isBrown;
    }

    public static int getGradientColor(Color color1, Color color2, double step) {
        step = Math.max(0.0, Math.min(1.0, step));

        int r = (int) (color1.getRed() + (color2.getRed() - color1.getRed()) * step);
        int g = (int) (color1.getGreen() + (color2.getGreen() - color1.getGreen()) * step);
        int b = (int) (color1.getBlue() + (color2.getBlue() - color1.getBlue()) * step);
        int a = (int) (color1.getAlpha() + (color2.getAlpha() - color1.getAlpha()) * step);

        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}