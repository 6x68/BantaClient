package today.vanta.client.module.impl.client;

import org.lwjgl.input.Keyboard;
import today.vanta.Vanta;
import today.vanta.client.module.Category;
import today.vanta.client.module.Module;
import today.vanta.client.screen.ClickGUIScreen;
import today.vanta.client.screen.ImGuiClickGUIScreen;
import today.vanta.client.screen.VantaScreen;
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

    private ClickGUIScreen clickGUIScreen;
    private ImGuiClickGUIScreen imGuiClickGuiScreen;

    @Override
    public void onEnable() {
        mc.displayGuiScreen(getClickGui());

        setEnabled(false);
    }

    public VantaScreen getClickGui() {
        if (clickGUIScreen == null) {
            clickGUIScreen = Vanta.instance.screenStorage.getT(ClickGUIScreen.class);
        }

        if (imGuiClickGuiScreen == null) {
            imGuiClickGuiScreen = Vanta.instance.screenStorage.getT(ImGuiClickGUIScreen.class);
        }

        switch (design.getValue()) {
            case "ImGui":
                return imGuiClickGuiScreen;

            case "Dropdown":
            default:
                return clickGUIScreen;
        }
    }
}