package today.vanta.util.game.render.font;

import java.awt.*;

public interface IRenderer {
    float drawString(String text, float x, float y, int color, boolean shadow);

    default float drawString(String text, float x, float y, Color color, boolean shadow) {
        return drawString(text, x, y, color.getRGB(), shadow);
    }

    float drawStringWithShadow(String text, float x, float y, int color);

    float drawHorizontalGradientString(String text, float x, float y, Color startColor, Color endColor, double speed, int spacing);

    default float drawString(String text, float x, float y, int color) {
        return drawString(text, x, y, color, false);
    }

    default float drawString(String text, float x, float y, Color color) {
        return drawString(text, x, y, color.getRGB());
    }

    default float drawStringWithShadow(String text, float x, float y, Color color) {
        return drawStringWithShadow(text, x, y, color.getRGB());
    }

    default float drawYCenteredString(String text, float x, float y, Color color, boolean dropShadow) {
        return drawString(text, x, y - getFontHeight() / 2F, color.getRGB(), dropShadow);
    }

    int getFontHeight();

    int getStringWidth(String text);
}