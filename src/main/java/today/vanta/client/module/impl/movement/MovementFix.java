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

public class MovementFix extends Module {
    public MovementFix() {
        super("MovementFix", "Fixes your yaw when in combat", Category.MOVEMENT);
        displayNames = new String[] {"MovementFix", "MovementCorrection", "CorrectMovement", "MoveFix"};
    }

    @EventListen
    public void onMoveInput(MoveInputEvent event) {
        MovementUtil.correctMovement(event, Vanta.instance.processorStorage.getT(RotationProcessor.class).rotations.yaw);
    }

    @EventListen
    public void onStrafe(MoveFlyingEvent event) {
        event.yaw = Vanta.instance.processorStorage.getT(RotationProcessor.class).rotations.yaw;
    }

    @EventListen
    public void onJump(JumpEvent event) {
        event.yaw = Vanta.instance.processorStorage.getT(RotationProcessor.class).rotations.yaw;
    }
}