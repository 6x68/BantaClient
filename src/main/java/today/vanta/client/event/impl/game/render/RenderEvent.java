package today.vanta.client.event.impl.game.render;

import net.minecraft.client.gui.ScaledResolution;
import today.vanta.client.event.Event;

public class RenderEvent extends Event {
    public float partialTicks;
    public final ScaledResolution scaledResolution;
    public final boolean screen;

    public RenderEvent(float partialTicks, ScaledResolution scaledResolution, boolean screen) {
        this.partialTicks = partialTicks;
        this.scaledResolution = scaledResolution;
        this.screen = screen;
    }
}