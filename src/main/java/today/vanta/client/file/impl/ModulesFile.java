package today.vanta.client.file.impl;

import com.google.gson.JsonObject;
import today.vanta.Vanta;
import today.vanta.client.file.File;
import today.vanta.client.module.Module;

public class ModulesFile extends File {
    public ModulesFile() {
        super("modules");
    }

    @Override
    protected JsonObject writeJson() {
        JsonObject moduleObject = new JsonObject();

        for (Module mod : Vanta.instance.moduleStorage.list) {
            JsonObject modObject = new JsonObject();

            modObject.addProperty("Key", mod.key);
            modObject.addProperty("Hidden", mod.hideFromArraylist);
            modObject.addProperty("Suffix", mod.addSuffix);
            modObject.addProperty("Savable", mod.addToConfig);
            modObject.addProperty("Expanded", mod.isExpanded());
            modObject.addProperty("Display name", mod.displayName);

            moduleObject.add(mod.name, modObject);
        }

        return moduleObject;
    }

    @Override
    protected void readJson(JsonObject json) {
        if (json == null) return;

        for (Module mod : Vanta.instance.moduleStorage.list) {
            if (!json.has(mod.name) || !json.get(mod.name).isJsonObject()) {
                continue;
            }

            JsonObject modObject = json.getAsJsonObject(mod.name);

            if (modObject.has("Key")) {
                mod.key = modObject.get("Key").getAsInt();
            }

            if (modObject.has("Hidden")) {
                mod.hideFromArraylist = modObject.get("Hidden").getAsBoolean();
            }

            if (modObject.has("Suffix")) {
                mod.addSuffix = modObject.get("Suffix").getAsBoolean();
            }

            if (modObject.has("Savable")) {
                mod.addToConfig = modObject.get("Savable").getAsBoolean();
            }

            if (modObject.has("Expanded")) {
                mod.setExpanded(modObject.get("Expanded").getAsBoolean());
            }

            if (modObject.has("Display name")) {
                mod.displayName = modObject.get("Display name").getAsString();
            }
        }
    }
}
