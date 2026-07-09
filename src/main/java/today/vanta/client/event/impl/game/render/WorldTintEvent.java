package today.vanta.client.event.impl.game.render;

import today.vanta.client.event.Event;

public class WorldTintEvent extends Event {
    public final float partialTicks;

    public WorldTintEvent(float partialTicks) {
        this.partialTicks = partialTicks;
    }
}
