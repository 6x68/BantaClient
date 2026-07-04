package today.vanta.client.event.impl.client;

import today.vanta.client.event.Event;
import today.vanta.client.module.Module;

public class ModuleExpandedEvent extends Event {
    public Module module;
    public boolean config = false;

    public ModuleExpandedEvent(Module module) {
        this.module = module;
    }

    public ModuleExpandedEvent(Module module, boolean config) {
        this.module = module;
        this.config = config;
    }
}