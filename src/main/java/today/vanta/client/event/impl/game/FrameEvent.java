package today.vanta.client.event.impl.game;

import today.vanta.client.event.Event;

public class FrameEvent extends Event {
    private final float ticks;

    public FrameEvent(float ticks) {
        this.ticks = ticks;
    }

    public float getTicks() {
        return ticks;
    }
}
