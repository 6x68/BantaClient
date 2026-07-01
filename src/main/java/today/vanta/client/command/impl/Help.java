package today.vanta.client.command.impl;

import today.vanta.Vanta;
import today.vanta.client.command.Command;
import today.vanta.util.client.IClient;
import today.vanta.util.game.player.ChatUtil;

public class Help extends Command {
    public Help() {
        super("Help", "See helpful information about the client.");
        aliases = new String[]{"help", "?"};
    }

    @Override
    public void execute(String[] args) {
        ChatUtil.sendNoLine(ChatUtil.Prefix.INFO, "You're running version {}", IClient.CLIENT_VERSION);
        ChatUtil.sendNoLine(ChatUtil.Prefix.INFO, "Available commands:");
        for (Command command : Vanta.instance.commandStorage.list) {
            ChatUtil.sendNoLineNoPrefix("&6{}&e{} &f- &f{}{}", IClient.COMMAND_PREFIX, command.aliases[0], command.description, command.getArgs() != null ? " Usage:" : "");

            if (command.getArgs() == null) {
                continue;
            }

            for (String arg : command.getArgs()) {
                ChatUtil.sendNoLineNoPrefix(" &7- &6{}", arg);
            }
        }
    }
}