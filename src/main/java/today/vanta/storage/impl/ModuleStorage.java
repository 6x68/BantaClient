package today.vanta.storage.impl;

import today.vanta.client.event.impl.system.KeyboardEvent;
import today.vanta.client.module.Category;
import today.vanta.client.module.Module;
import today.vanta.client.module.impl.client.*;
import today.vanta.client.module.impl.hud.*;
import today.vanta.client.module.impl.misc.ClientBrand;
import today.vanta.client.module.impl.misc.Disabler;
import today.vanta.client.module.impl.misc.Timer;
import today.vanta.client.module.impl.movement.*;
import today.vanta.client.module.impl.combat.*;
import today.vanta.client.module.impl.player.*;
import today.vanta.client.module.impl.render.*;
import today.vanta.storage.Storage;
import today.vanta.util.game.events.EventListen;
import today.vanta.util.game.events.EventPriority;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ModuleStorage extends Storage<Module> {
    public Module context;

    public List<String> changelog = new ArrayList<>();

    public ModuleStorage() {
        changelog.add("[+] Added 'Custom' speed mode");
        changelog.add("[+] Added Jesus");
    }

    @Override
    public void subscribe() {
        super.subscribe();

        // Client
        list.add(new ClickGUI());
        list.add(new Theme());
        list.add(new AutoDisable());

        // Combat
        list.add(new AntiBot());
        list.add(new Criticals());
        list.add(new KillAura());
        list.add(new Velocity());
        list.add(new BlockHit());
        list.add(new KeepSprint());
        list.add(new TriggerBot());

        // Movement
        list.add(new Sprint());
        list.add(new LongJump());
        list.add(new Speed());
        list.add(new MovementFix());
        list.add(new NoSlowdown());
        list.add(new FastSneak());
        list.add(new Fly());
        list.add(new Jesus());
        list.add(new NoJumpDelay());

        // Player
        list.add(new Scaffold());
        list.add(new FastUse());

        // Render
        list.add(new ESP());
        list.add(new Ambience());
        list.add(new Animations());
        list.add(new Nametags());

        // Misc
        list.add(new Disabler());
        list.add(new ClientBrand());
        list.add(new Timer());

        //Hud
        list.add(new Arraylist());
        list.add(new Watermark());
        list.add(new TargetHUD());
        list.add(new BlockCounter());
        list.add(new Crosshair());
        list.add(new TargetList());

        this.context = null;
    }

    @EventListen(priority = EventPriority.HIGHEST)
    private void onKey(KeyboardEvent event) {
        list.forEach(mod -> {
            if (event.key == mod.key) {
                mod.setEnabled(!mod.isEnabled());
            }
        });
    }

    public List<Module> getModulesByCategory(Category input) {
        return this.list.stream().filter(mod ->
                mod.category.equals(input)).collect(Collectors.toList()
        );
    }

    public Module getModule(String input) {
        return this.list.stream().filter(m -> m.name.equalsIgnoreCase(input)).findFirst().orElse(null);
    }
}