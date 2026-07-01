package today.vanta.client.module.impl.movement;

import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;
import today.vanta.client.event.impl.game.world.UpdateEvent;
import today.vanta.client.module.Category;
import today.vanta.client.module.Module;
import today.vanta.util.game.events.EventListen;

public class SaveMoveKeys extends Module {
    public SaveMoveKeys() {
        super("SaveMoveKeys", "Forces movement keys.", Category.MOVEMENT);
        displayNames = new String[]{"SaveMoveKeys", "ForceMovementKeys"};
    }

    @EventListen
    public void onUpdate(UpdateEvent event) {
        if (mc.currentScreen != null) {
            return;
        }

        updateKey(mc.gameSettings.keyBindForward);
        updateKey(mc.gameSettings.keyBindBack);
        updateKey(mc.gameSettings.keyBindLeft);
        updateKey(mc.gameSettings.keyBindRight);
        updateKey(mc.gameSettings.keyBindJump);
    }

    private void updateKey(KeyBinding keyBinding) {
        keyBinding.pressed = Keyboard.isKeyDown(keyBinding.getKeyCode());
    }
}
