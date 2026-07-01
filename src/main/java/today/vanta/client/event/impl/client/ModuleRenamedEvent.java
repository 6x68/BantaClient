package today.vanta.client.event.impl.client;

import today.vanta.client.event.Event;
import today.vanta.client.module.Module;

public class ModuleRenamedEvent extends Event {
    public Module module;

    public ModuleRenamedEvent(Module module) {
        this.module = module;
    }
}