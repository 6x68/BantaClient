package today.vanta.client.event.impl.client;

import net.minecraft.client.gui.ScaledResolution;
import today.vanta.client.event.Event;
import today.vanta.util.game.render.Renderable;

public class RenderOverlayEvent extends Event implements Renderable {
    public float partialTicks;
    public ScaledResolution scaledResolution;

    public RenderOverlayEvent(float partialTicks, ScaledResolution scaledResolution) {
        this.partialTicks = partialTicks;
        this.scaledResolution = scaledResolution;
    }
}