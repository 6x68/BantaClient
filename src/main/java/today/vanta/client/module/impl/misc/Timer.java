package today.vanta.client.module.impl.misc;

import today.vanta.client.event.impl.game.world.UpdateEvent;
import today.vanta.client.module.Category;
import today.vanta.client.module.Module;
import today.vanta.client.setting.Setting;
import today.vanta.client.setting.impl.NumberSetting;
import today.vanta.util.game.events.EventListen;

public class Timer extends Module {
    private final NumberSetting gameSpeed = Setting.of("Game speed", 0.5, 0.1, 5, 1);

    public Timer() {
        super("Timer", "Modify the game tick speed.", Category.MISC);
    }

    @EventListen
    private void onUpdate(UpdateEvent event) {
        mc.timer.timerSpeed = gameSpeed.getValue().floatValue();
    }

    @Override
    public void onDisable() {
        if (mc.theWorld == null) return;
        mc.timer.timerSpeed = 1.0f;
    }
}