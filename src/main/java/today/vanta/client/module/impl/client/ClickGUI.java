package today.vanta.client.module.impl.client;

import org.lwjgl.input.Keyboard;
import today.vanta.client.module.Category;
import today.vanta.client.module.Module;
import today.vanta.client.screen.ClickGUIScreen;
import today.vanta.client.screen.ImGuiClickGUIScreen;
import today.vanta.client.setting.Setting;
import today.vanta.client.setting.impl.BooleanSetting;
import today.vanta.client.setting.impl.StringSetting;

public class ClickGUI extends Module {
    public final BooleanSetting
            pauseGame = Setting.of("Pause singleplayer", false),
            darkenBackground = Setting.of("Dark background", true),
            gradientBackground = Setting.of("Gradient background", true);

    private final StringSetting design = Setting.of("Design", "Dropdown", "Dropdown", "ImGui");

    public ClickGUI() {
        super("ClickGUI", "Opens up the ClickGUI.", Category.CLIENT, Keyboard.KEY_RSHIFT);
        hideFromArraylist = true;
    }

    private final ClickGUIScreen clickGUIScreen = new ClickGUIScreen();
    private final ImGuiClickGUIScreen imGuiClickGuiScreen = new ImGuiClickGUIScreen();

    @Override
    public void onEnable() {
        switch (design.getValue()) {
            case "Dropdown": {
                mc.displayGuiScreen(clickGUIScreen);
                break;
            }

            case "ImGui": {
                mc.displayGuiScreen(imGuiClickGuiScreen);
                break;
            }
        }

        setEnabled(false);
    }
}