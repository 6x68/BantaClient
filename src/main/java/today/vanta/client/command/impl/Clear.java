package today.vanta.client.command.impl;

import today.vanta.client.command.Command;

public class Clear extends Command {
    public Clear() {
        super("Clear", "Clears chat.");
        aliases = new String[]{"clear", "cc", "c"};
    }

    @Override
    public void execute(String[] args) {
        mc.ingameGUI.getChatGUI().clearChatMessages();
    }
}