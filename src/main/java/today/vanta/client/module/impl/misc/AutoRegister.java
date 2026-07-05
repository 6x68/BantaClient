package today.vanta.client.module.impl.misc;

import today.vanta.client.event.impl.game.network.PrintChatMessage;
import today.vanta.client.module.Category;
import today.vanta.client.module.Module;
import today.vanta.util.game.events.EventListen;

public class AutoRegister extends Module {
    String password;
    public AutoRegister() {
        super("Auto Register", "Registers your account.", Category.MISC);
        displayNames = new String[] {"Auto Register", "Auto Login"};
    }

    @EventListen
    private void onPrintChat(PrintChatMessage event) {
        if (event.message.contains("/login <password>") || event.message.contains("/login password") && !password.isEmpty()) {
            mc.thePlayer.sendChatMessage("/login "+password);
        }
        if (event.message.contains("/register <password>") || event.message.contains("/register password") && !password.isEmpty()) {
            mc.thePlayer.sendChatMessage("/register "+password);
        }
    }
}
