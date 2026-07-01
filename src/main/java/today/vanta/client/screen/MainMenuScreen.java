package today.vanta.client.screen;

import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSelectWorld;
import today.vanta.Vanta;
import today.vanta.client.screen.component.Component;
import today.vanta.client.screen.component.impl.ButtonComponent;
import today.vanta.storage.impl.ModuleStorage;
import today.vanta.util.client.IClient;
import today.vanta.util.game.render.font.impl.GlyphFontRenderer;
import today.vanta.util.game.render.font.CFonts;
import today.vanta.util.game.render.shape.impl.Rectangle;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainMenuScreen extends GuiScreen {
    private final GlyphFontRenderer roundedSemibold10 = CFonts.SFPT_SEMIBOLD_20;
    private final GlyphFontRenderer roundedMedium9 = CFonts.SFPT_MEDIUM_18;
    private final GlyphFontRenderer smallTitle = CFonts.SFPT_SEMIBOLD_20;
    private final GlyphFontRenderer changesFont = CFonts.SFPT_MEDIUM_18;

    private final List<Component> buttons = new ArrayList<>();

    @Override
    public void initGui() {
        super.initGui();

        float middleX = width / 2f;
        float middleY = height / 2f;

        float buttonWidth = 140;

        buttons.clear();
        buttons.add(new ButtonComponent("Singleplayer", middleX - buttonWidth / 2f, middleY, buttonWidth, 14, roundedMedium9));
        middleY += 14;
        buttons.add(new ButtonComponent("Multiplayer", middleX - buttonWidth / 2f, middleY, buttonWidth, 14, roundedMedium9));
        middleY += 14;
        buttons.add(new ButtonComponent("Options", middleX - buttonWidth / 2f, middleY, buttonWidth, 14, roundedMedium9));
        middleY += 14;
        buttons.add(new ButtonComponent("Alts", middleX - buttonWidth / 2f, middleY, buttonWidth, 14, roundedMedium9));
        middleY += 14;
        buttons.add(new ButtonComponent("Exit", middleX - buttonWidth / 2f, middleY, buttonWidth, 14, roundedMedium9));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        Rectangle
                .create(0, 0, width, height)
                .color(new Color(20, 20, 20))
                .draw();

        float panelWidth = 0;
        for (String change : Vanta.instance.moduleStorage.changelog) {
            panelWidth = Math.max(panelWidth, changesFont.getStringWidth(change) + 10);
        }

        float boxHeight = 14 * Vanta.instance.moduleStorage.changelog.size() + 18;
        float middleY = 5;

        Rectangle
                .create(5, middleY, panelWidth, boxHeight)
                .color(new Color(30, 30, 30))
                .draw();
        smallTitle.drawString("Changelog", 5 + 3.5f, middleY + 4.5f - 1, -1);

        for (int i = 0; i < Vanta.instance.moduleStorage.changelog.size(); i++) {
            String change = Vanta.instance.moduleStorage.changelog.get(i);
            float y = middleY + 18 + i * 14 - 1.5f;

            Rectangle
                    .create(5 + 1.5f, y, (panelWidth - 3), 14)
                    .color(new Color(35, 35, 35))
                    .draw();

            String formattedChange = formatChange(change);

            changesFont.drawYCenteredString(formattedChange, 5 + 3.5f, y + 14 / 2f - 2, Color.WHITE, false);
        }

        float middleX = width / 2f;
        middleY = height / 2f;

        Rectangle
                .create(middleX - 143 / 2f, middleY - 16, 143, 14 * (buttons.size()) + 18)
                .color(new Color(30, 30, 30))
                .draw();
        roundedSemibold10.drawString(IClient.CLIENT_NAME, middleX - 143 / 2f + 3, middleY - 18 + 4.5f, -1);
        roundedMedium9.drawString(IClient.CLIENT_VERSION + " | " + IClient.DEVELOPERS, middleX * 2 - roundedMedium9.getStringWidth(IClient.CLIENT_VERSION + " | " + IClient.DEVELOPERS) - 3, middleY * 2 - roundedMedium9.getFontHeight() - 5.5f, new Color(200, 200, 200));

        buttons.forEach(but -> but.draw(mouseX, mouseY));
    }

    private static String formatChange(String change) {
        String formattedChange;

        if (change.startsWith("[+]")) {
            formattedChange = "§a" + change;
        } else if (change.startsWith("[-]")) {
            formattedChange = "§c" + change;
        } else if (change.startsWith("[#]")) {
            formattedChange = "§e" + change;
        } else if (change.startsWith("[~]")) {
            formattedChange = "§9" + change;
        } else {
            formattedChange = "§7" + change;
        }

        return formattedChange;
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        for (Component but : buttons) {
            if (but.click(mouseX, mouseY, 0)) {
                switch (but.text) {
                    case "Singleplayer":
                        mc.displayGuiScreen(new GuiSelectWorld(this));
                        break;
                    case "Multiplayer":
                        mc.displayGuiScreen(new GuiMultiplayer(this));
                        break;
                    case "Options":
                        mc.displayGuiScreen(new GuiOptions(this, mc.gameSettings));
                        break;
                    case "Alts":
                        mc.displayGuiScreen(Vanta.instance.screenStorage.getT(AltLoginScreen.class));
                        break;
                    case "Exit":
                        mc.shutdownMinecraftApplet();
                        break;
                }
            }
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1) {
            return;
        }

        super.keyTyped(typedChar, keyCode);
    }
}