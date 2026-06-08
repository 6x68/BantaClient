package today.vanta.client.module.impl.misc;

import com.sun.security.ntlm.Client;
import net.minecraft.client.entity.EntityPlayerSP;
import today.vanta.client.event.impl.game.world.UpdateEvent;
import today.vanta.client.module.Category;
import today.vanta.client.module.Module;
import today.vanta.client.setting.impl.StringSetting;
import today.vanta.util.game.events.EventListen;

public class ClientBrand extends Module {
    public final StringSetting brand = StringSetting.builder()
            .name("Brand")
            .value("vanilla")
            .values("Vanta", "vanilla", "Vanta V1", "God Bypass Client", "billionare client", "Rise Client", "Rise", "Asstralis", "Cornelius Hubert", "hello", "Benjamin Netanyahu Client", "Israel Supporter", "Mossad Agent", "I'm obfuscating Dog Client right now", "Military Grade Bypass", "Big Ben Yahu", "Epstein Enjoyer", "I live on Epstein Island", "EFN", "Rock", "Dort","§0§lEPSTEIN")
            .build();
    public ClientBrand() {
        super("ClientBrand", "Changes the clients brand", Category.MISC);
    }
    @EventListen
    public void onUpdate(UpdateEvent event) {
        mc.thePlayer.setClientBrand(brand.getValue());
    }
}
