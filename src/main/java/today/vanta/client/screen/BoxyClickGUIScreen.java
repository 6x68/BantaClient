package today.vanta.client.screen;

import today.vanta.Vanta;
import today.vanta.client.event.impl.client.RenderScreenEvent;
import today.vanta.client.module.Category;
import today.vanta.client.module.Module;
import today.vanta.client.module.impl.client.ClickGUI;
import today.vanta.client.module.impl.client.Theme;
import today.vanta.client.setting.Setting;
import today.vanta.client.setting.impl.BooleanSetting;
import today.vanta.util.game.events.EventListen;
import today.vanta.util.game.render.ImageUtil;
import today.vanta.util.game.render.RenderUtil;
import today.vanta.util.game.render.font.CFonts;
import today.vanta.util.game.render.font.Icons;
import today.vanta.util.game.render.shape.GradientMode;
import today.vanta.util.game.render.shape.impl.GradientRectangle;
import today.vanta.util.game.render.shape.impl.ImageRectangle;
import today.vanta.util.game.render.shape.impl.Rectangle;

import java.awt.Color;
import java.io.IOException;

public class BoxyClickGUIScreen extends VantaScreen {
    private static final float SIDEBAR_WIDTH = 70;

    public static final Color GRAY_20 = new Color(20, 20, 20);
    public static final Color GRAY_30 = new Color(30, 30, 30);
    public static final Color GRAY_3C = new Color(0x3c3c3c);
    public static final Color GRAY_60 = new Color(0x606060);
    public static final Color TEXT_MAIN = new Color(0xe0e0e0);
    public static final Color TEXT_MUTED = new Color(0x888888);
    public static final Color WHITE = Color.WHITE;

    private float sWidth = 250, sHeight = 190;
    private float x = -999, y = -999;
    private Category selectedCat = Category.COMBAT;

    @Override
    protected void initScreen() {
        if (x == -999 || y == -999) {
            x = width / 2f - sWidth / 2;
            y = height / 2f - sHeight / 2;
        }
    }

    @EventListen
    private void onRender(RenderScreenEvent event) {
        if (Vanta.instance.moduleStorage.getT(ClickGUI.class).image.getValue()) {
            float imgWidth = 300;
            float imgHeight = 300;

            String texture = Vanta.instance.moduleStorage.getT(ClickGUI.class).mascot.getValue() + ".png";
            if (texture.startsWith("cousin")) {
                texture = "cousin.gif";
            } else if (texture.startsWith("longboy")) {
                imgHeight = 400 ;
            }

            ImageRectangle
                    .create(width - 175 - (imgWidth / 2), height - 210 - (imgHeight / 2), imgWidth, imgHeight, -1)
                    .resource(ImageUtil.getTexture(texture))
                    .push(event);
        }

        if (Vanta.instance.moduleStorage.getT(ClickGUI.class).darkenBackground.getValue()) {
            Rectangle.create(0, 0, width, height)
                    .color(new Color(0, 0, 0, 150))
                    .push(event);
        }

        Color color1 = Vanta.instance.moduleStorage.getT(Theme.class).colors[0];

        if (Vanta.instance.moduleStorage.getT(ClickGUI.class).gradientBackground.getValue()) {
            GradientRectangle.create(0, 0, width, height)
                    .firstColor(new Color(0, 0, 0, 150))
                    .secondColor(new Color(color1.getRed(), color1.getGreen(), color1.getBlue(), 150))
                    .gradientMode(GradientMode.VERTICAL)
                    .push(event);
        }

        drawBox(event);
        RenderUtil.scissor(x, y, sWidth, sHeight, () -> {
            drawSidebar(event);
            drawCategories(event);
            if (selectedCat != null)
                drawModules(event);
        });
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (!RenderUtil.hovered(mouseX, mouseY, x, y, sWidth, sHeight)) return;

        clickCategory(mouseX, mouseY, mouseButton);
        clickModules(mouseX, mouseY, mouseButton);
    }

    private void drawBox(RenderScreenEvent event) {
        Rectangle
                .create(x - 0.5, y - 0.5, sWidth + 1, sHeight + 1)
                .color(GRAY_3C)
                .push(event);

        Rectangle
                .create(x, y, sWidth, sHeight)
                .color(GRAY_30)
                .push(event);
    }

    private void drawSidebar(RenderScreenEvent event) {
        Rectangle
                .create(x, y, SIDEBAR_WIDTH, sHeight)
                .color(GRAY_20)
                .push(event);

        Rectangle
                .create(x + SIDEBAR_WIDTH, y, 0.5, sHeight)
                .color(GRAY_3C)
                .push(event);

        CFonts.getFont("SFPT-Semibold", 12).drawString("CLICKGUI", x + 7.5f, y + 7.5f, WHITE);
    }

    private void drawCategories(RenderScreenEvent event) {
        float yOffset = y + 7.5f + 7.5f + 7.5f;
        for (Category cat : Category.values()) {
            boolean over = RenderUtil.hovered(event.mouseX, event.mouseY, x, yOffset, SIDEBAR_WIDTH, 20);
            boolean selected = cat.equals(selectedCat);

            Rectangle
                    .create(x, yOffset, SIDEBAR_WIDTH, 20)
                    .color(over ? GRAY_30 : selected ? GRAY_30 : GRAY_20)
                    .push(event);

            Rectangle
                    .create(x, yOffset, SIDEBAR_WIDTH, 0.5)
                    .color(GRAY_3C)
                    .push(event);

            Rectangle
                    .create(x, yOffset + 20, SIDEBAR_WIDTH, 0.5)
                    .color(GRAY_3C)
                    .push(event);

            if (selected) {
                Rectangle
                        .create(x, yOffset, 1.5, 20)
                        .color(GRAY_60)
                        .push(event);
            }

            float textX = x + 6.5f;

            CFonts.getFont("SFPT-Regular", 12).drawString(cat.name, textX + 13, yOffset + 6.5f, selected ? WHITE : over ? TEXT_MAIN : TEXT_MUTED);
            CFonts.ICONS_16.drawString(cat.icon + "", textX, yOffset + 6.5f, selected ? WHITE : over ? TEXT_MAIN : TEXT_MUTED);

            yOffset += 20;
        }
    }

