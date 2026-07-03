package today.vanta.client.file.impl;

import com.google.gson.JsonObject;
import today.vanta.Vanta;
import today.vanta.client.file.File;
import today.vanta.client.module.Category;
import today.vanta.client.screen.ClickGUIScreen;

public class ScreenFile extends File {
    public ScreenFile() {
        super("screens");
    }

    @Override
    protected JsonObject writeJson() {
        JsonObject screensObject = new JsonObject();

        ClickGUIScreen cgui = Vanta.instance.screenStorage.getT(ClickGUIScreen.class);
        if (cgui != null) {
            JsonObject cguiObject = new JsonObject();
            for (Category cat : Category.values()) {
                JsonObject catObject = new JsonObject();
                catObject.addProperty("X", cat.position.x);
                catObject.addProperty("Y", cat.position.y);

                cguiObject.add(cat.name, catObject);
            }

            screensObject.add("ClickGui", cguiObject);
        }

        return screensObject;
    }

    @Override
    protected void readJson(JsonObject json) {
        if (json == null || !json.has("ClickGui")) return;

        JsonObject cguiObject = json.getAsJsonObject("ClickGui");

        for (Category cat : Category.values()) {
            if (!cguiObject.has(cat.name)) {
                continue;
            }

            JsonObject catObject = cguiObject.getAsJsonObject(cat.name);

            if (catObject.has("X")) {
                cat.position.x = catObject.get("X").getAsFloat();
            }

            if (catObject.has("Y")) {
                cat.position.y = catObject.get("Y").getAsFloat();
            }
        }
    }
}