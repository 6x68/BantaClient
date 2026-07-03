package today.vanta.client.event.impl.game.player;

import today.vanta.client.event.Event;

public class MoveButtonEvent extends Event {
    public boolean forward, back, left, right, jump, sneak;

    public MoveButtonEvent(boolean forward, boolean back, boolean left, boolean right, boolean jump, boolean sneak) {
        this.forward = forward;
        this.back = back;
        this.left = left;
        this.right = right;
        this.jump = jump;
        this.sneak = sneak;
    }
}