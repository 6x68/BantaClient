package today.vanta.client.module.impl.player;

import today.vanta.client.event.impl.game.world.UpdateEvent;
import today.vanta.client.module.Category;
import today.vanta.client.module.Module;
import today.vanta.client.setting.impl.NumberSetting;
import today.vanta.util.game.events.EventListen;

public class FastUse extends Module {
    private final NumberSetting
            rightDelay = NumberSetting.builder()
            .name("RMB delay")
            .value(0)
            .min(0)
            .max(1)
            .places(0)
            .build(),

    leftDelay = NumberSetting.builder()
            .name("LMB delay")
            .value(0)
            .min(0)
            .max(1)
            .places(0)
            .build();

    public FastUse() {
        super("FastUse", "Makes you use items faster", Category.PLAYER);
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
