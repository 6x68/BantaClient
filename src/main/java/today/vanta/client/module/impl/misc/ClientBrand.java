package today.vanta.client.module.impl.misc;

import today.vanta.client.event.impl.game.ClientBrandEvent;
import today.vanta.client.module.Category;
import today.vanta.client.module.Module;
import today.vanta.client.setting.impl.StringSetting;
import today.vanta.util.game.events.EventListen;
import today.vanta.util.game.player.ChatUtil;

public class ClientBrand extends Module {
    private boolean message;

    private final StringSetting brand = StringSetting.builder()
            .name("Brand")
            .value("vanilla")
            .values("vanilla")
            .build();

    public ClientBrand() {
        super("ClientBrand", "Changes the clients brand.", Category.MISC);

        brand.addListener(((setting, oldValue, newValue) -> {
            if (!message && mc.thePlayer != null) {
                ChatUtil.warn("You must rejoin for the brand to show up!");
                message = true;
            }
        }));
    }

    @EventListen
    private void onUpdate(ClientBrandEvent event) {
        event.brand = brand.getValue();
    }
}