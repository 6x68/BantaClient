package today.vanta.client.event.impl.game.player;

import today.vanta.client.event.Event;

public class MoveInputEvent extends Event {
    public float forward, strafe;
    public boolean jumping, sneaking;
    public float sneakFactor;

    public MoveInputEvent(float forward, float strafe, boolean jumping, boolean sneaking, float sneakFactor) {
        this.forward = forward;
        this.strafe = strafe;
        this.jumping = jumping;
        this.sneaking = sneaking;
        this.sneakFactor = sneakFactor;
    }
}