package today.vanta.client.module.impl.client;

import today.vanta.client.event.impl.client.ModuleDisableEvent;
import today.vanta.client.event.impl.client.ModuleEnableEvent;
import today.vanta.client.event.impl.client.ModuleExpandedEvent;
import today.vanta.client.module.Category;
import today.vanta.client.module.Module;
import today.vanta.client.screen.ClickGUIScreen;
import today.vanta.client.setting.Setting;
import today.vanta.client.setting.impl.BooleanSetting;
import today.vanta.util.game.events.EventListen;
import today.vanta.util.game.sound.Sounds;

public class ClientSounds extends Module {
    private final BooleanSetting toggleSounds = Setting.of("Toggle sounds", true);
    private final BooleanSetting expandSounds = Setting.of("Expand sounds", true);

    public ClientSounds() {
        super("ClientSounds", "Client Sounds.", Category.CLIENT);
        displayNames = new String[]{"ClientSounds", "Sounds", "ToggleSounds"};
        hideFromArraylist = true;
    }

    @EventListen
    private void onModuleEnable(ModuleEnableEvent event) {
        if (!toggleSounds.getValue()) return;
        if (event.config || event.module instanceof ClickGUI) return;

        Sounds.ON.play();
    }

    @EventListen
    private void onModuleDisable(ModuleDisableEvent event) {
        if (!toggleSounds.getValue()) return;
        if (event.config || event.module instanceof ClickGUI) return;

        Sounds.OFF.play();
    }

    @EventListen
    private void onModuleExpand(ModuleExpandedEvent event) {
        if (!expandSounds.getValue()) return;
        if (event.config) return;
        if (!(mc.currentScreen instanceof ClickGUIScreen)) return;

        Sounds.OPEN.play();
    }
}
