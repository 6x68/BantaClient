package today.vanta.client.event.impl.game.render;

import net.minecraft.client.gui.ScaledResolution;
import today.vanta.client.event.Event;

public class RenderEvent extends Event {
    public float partialTicks;
    public ScaledResolution scaledResolution;

    public RenderEvent(float partialTicks, ScaledResolution scaledResolution) {
        this.partialTicks = partialTicks;
        this.scaledResolution = scaledResolution;
    }
}