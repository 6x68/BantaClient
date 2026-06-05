package today.vanta.client.event.impl.game.player;

import today.vanta.client.event.Event;

public class JumpEvent extends Event {
    public float yaw;

    public JumpEvent(float yaw) {
        this.yaw = yaw;
    }
}