package today.vanta.client.module.impl.misc;

import org.lwjgl.input.Keyboard;
import today.vanta.client.event.impl.game.world.UpdateEvent;
import today.vanta.client.module.Category;
import today.vanta.client.module.Module;
import today.vanta.util.game.events.EventListen;
import today.vanta.util.game.player.ChatUtil;
import today.vanta.util.system.math.Counter;

public class Preventer extends Module {
    int keyCodeNum;
    int times;
    boolean pressed;
    Counter counter = new Counter();
    public Preventer() {
        super("Preventer", "Prevents you from dropping items accidentally.", Category.MISC);
    }

    @EventListen
    public void onUpdate(UpdateEvent event) {
        if (String.valueOf(keyCodeNum).isEmpty()) {
            keyCodeNum = mc.gameSettings.keyBindDrop.getKeyCode();
        }
        mc.gameSettings.keyBindDrop.setKeyCode(0);
        if (Keyboard.isKeyDown(keyCodeNum) && !pressed) {
            times++;
            ChatUtil.send(ChatUtil.Prefix.INFO,"aaaa"+times);
            counter.reset();
        }

        if (!Keyboard.isKeyDown(keyCodeNum)) {
            pressed = false;
        }

        if (counter.getElapsedTime() < 1000) {
            times = 0;
        }

        if (times >= 3) {
            mc.gameSettings.keyBindDrop.pressed = true;
            mc.gameSettings.keyBindDrop.pressed = false;
        }
    }

    @Override
    public void onDisable() {
        mc.gameSettings.keyBindDrop.setKeyCode(keyCodeNum);
    }
}
