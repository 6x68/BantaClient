package today.vanta.client.module.impl.hud;

import today.vanta.client.module.Category;
import today.vanta.client.module.Module;
import today.vanta.client.setting.Setting;
import today.vanta.client.setting.impl.BooleanSetting;
import today.vanta.client.setting.impl.NumberSetting;
import today.vanta.client.setting.impl.StringSetting;

public class WindowSettings extends Module {
    public final StringSetting outline = Setting.of("Outline mode", "None", "Horizontal gradient", "Vertical gradient", "Primary", "Secondary", "None");
    public final StringSetting textAlignment = Setting.of("Title alignment", "Left", "Center", "Left");
    public WindowSettings() {
        super("Window Settings", "Let's you customise the window rects.", Category.HUD);
        frozen = true;
    }
}
