package today.vanta.client.module.impl.client;

import today.vanta.client.module.Category;
import today.vanta.client.module.Module;
import today.vanta.client.setting.Setting;
import today.vanta.client.setting.impl.BooleanSetting;
import today.vanta.client.setting.impl.NumberSetting;

public class ClickSettings extends Module {
    public final NumberSetting
            minCps = Setting.of("Min CPS", 10, 1, 20),
            maxCps = Setting.of("Max CPS", 11, 1, 20),
            jitter = Setting.of("Jitter", 15, 0, 50, 1, "%"),
            hurtBoost = Setting.of("Hurt boost", 30, 0, 100, 1, "%"),
            flickChance = Setting.of("Flick chance", 5, 0, 100, 1, "%"),
            flickBoost = Setting.of("Flick boost", 25, 0, 100, 1, "%"),
            fatigueChance = Setting.of("Fatigue chance", 3, 0, 100, 1, "%"),
            fatigueSlowdown = Setting.of("Fatigue slowdown", 20, 0, 100, 1, "%");

    public final BooleanSetting
            hurtReaction = Setting.of("Hurt reaction", true),
            flicks = Setting.of("Flicks", true);

    public ClickSettings() {
        super("ClickSettings", "Configure combat clicking behavior.", Category.CLIENT);
        frozen = true;

        maxCps.addListener((setting, oldValue, newValue) -> {
            if (newValue.intValue() < minCps.getValue().intValue()) {
                setting.setValue(minCps.getValue());
            }
        });

        minCps.addListener((setting, oldValue, newValue) -> {
            if (newValue.intValue() > maxCps.getValue().intValue()) {
                setting.setValue(maxCps.getValue());
            }
        });
    }
}