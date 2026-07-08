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
            modObject.addProperty("Hidden", mod.hideFromArraylistSetting.getValue());
            modObject.addProperty("Suffix", mod.addSuffixSetting.getValue());
            modObject.addProperty("Savable", mod.addToConfigSetting.getValue());
            modObject.addProperty("Expanded", mod.isExpanded());
            modObject.addProperty("Display name", mod.displayNameSetting.getValue());

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
                mod.hideFromArraylistSetting.setValue(modObject.get("Hidden").getAsBoolean());
            }

            if (modObject.has("Suffix")) {
                mod.addSuffixSetting.setValue(modObject.get("Suffix").getAsBoolean());
            }

            if (modObject.has("Savable")) {
                mod.addToConfigSetting.setValue(modObject.get("Savable").getAsBoolean());
            }

            if (modObject.has("Expanded")) {
                mod.setExpanded(modObject.get("Expanded").getAsBoolean(), true);
            }

            if (modObject.has("Display name")) {
                mod.displayNameSetting.setValue(modObject.get("Display name").getAsString());
            }
        }
    }
}
