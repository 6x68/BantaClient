package today.vanta.client.module.impl.movement;

import today.vanta.Vanta;
import today.vanta.client.event.impl.game.player.JumpEvent;
import today.vanta.client.event.impl.game.player.MoveFlyingEvent;
import today.vanta.client.event.impl.game.player.MoveInputEvent;
import today.vanta.client.module.Category;
import today.vanta.client.module.Module;
import today.vanta.client.module.impl.combat.KillAura;
import today.vanta.client.module.impl.player.Scaffold;
import today.vanta.client.processor.impl.RotationProcessor;
import today.vanta.client.processor.impl.TargetProcessor;
import today.vanta.client.setting.impl.MultiStringSetting;
import today.vanta.util.game.events.EventListen;
import today.vanta.util.game.player.MovementUtil;
import today.vanta.util.game.player.constructors.Rotation;

public class MovementFix extends Module {
    private final MultiStringSetting exemptions = MultiStringSetting.builder()
            .name("Exemptions")
            .value("Scaffold")
            .values("KillAura", "Scaffold")
            .build();

    public MovementFix() {
        super("MovementFix", "Fixes your yaw when in combat", Category.MOVEMENT);
        displayNames = new String[] {"MovementFix", "MovementCorrection", "CorrectMovement", "MoveFix"};
    }

    @EventListen
    private void onMoveInput(MoveInputEvent event) {
        if (!shouldFix()) return;
        if (isExempted()) return;
        MovementUtil.correctMovement(event, getRotations().yaw);
    }

    @EventListen
    private void onStrafe(MoveFlyingEvent event) {
        if (!shouldFix()) return;
        if (isExempted()) return;
        event.yaw = getRotations().yaw;
    }

    @EventListen
    private void onJump(JumpEvent event) {
        if (getRotations() == null) return;
        if (isExempted()) return;
        event.yaw = getRotations().yaw;
    }

    private boolean shouldFix() {
        return getRotations() != null && (TargetProcessor.getInstance().target != null || TargetProcessor.getInstance().cache != null);
    }

    private Rotation getRotations() {
        return RotationProcessor.getInstance().rotations;
    }

    private boolean isExempted() {
        boolean scaffoldEnabled = exemptions.isEnabled("Scaffold")
                && Vanta.instance.moduleStorage.getT(Scaffold.class).isEnabled();

        boolean killAuraEnabled = exemptions.isEnabled("KillAura")
                && Vanta.instance.moduleStorage.getT(KillAura.class).isEnabled();

        if (scaffoldEnabled) {
            return true;
        }

        if (killAuraEnabled) {
            return true;
        }

        return false;
    }
}