package today.vanta.client.module.impl.client;

import today.vanta.client.event.impl.client.ModuleDisableEvent;
import today.vanta.client.event.impl.client.ModuleEnableEvent;
import today.vanta.client.module.Category;
import today.vanta.client.module.Module;
import today.vanta.client.setting.Setting;
import today.vanta.client.setting.impl.BooleanSetting;
import today.vanta.util.game.events.EventListen;
import today.vanta.util.game.sound.Sounds;

public class ClientSounds extends Module {
    private final BooleanSetting toggleSounds = Setting.of("Toggle Sounds", true);
    public ClientSounds() {
        super("Sounds", "Client Sounds.", Category.CLIENT);
        hideFromArraylist = true;
    }

    @EventListen
    private void onModuleEnable(ModuleEnableEvent event) {
        if (toggleSounds.getValue()) {
            Sounds.ON.play();
        }
    }

    @EventListen
    private void onModuleDisable(ModuleDisableEvent event) {
        if (toggleSounds.getValue()) {
            Sounds.OFF.play();
        }
    }

}
