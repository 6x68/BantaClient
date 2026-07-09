package today.vanta.util.client;

import org.lwjgl.input.Keyboard;

import java.util.Arrays;
import java.util.List;

public interface IClient {
    String CLIENT_NAME = "Vanta";
    String CLIENT_VERSION = "1.5";
    String DEVELOPERS = "made by mark & luna";
    String CLIENT_FULL_TITLE = CLIENT_NAME + " - " + CLIENT_VERSION + " - " + DEVELOPERS;
    int COMMAND_PREFIX_KEY = Keyboard.KEY_PERIOD;
    String COMMAND_PREFIX = ".";

    List<String> CHANGELOG = Arrays.asList(
            "[#] Made ClickGui togglable in Main Menu",
            "[#] ClickTeleport now draws destination",
            "[#] Fixed ClientSounds",
            "[#] Fixed Scaffold",
            "[+] Added Clicking settings module",
            "[+] Added session stat modules",
            "[+] Added Arraylist animations",
            "[+] Added new Boxy ClickGUI",
            "[+] Added WindowSettings",
            "[+] Added AutoRegister",
            "[+] Added Image ESP",
            "[+] Added AutoPlay",
            "[+] Added AntiVoid",
            "[+] Added NoFall",
            "[+] Added Arrows",
            "[+] Added VClip"
    );
}