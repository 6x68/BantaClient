package today.vanta.client.module;

import org.lwjgl.util.vector.Vector2f;
import today.vanta.client.screen.ClickGUIScreen;

import java.awt.*;

public enum Category {
    COMBAT("Combat", new Vector2f(5, 5), new Color(0xFF3B3B)),
    MOVEMENT("Movement", new Vector2f((5 + ClickGUIScreen.PANEL_WIDTH) * 1, 5), new Color(0x00E5FF)),
    PLAYER("Player", new Vector2f((5 + ClickGUIScreen.PANEL_WIDTH) * 2, 5), new Color(0xFF9800)),
    RENDER("Render", new Vector2f((5 + ClickGUIScreen.PANEL_WIDTH) * 3, 5), new Color(0xB388FF)),
    HUD("Hud", new Vector2f((5 + ClickGUIScreen.PANEL_WIDTH) * 4, 5), new Color(0xE0E0E0)),
    MISC("Misc", new Vector2f((5 + ClickGUIScreen.PANEL_WIDTH) * 5, 5), new Color(0x9E9E9E)),
    CLIENT("Client", new Vector2f((5 + ClickGUIScreen.PANEL_WIDTH) * 6, 5), new Color(0xE95D3C));

    public final String name;
    public final Vector2f position;
    public final Color color;

    Category(String name, Vector2f position, Color color) {
        this.name = name;
        this.position = position;
        this.color = color;
    }
}