package today.vanta.client.event.impl.game.player;

import today.vanta.client.event.Event;

public class MoveFlyingEvent extends Event {
    public float yaw, strafe, forward, friction;

    public MoveFlyingEvent(float yaw, float strafe, float forward, float friction) {
        this.yaw = yaw;
        this.strafe = strafe;
        this.forward = forward;
        this.friction = friction;
    }
}