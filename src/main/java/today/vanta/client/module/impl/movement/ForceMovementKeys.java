package today.vanta.client.module.impl.movement;

import org.lwjgl.input.Keyboard;
import today.vanta.client.event.impl.game.world.UpdateEvent;
import today.vanta.client.module.Category;
import today.vanta.client.module.Module;
import today.vanta.util.game.events.EventListen;

public class ForceMovementKeys extends Module {
    int forwardKey;
    int jumpKey;
    int leftKey;
    int rightKey;
    int backKey;
    public ForceMovementKeys() {
        super("ForceMovementKeys", "Forces Movement keys.", Category.MOVEMENT);
    }
    @EventListen
    public void onUpdate(UpdateEvent event) {
        if (mc.currentScreen != null) {return;}
        forwardKey = mc.gameSettings.keyBindForward.getKeyCode();
        if (Keyboard.isKeyDown(forwardKey) && !mc.gameSettings.keyBindForward.isKeyDown()) {
            mc.gameSettings.keyBindForward.pressed = true;
        }
        if (!Keyboard.isKeyDown(forwardKey)) {
            mc.gameSettings.keyBindForward.pressed = false;
        }
        backKey = mc.gameSettings.keyBindBack.getKeyCode();
        if (Keyboard.isKeyDown(backKey) && !mc.gameSettings.keyBindBack.isKeyDown()) {
            mc.gameSettings.keyBindForward.pressed = true;
        }
        if (!Keyboard.isKeyDown(backKey)) {
            mc.gameSettings.keyBindBack.pressed = false;
        }
        leftKey = mc.gameSettings.keyBindLeft.getKeyCode();
        if (Keyboard.isKeyDown(leftKey) && !mc.gameSettings.keyBindLeft.isKeyDown()) {
            mc.gameSettings.keyBindLeft.pressed = true;
        }
        if (!Keyboard.isKeyDown(leftKey)) {
            mc.gameSettings.keyBindLeft.pressed = false;
        }
        rightKey = mc.gameSettings.keyBindRight.getKeyCode();
        if (Keyboard.isKeyDown(rightKey) && !mc.gameSettings.keyBindRight.isKeyDown()) {
            mc.gameSettings.keyBindRight.pressed = true;
        }
        if (!Keyboard.isKeyDown(rightKey)) {
            mc.gameSettings.keyBindRight.pressed = false;
        }
        jumpKey = mc.gameSettings.keyBindJump.getKeyCode();
        if (Keyboard.isKeyDown(jumpKey) && !mc.gameSettings.keyBindJump.isKeyDown()) {
            mc.gameSettings.keyBindJump.pressed = true;
        }
        if (!Keyboard.isKeyDown(jumpKey)) {
            mc.gameSettings.keyBindJump.pressed = false;
        }
    }
}
