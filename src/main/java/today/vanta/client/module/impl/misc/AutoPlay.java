package today.vanta.client.module.impl.misc;

import today.vanta.client.event.impl.game.network.PrintChatMessage;
import today.vanta.client.module.Category;
import today.vanta.client.module.Module;
import today.vanta.client.setting.Setting;
import today.vanta.client.setting.impl.StringSetting;
import today.vanta.util.game.events.EventListen;

public class AutoPlay extends Module {
    private final StringSetting mode = Setting.of("Mode", "Miniblox", "Miniblox");

    public AutoPlay() {
        super("AutoPlay", "Auto Queues.", Category.MISC);
    }

    @EventListen
    private void onPrintChatMessage(PrintChatMessage event) {
        String message = event.message;

        if (mode.isValue("Miniblox")) {
            if (message.contains("§6§lQueueing next game in 15 seconds (or click Next Game to queue immediately)...")) {
                mc.thePlayer.sendChatMessage("/server skywars");
            }
        }
    }
}
