package today.vanta.client.event.impl.game.world;

import today.vanta.client.event.Event;

public class SkyColorEvent extends Event {
    public float red;
    public float green;
    public float blue;
    public final float partialTicks;

    public SkyColorEvent(float red, float green, float blue, float partialTicks) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.partialTicks = partialTicks;
    }
}
