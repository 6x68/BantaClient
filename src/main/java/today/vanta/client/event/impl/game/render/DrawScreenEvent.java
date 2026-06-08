package today.vanta.client.event.impl.game.render;

import today.vanta.client.event.Event;

public class DrawScreenEvent extends Event {
    public final float mouseX, mouseY, partialTicks;

    public DrawScreenEvent(float mouseX, float mouseY, float partialTicks) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.partialTicks = partialTicks;
    }
}