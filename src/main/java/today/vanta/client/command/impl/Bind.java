package today.vanta.client.command.impl;

import org.lwjgl.input.Keyboard;
import today.vanta.Vanta;
import today.vanta.client.command.Command;
import today.vanta.client.module.Module;
import today.vanta.util.game.player.ChatUtil;

public class Bind extends Command {
    public Bind() {
        super("Bind", "Bind modules.");
        aliases = new String[]{"bind", "b", "unbind", "ub"};
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 1 || args.length > 2) {
            ChatUtil.error("Usage: bind <module> [key]");
            return;
        }

        String moduleName = args[0].toLowerCase();
        if (moduleName.isEmpty()) {
            ChatUtil.error("Module name can't be empty!");
            return;
        }

        Module module = Vanta.instance.moduleStorage.getModule(moduleName);
        if (module == null) {
            ChatUtil.error("Module &c{}&f not found!", moduleName);
            return;
        }

        if (args.length == 1) {
            module.key = 0;
            send("Unbound &e{}&f!", module.name);
            return;
        }

        String key = args[1].toUpperCase();

        int keyCode = Keyboard.getKeyIndex(key);

        if (keyCode == Keyboard.KEY_SPACE
                || keyCode == Keyboard.KEY_ESCAPE
                || keyCode == Keyboard.KEY_BACK) {

            module.key = 0;
            send("Unbound &e{}&f!", module.name);
            return;
        } else if (keyCode == Keyboard.KEY_NONE) {
            ChatUtil.error("Unknown key &e{}&f!", key);
            return;
        }

        String keyName = Keyboard.getKeyName(keyCode);
        module.key = keyCode;
        send("Bound &e{}&f to &e{}&f!", module.name, keyName);
    }

    @Override
    public String[] getArgs() {
        return new String[]{
                "bind <module> <key>",
                "unbind <module>"
        };
    }
}
