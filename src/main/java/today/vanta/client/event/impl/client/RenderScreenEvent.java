package today.vanta.client.event.impl.client;

import today.vanta.client.event.Event;
import today.vanta.util.game.render.Renderable;

public class RenderScreenEvent extends Event implements Renderable {
    public final float mouseX, mouseY, partialTicks;

    public RenderScreenEvent(float mouseX, float mouseY, float partialTicks) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.partialTicks = partialTicks;
    }
}