package today.vanta.client.module.impl.misc;

import today.vanta.client.event.impl.game.network.PrintChatMessage;
import today.vanta.client.module.Category;
import today.vanta.client.module.Module;
import today.vanta.util.game.events.EventListen;

public class AutoRegister extends Module {
    private final String password = "buttSex**99.,11";

    public AutoRegister() {
        super("AutoRegister", "Registers your account.", Category.MISC);
        displayNames = new String[] {"AutoRegister", "AutoLogin", "AutoAuth"};
    }

    @EventListen
    private void onPrintChatMessage(PrintChatMessage event) {
        String message = event.message;
        if (message.contains("/login <password>") || message.contains("/login password")) {
            mc.thePlayer.sendChatMessage("/login "+password);
        }

        if (message.contains("/register <password>") || message.contains("/register password")) {
            mc.thePlayer.sendChatMessage("/register "+password);
        }
    }
}
