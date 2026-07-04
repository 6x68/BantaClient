package today.vanta.client.file.impl;

import com.google.gson.*;
import today.vanta.Vanta;
import today.vanta.client.file.File;
import today.vanta.client.module.Module;
import today.vanta.client.setting.Setting;
import today.vanta.client.setting.impl.BooleanSetting;
import today.vanta.client.setting.impl.MultiStringSetting;
import today.vanta.client.setting.impl.NumberSetting;
import today.vanta.client.setting.impl.StringSetting;

public class ConfigFile extends File {
    public ConfigFile(String name) {
        super("configs/" + name);
    }

    public ConfigFile() {
        super("configs/default");
    }

    @Override
    protected JsonObject writeJson() {
        JsonObject config = new JsonObject();

        for (Module mod : Vanta.instance.moduleStorage.list) {
            JsonObject modObject = new JsonObject();

            modObject.addProperty("Enabled", mod.isEnabled());

            if (mod.addToConfig) {
                for (Setting<?> setting : mod.settings) {
                    JsonElement value = JsonNull.INSTANCE;

                    if (setting instanceof BooleanSetting) {
                        value = new JsonPrimitive(((BooleanSetting) setting).getValue());
                    } else if (setting instanceof StringSetting) {
                        value = new JsonPrimitive(((StringSetting) setting).getValue());
                    } else if (setting instanceof NumberSetting) {
                        value = new JsonPrimitive(((NumberSetting) setting).getValue());
                    } else if (setting instanceof MultiStringSetting) {
                        value = new Gson().toJsonTree(((MultiStringSetting) setting).getValue());
                    }

                    modObject.add(setting.name, value);
                }
            }

            config.add(mod.name, modObject);
        }

        return config;
    }

    @Override
    protected void readJson(JsonObject json) {
        if (json == null) return;

        for (Module mod : Vanta.instance.moduleStorage.list) {
            if (!json.has(mod.name) || !json.get(mod.name).isJsonObject()) {
                continue;
            }

            JsonObject modObject = json.getAsJsonObject(mod.name);

            mod.setEnabled(false, true);

            if (mod.addToConfig) {
                for (Setting<?> setting : mod.settings) {
                    if (!modObject.has(setting.name)) {
                        continue;
                    }

                    JsonElement value = modObject.get(setting.name);

                    if (setting instanceof BooleanSetting) {
                        ((BooleanSetting) setting).setValue(value.getAsBoolean());
                    } else if (setting instanceof StringSetting) {
                        ((StringSetting) setting).setValue(value.getAsString());
                    } else if (setting instanceof NumberSetting) {
                        ((NumberSetting) setting).setValue(value.getAsDouble());
                    } else if (setting instanceof MultiStringSetting) {
                        MultiStringSetting multi = (MultiStringSetting) setting;
                        multi.setValue(new Gson().fromJson(value, multi.getValue().getClass()));
                    }
                }
            }

            if (modObject.has("Enabled")) {
                mod.setEnabled(modObject.get("Enabled").getAsBoolean(), true);
            }
        }
    }
}
