package today.vanta.client.module.impl.movement;

import today.vanta.Vanta;
import today.vanta.client.event.impl.game.player.JumpEvent;
import today.vanta.client.event.impl.game.player.MoveFlyingEvent;
import today.vanta.client.event.impl.game.player.MoveInputEvent;
import today.vanta.client.module.Category;
import today.vanta.client.module.Module;
import today.vanta.client.processor.impl.RotationProcessor;
import today.vanta.util.game.events.EventListen;
import today.vanta.util.game.player.MovementUtil;
import today.vanta.util.game.player.constructors.Rotation;

public class MovementFix extends Module {
    public MovementFix() {
        super("MovementFix", "Fixes your yaw when in combat", Category.MOVEMENT);
        displayNames = new String[] {"MovementFix", "MovementCorrection", "CorrectMovement", "MoveFix"};
    }

    @EventListen
    private void onMoveInput(MoveInputEvent event) {
        if (getRotations() == null) return;
        MovementUtil.correctMovement(event, getRotations().yaw);
    }

    @EventListen
    private void onStrafe(MoveFlyingEvent event) {
        if (getRotations() == null) return;
        event.yaw = getRotations().yaw;
    }

    @EventListen
    private void onJump(JumpEvent event) {
        if (getRotations() == null) return;
        event.yaw = getRotations().yaw;
    }

    private Rotation getRotations() {
        return RotationProcessor.getInstance().rotations;
    }
}