package today.vanta.client.command.impl;

import today.vanta.Vanta;
import today.vanta.client.command.Command;
import today.vanta.client.module.Module;
import today.vanta.util.game.player.ChatUtil;

public class Toggle extends Command {
    public Toggle() {
        super("Toggle", "Toggle modules.");
        aliases = new String[]{"toggle", "t"};
    }

    @Override
    public void execute(String[] args) {
        if (args.length != 1) {
            ChatUtil.error("Missing argument &c<module>");
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

        module.setEnabled(!module.isEnabled());
        send("{} &e{}", module.isEnabled() ? "&aEnabled" : "&cDisabled", module.name);
    }

    @Override
    public String[] getArgs() {
        return new String[]{"toggle <module>"};
    }
}
