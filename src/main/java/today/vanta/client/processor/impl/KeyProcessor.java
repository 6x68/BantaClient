package today.vanta.client.processor.impl;

import net.minecraft.client.gui.GuiChat;
import today.vanta.Vanta;
import today.vanta.client.event.impl.system.KeyboardEvent;
import today.vanta.client.processor.Processor;
import today.vanta.util.client.IClient;
import today.vanta.util.game.events.EventListen;
import today.vanta.util.game.events.EventPriority;

public class KeyProcessor extends Processor {
    @EventListen(priority = EventPriority.HIGHEST)
    private void onKey(KeyboardEvent event) {
        if (event.key == IClient.COMMAND_PREFIX_KEY) {
            mc.displayGuiScreen(new GuiChat(IClient.COMMAND_PREFIX));
        }

        Vanta.instance.moduleStorage.list.forEach(mod -> {
            if (event.key == mod.key) {
                mod.setEnabled(!mod.isEnabled());
            }
        });
    }

    public static KeyProcessor getInstance() {
        return Vanta.instance.processorStorage.getT(KeyProcessor.class);
    }
}