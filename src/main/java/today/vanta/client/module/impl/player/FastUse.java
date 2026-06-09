package today.vanta.client.module.impl.player;

import today.vanta.client.event.impl.game.world.UpdateEvent;
import today.vanta.client.module.Category;
import today.vanta.client.module.Module;
import today.vanta.client.setting.Setting;
import today.vanta.client.setting.impl.NumberSetting;
import today.vanta.util.game.events.EventListen;

public class FastUse extends Module {
    private final NumberSetting
            rightDelay = Setting.of("RMB delay", 0, 0, 4),
            leftDelay = Setting.of("LMB delay", 0, 0, 10);

    public FastUse() {
        super("FastUse", "Makes you use items faster.", Category.PLAYER);
        displayNames = new String[]{"FastUse", "FastPlace", "NoClickDelay"};
    }

    @EventListen
    private void onUpdate(UpdateEvent event) {
        mc.rightClickDelayTimer = rightDelay.getValue().intValue();
        mc.leftClickCounter = leftDelay.getValue().intValue();
    }

    @Override
    public void onDisable() {
        mc.rightClickDelayTimer = 4;
        mc.leftClickCounter = 10;
    }
}