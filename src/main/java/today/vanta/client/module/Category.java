package today.vanta.client.module;

import org.lwjgl.util.vector.Vector2f;
import today.vanta.client.screen.ClickGUIScreen;
import today.vanta.util.game.render.font.Icons;

import java.awt.*;

public enum Category {
    COMBAT("Combat", new Vector2f(5, 5), new Color(0xFF3B3B), Icons.SWORD),
    MOVEMENT("Movement", new Vector2f((5 + ClickGUIScreen.PANEL_WIDTH) * 1, 5), new Color(0x00E5FF), Icons.PERSON_SIMPLE_RUN),
    PLAYER("Player", new Vector2f((5 + ClickGUIScreen.PANEL_WIDTH) * 2, 5), new Color(0xFF9800), Icons.PERSON_SIMPLE),
    RENDER("Render", new Vector2f((5 + ClickGUIScreen.PANEL_WIDTH) * 3, 5), new Color(0xB388FF), Icons.CUBE_FOCUS),
    HUD("Hud", new Vector2f((5 + ClickGUIScreen.PANEL_WIDTH) * 4, 5), new Color(0xE0E0E0), Icons.MONITOR),
    MISC("Misc", new Vector2f((5 + ClickGUIScreen.PANEL_WIDTH) * 5, 5), new Color(0x9E9E9E), Icons.PUZZLE_PIECE),
    CLIENT("Client", new Vector2f((5 + ClickGUIScreen.PANEL_WIDTH) * 6, 5), new Color(0xE95D3C), Icons.WRENCH);

    public final String name;
    public final Vector2f position;
    public final Color color;
    public final char icon;

    Category(String name, Vector2f position, Color color, char icon) {
        this.name = name;
        this.position = position;
        this.color = color;
        this.icon = icon;
    }
}