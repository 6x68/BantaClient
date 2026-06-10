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
import today.vanta.client.setting.Setting;
import today.vanta.client.setting.impl.BooleanSetting;
import today.vanta.client.setting.impl.MultiStringSetting;
import today.vanta.util.game.events.EventListen;
import today.vanta.util.game.player.MovementUtil;
import today.vanta.util.game.player.constructors.Rotation;

public class MovementFix extends Module {
    private final BooleanSetting silent = Setting.of("Silent", true);
    private final MultiStringSetting exemptions = Setting.of("Exemptions", new String[]{"Scaffold"}, new String[]{"KillAura", "Scaffold"});

    public MovementFix() {
        super("MovementFix", "Fixes your movement when in combat.", Category.MOVEMENT);
        displayNames = new String[]{"MovementFix", "MovementCorrection", "CorrectMovement", "MoveFix"};
    }

    @EventListen
    private void onMoveInput(MoveInputEvent event) {
        if (!shouldFix()) return;
        if (isExempted()) return;
        if (!silent.getValue()) return;
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
        if (!shouldFix()) return;
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

    @Override
    public String getSuffix() {
        return silent.getValue() ? "Silent" : null;
    }
}