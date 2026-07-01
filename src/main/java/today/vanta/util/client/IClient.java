package today.vanta.util.client;

import org.lwjgl.input.Keyboard;

import java.util.Arrays;
import java.util.List;

public interface IClient {
    String CLIENT_NAME = "Vanta";
    String CLIENT_VERSION = "1.4";
    String DEVELOPERS = "made by mark & luna";
    String CLIENT_FULL_TITLE = CLIENT_NAME + " - " + CLIENT_VERSION + " - " + DEVELOPERS;
    int COMMAND_PREFIX_KEY = Keyboard.KEY_PERIOD;
    String COMMAND_PREFIX = ".";

    List<String> CHANGELOG = Arrays.asList(
            "[+] Added Commands - help, bind, toggle, clear",
            "[+] Added command suggestions",
            "[+] Added AntiExploit",
            "[+] Added AntiCheat",
            "[#] Reworked InventoryManager",
            "[#] Reworked ChestStealer"
    );
}