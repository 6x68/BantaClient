package today.vanta.client.event.impl.game.render;

import net.minecraft.client.renderer.ItemRenderer;
import today.vanta.client.event.Event;

public class RenderItemSwingEvent extends Event {
    public final ItemRenderer renderer;
    public final float partialTicks;
    public float equippedProgress, swingProgress;

    public RenderItemSwingEvent(ItemRenderer renderer, float partialTicks, float equippedProgress, float swingProgress) {
        this.renderer = renderer;
        this.partialTicks = partialTicks;
        this.equippedProgress = equippedProgress;
        this.swingProgress = swingProgress;
    }
}