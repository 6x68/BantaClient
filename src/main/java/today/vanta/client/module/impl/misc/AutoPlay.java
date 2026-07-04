package today.vanta.client.module.impl.misc;

import net.minecraft.util.ChatComponentText;
import today.vanta.client.event.impl.game.network.PrintChatMessage;
import today.vanta.client.event.impl.game.player.SendChatMessageEvent;
import today.vanta.client.event.impl.game.world.UpdateEvent;
import today.vanta.client.module.Category;
import today.vanta.client.module.Module;
import today.vanta.client.setting.Setting;
import today.vanta.client.setting.impl.StringSetting;
import today.vanta.util.game.events.EventListen;
import today.vanta.util.game.player.ChatUtil;

public class AutoPlay extends Module {
    private final StringSetting mode = Setting.of("Mode", "Miniblox", "Miniblox");
    public AutoPlay() {
        super("Auto Play", "Auto Queues.", Category.MISC);
    }
    @EventListen
    private void onUpdate(UpdateEvent event) {

    }

    @EventListen
    private void onChat(PrintChatMessage event) {
        if (event.message.contains("§6§lQueueing next game in 15 seconds (or click Next Game to queue immediately)...")) {
            mc.thePlayer.addChatMessage(new ChatComponentText("/server skywars"));
        }
    }
}