    private void drawModules(RenderScreenEvent event) {
        float xOffset = x + SIDEBAR_WIDTH + 10;
        float yOffset = y + 9.5f;
        float moduleWidth = sWidth - SIDEBAR_WIDTH - 10 * 2;
        for (Module mod : Vanta.instance.moduleStorage.getModulesByCategory(selectedCat)) {
            float moduleHeight = 18;

            boolean over = RenderUtil.hovered(event.mouseX, event.mouseY, xOffset, yOffset, moduleWidth, moduleHeight);

            Rectangle
                    .create(xOffset, yOffset, moduleWidth, moduleHeight)
                    .color(over ? GRAY_3C : GRAY_20)
                    .push(event);

            Rectangle
                    .create(xOffset, yOffset, moduleWidth, 0.5)
                    .color(GRAY_3C)
                    .push(event);

            Rectangle
                    .create(xOffset, yOffset + moduleHeight, moduleWidth, 0.5)
                    .color(GRAY_3C)
                    .push(event);

            Rectangle
                    .create(xOffset, yOffset, 0.5, moduleHeight)
                    .color(GRAY_3C)
                    .push(event);

            Rectangle
                    .create(xOffset + moduleWidth, yOffset, 0.5, moduleHeight + 0.5)
                    .color(GRAY_3C)
                    .push(event);

            CFonts.getFont("SFPT-Semibold", 12).drawString(mod.name.toUpperCase(), xOffset + 7.5f, yOffset + 5.5f, over ? WHITE : TEXT_MAIN);
            CFonts.ICONS_12.drawString((mod.isExpanded() ? Icons.CARET_DOWN : Icons.CARET_UP) + "", xOffset + moduleWidth - 12, yOffset + 6, over ? WHITE : TEXT_MAIN);

            float appendHeight = 0;
            if (mod.isExpanded()) {
                float settX = xOffset;
                float settY = yOffset + moduleHeight;

                float settYOffset = settY + 8;
                for (Setting<?> sett : mod.settings) {
                    if (sett.isHidden()) continue;

                    if (sett instanceof BooleanSetting) { //checkbox
                        BooleanSetting boolSett = (BooleanSetting) sett;

                        Rectangle
                                .create(settX + 8, settYOffset, 8, 8)
                                .color(GRAY_20)
                                .push(event);

                        if (boolSett.getValue()) {
                            Rectangle
                                    .create(settX + 8, settYOffset, 8, 8)
                                    .color(GRAY_3C)
                                    .push(event);

                            Rectangle
                                    .create(settX + 8 + 2, settYOffset + 2, 4, 4)
                                    .color(WHITE)
                                    .push(event);
                        }

                        CFonts.getFont("SFPT-Regular", 12).drawString(sett.name.toUpperCase(), settX + 8 + 4.5f + 2 + 5, settYOffset + 0.5f, TEXT_MUTED);

                        settYOffset += 15;
                        appendHeight += 15;
                    }
                }
                appendHeight += 8.5f;

                Rectangle
                        .create(settX, settY + appendHeight, moduleWidth, 0.5)
                        .color(GRAY_3C)
                        .push(event);

                Rectangle
                        .create(settX, settY, 0.5, appendHeight)
                        .color(GRAY_3C)
                        .push(event);

                Rectangle
                        .create(settX + moduleWidth, settY, 0.5, appendHeight + 0.5)
                        .color(GRAY_3C)
                        .push(event);
            }

            yOffset += moduleHeight + 10 + appendHeight;
        }
    }

    private void clickCategory(int mouseX, int mouseY, int mouseButton) {
        float yOffset = y + 7.5f + 7.5f + 7.5f;
        for (Category cat : Category.values()) {
            if (RenderUtil.hovered(mouseX, mouseY, x, yOffset, SIDEBAR_WIDTH, 20) && mouseButton == 0) {
                selectedCat = cat;
            }
            yOffset += 20;
        }
    }

    private void clickModules(int mouseX, int mouseY, int mouseButton) {
        float xOffset = x + SIDEBAR_WIDTH + 10;
        float yOffset = y + 9.5f;
        float moduleWidth = sWidth - SIDEBAR_WIDTH - 10 * 2;
        for (Module mod : Vanta.instance.moduleStorage.getModulesByCategory(selectedCat)) {
            float moduleHeight = 18;
            boolean hover = RenderUtil.hovered(mouseX, mouseY, xOffset, yOffset, moduleWidth, moduleHeight);

            if (hover && mouseButton == 0) {
                mod.setEnabled(!mod.isEnabled());
            } else if (hover && mouseButton == 1) {
                mod.setExpanded(!mod.isExpanded());
            }

            float appendHeight = 0;
            if (mod.isExpanded()) {
                appendHeight += 30;
            }

            yOffset += moduleHeight + 10 + appendHeight;
        }
    }
}