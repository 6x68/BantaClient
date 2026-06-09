package today.vanta.client.module.impl.hud.arraylist;

import today.vanta.util.game.render.font.IRenderer;

public class GlyphRenderer extends ArraylistRenderer {
    public GlyphRenderer(IRenderer renderer) {
        super(renderer);
    }

    @Override
    public float getFontHeight() {
        return renderer.getFontHeight();
    }
}
