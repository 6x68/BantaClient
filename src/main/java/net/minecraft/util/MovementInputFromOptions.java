package net.minecraft.util;

import net.minecraft.client.settings.GameSettings;
import today.vanta.client.event.impl.game.player.MoveButtonEvent;
import today.vanta.client.event.impl.game.player.MoveInputEvent;
import today.vanta.client.event.impl.game.player.SneakInputEvent;

public class MovementInputFromOptions extends MovementInput {
    private final GameSettings gameSettings;

    public MovementInputFromOptions(GameSettings gameSettingsIn) {
        this.gameSettings = gameSettingsIn;
    }

    public void updatePlayerMoveState() {
        this.moveStrafe = 0.0F;
        this.moveForward = 0.0F;

        MoveButtonEvent moveButtonEvent = new MoveButtonEvent(
                this.gameSettings.keyBindForward.isKeyDown(),
                this.gameSettings.keyBindBack.isKeyDown(),
                this.gameSettings.keyBindLeft.isKeyDown(),
                this.gameSettings.keyBindRight.isKeyDown(),
                this.gameSettings.keyBindJump.isKeyDown(),
                this.gameSettings.keyBindSneak.isKeyDown()
        );
        moveButtonEvent.call();

        if (moveButtonEvent.forward) {
            ++this.moveForward;
        }

        if (moveButtonEvent.back) {
            --this.moveForward;
        }

        if (moveButtonEvent.left) {
            ++this.moveStrafe;
        }

        if (moveButtonEvent.right) {
            --this.moveStrafe;
        }

        this.jump = moveButtonEvent.jump;
        this.sneak = moveButtonEvent.sneak;

        MoveInputEvent moveInputEvent = new MoveInputEvent(this.moveForward, this.moveStrafe, this.jump, this.sneak, 0.3F);
        moveInputEvent.call();

        this.moveStrafe = moveInputEvent.strafe;
        this.moveForward = moveInputEvent.forward;
        this.jump = moveInputEvent.jumping;
        this.sneak = moveInputEvent.sneaking;

        if (this.sneak) {
            SneakInputEvent sneakInputEvent = new SneakInputEvent();
            sneakInputEvent.call();

            if (sneakInputEvent.cancelled) {
                return;
            }

            this.moveStrafe *= moveInputEvent.sneakFactor;
            this.moveForward *= moveInputEvent.sneakFactor;
        }
    }
}
