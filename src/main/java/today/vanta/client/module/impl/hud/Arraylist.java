package today.vanta.client.module.impl.hud;

import today.vanta.Vanta;
import today.vanta.client.event.impl.game.render.Render2DEvent;
import today.vanta.client.module.Category;
import today.vanta.client.module.Module;
import today.vanta.client.module.impl.client.Theme;
import today.vanta.client.module.impl.hud.arraylist.ArraylistRenderer;
import today.vanta.client.module.impl.hud.arraylist.BitMapRenderer;
import today.vanta.client.module.impl.hud.arraylist.GlyphRenderer;
import today.vanta.client.setting.Setting;
import today.vanta.client.setting.impl.BooleanSetting;
import today.vanta.client.setting.impl.NumberSetting;
import today.vanta.client.setting.impl.StringSetting;
import today.vanta.util.game.events.EventListen;
import today.vanta.util.game.events.EventPriority;
import today.vanta.util.game.render.RenderUtil;
import today.vanta.util.game.render.font.CFonts;
import today.vanta.util.system.math.ColorUtil;

import java.awt.*;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Arraylist extends Module {
    private final NumberSetting
            xOffset = Setting.of("X offset", 5, 0, 25),
            yOffset = Setting.of("Y offset", 5, 0, 25);

    private final StringSetting
            font = Setting.of("Font", "SFPT", "SFPT", "Minecraft", "Exhibition"),
            fontStyle = Setting.of("Font style", "Medium", "Light", "Italic", "Medium", "Semibold", "Bold", "Heavy");

    private final NumberSetting fontSize = Setting.of("Font size", 24, 10, 42, "px").hide(() -> !font.getValue().equals("SFPT"));

    private final BooleanSetting
            fontShadow = Setting.of("Font shadow", true),
            suffixes = Setting.of("Show suffixes", true),
            background = Setting.of("Background", true),
            spaceOut = Setting.of("Space out", false);

    private final StringSetting line = Setting.of("Line", "Full", "Full", "Left", "Right", "Top", "Top+right", "None");

    private final NumberSetting backgroundAlpha = Setting.of("Background alpha", 100, 0, 255).hide(() -> !background.getValue());
    private final StringSetting
            moduleCase = Setting.of("Module case", "Default", "Default", "Lowercase", "Uppercase"),
            colorMode = Setting.of("Color mode", "Theme", "Theme", "Rainbow", "Random", "Category", "Fade");

    public Arraylist() {
        super("Arraylist", "Draws an arraylist of modules.", Category.HUD);
        displayNames = new String[]{"Arraylist", "ArrayList", "ModuleList"};
        hideFromArraylist = true;
        setEnabled(true);

        font.addListener(((setting, oldValue, newValue) -> setFont()));
        fontStyle.addListener(((setting, oldValue, newValue) -> setFont()));
        fontSize.addListener(((setting, oldValue, newValue) -> setFont()));
    }

    private ArraylistRenderer arraylistFontRenderer = new GlyphRenderer(CFonts.SFPT_MEDIUM_24);

    private void setFont() {
        switch (font.getValue()) {
            case "Exhibition":
                arraylistFontRenderer = new BitMapRenderer(mc.exhiFontRendererObj);
                break;
            case "Minecraft":
                arraylistFontRenderer = new BitMapRenderer(mc.fontRendererObj);
                break;
            default:
                arraylistFontRenderer = new GlyphRenderer(CFonts.getFont("SFPT-" + fontStyle.getValue(), fontSize.getValue().intValue()));
                break;
        }
    }

    @EventListen(priority = EventPriority.LOWEST)
    private void onRender(Render2DEvent event) {
        Color fg = Vanta.instance.moduleStorage.getT(Theme.class).colors[0];
        Color bg = new Color(0, 0, 0, backgroundAlpha.getValue().intValue());

        List<Module> modules = Vanta.instance.moduleStorage.list.stream().
                filter(m -> {
                    boolean add = m.isEnabled();

                    if (m.hideFromArraylist) {
                        add = false;
                    }

                    return add;
                }).sorted(Comparator.comparingDouble(
                        m -> arraylistFontRenderer.getStringWidth(getModuleName((Module) m))
                ).reversed()).collect(Collectors.toList());

        float y = yOffset.getValue().floatValue();
        int offset = 0;
        for (int i = 0; i < modules.size(); i++) {
            Module module = modules.get(i);
            String name = getModuleName(module);

            float modWidth = arraylistFontRenderer.getStringWidth(name);

            float x = event.scaledResolution.getScaledWidth() - modWidth - xOffset.getValue().floatValue() - 2.5f;

            Color color = fg;

            switch (colorMode.getValue()) {
                case "Category":
                    color = module.category.color;
                    break;
                case "Random":
                    color = module.color;
                    break;
                case "Rainbow":
                    color = new Color(ColorUtil.getRainbow(3000, (int) (offset * 150L)));
                    break;
                case "Fade":
                    color = new Color(ColorUtil.fadeBetween(fg.getRGB(), fg.darker().darker().getRGB(), offset * 150L));
                    break;
            }

            if (background.getValue()) {
                float rectX = x - 2;
                float rectY = y;
                float rectWidth = modWidth + 4.5f;
                float rectHeight = arraylistFontRenderer.getBoxHeight();

                boolean first = i == 0;
                boolean last = i == modules.size() - 1;

                switch (line.getValue()) {
                    case "Top+right":
                    case "Top":
                        if (first) {
                            // top
                            RenderUtil.rectangle(rectX, rectY - 1, rectWidth, 1, color);
                        }

                        if (line.getValue().equals("Top+right")) {
                            // right side
                            RenderUtil.rectangle(rectX + rectWidth, rectY - 1, 1, rectHeight + 1, color);
                        }
                        break;
                    case "Full":
                        if (first) {
                            // top
                            RenderUtil.rectangle(rectX, rectY - 1, rectWidth, 1, color);
                            // top left corner
                            RenderUtil.rectangle(rectX - 1, rectY - 1, 1, 1, color);
                        }

                        if (last) {
                            // bottom for last module
                            RenderUtil.rectangle(rectX, rectY + rectHeight, rectWidth, 1, color);
                        } else {
                            // bottom for each middle module
                            Module nextModule = modules.get(i + 1);
                            String nextName = getModuleName(nextModule);
                            float nextModWidth = arraylistFontRenderer.getStringWidth(nextName);
                            float nextX = event.scaledResolution.getScaledWidth() - nextModWidth - 5;
                            float nextRectX = nextX - xOffset.getValue().floatValue();

                            float widthToNext = nextRectX - rectX;

                            RenderUtil.rectangle(rectX, rectY + rectHeight, widthToNext, 1, color);
                        }

                        // left side
                        RenderUtil.rectangle(rectX - 1, rectY, 1, rectHeight, color);
                        // bottom left corner
                        RenderUtil.rectangle(rectX - 1, rectY + rectHeight, 1, 1, color);

                        // right side
                        RenderUtil.rectangle(rectX + rectWidth, rectY - 1, 1, rectHeight + 2, color);
                        break;

                    case "Left":
                        // left side
                        RenderUtil.rectangle(rectX - 1, rectY, 1, rectHeight, color);
                        break;

                    case "Right":
                        // right side
                        RenderUtil.rectangle(rectX + rectWidth, rectY, 1, rectHeight, color);
                        break;
                }

                RenderUtil.rectangle(rectX, rectY, rectWidth, rectHeight, bg);
            }

            arraylistFontRenderer.drawString(name, x, y + 0.5f, color, fontShadow.getValue());

            y += background.getValue() ? arraylistFontRenderer.getBoxHeight() : arraylistFontRenderer.getFontHeight() + 2;
            offset++;
        }
    }

    private String getModuleName(Module module) {
        String name = module.displayName;
        String suffix;

        if (module.getSuffix() != null && module.addSuffix && suffixes.getValue()) {
            suffix = module.getSuffix();
            if (spaceOut.getValue()) {
                suffix = suffix.replaceAll("(?<=[a-z])(?=[A-Z])", " ");
            }
            name += "§f" + suffix;
        }

        if (spaceOut.getValue()) {
            name = name.replaceAll("(?<=[a-z])(?=[A-Z])|(?<=[A-Za-z])(?=\\d)", " ");
        } else {
            name = name.replace("§", " §");
        }

        switch (moduleCase.getValue()) {
            case "Lowercase":
                name = name.toLowerCase();
                break;
            case "Uppercase":
                name = name.toUpperCase();
                break;
        }

        return name;
    }
}