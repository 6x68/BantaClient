package today.vanta.client.event.impl.game.render;

import today.vanta.client.event.Event;

public class FogColorEvent extends Event {
    public float red;
    public float green;
    public float blue;
    public final float partialTicks;

    public FogColorEvent(float red, float green, float blue, float partialTicks) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.partialTicks = partialTicks;
    }
}
