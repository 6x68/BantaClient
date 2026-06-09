package today.vanta.client.module.impl.hud;

import today.vanta.Vanta;
import today.vanta.client.event.impl.game.render.Render2DEvent;
import today.vanta.client.module.Category;
import today.vanta.client.module.Module;
import today.vanta.client.module.impl.client.Theme;
import today.vanta.client.setting.impl.BooleanSetting;
import today.vanta.client.setting.impl.NumberSetting;
import today.vanta.client.setting.impl.StringSetting;
import today.vanta.util.game.events.EventListen;
import today.vanta.util.game.events.EventPriority;
import today.vanta.util.game.render.RenderUtil;
import today.vanta.util.game.render.font.CFontRenderer;
import today.vanta.util.game.render.font.CFonts;

import java.awt.*;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Arraylist extends Module {
    private final NumberSetting xValue = NumberSetting.builder()
            .name("X offset")
            .value(5)
            .min(1)
            .max(25)
            .places(0)
            .build();

    private final NumberSetting yValue = NumberSetting.builder()
            .name("Y offset")
            .value(5)
            .min(0)
            .max(25)
            .places(0)
            .build();

    private final StringSetting fontStyle = StringSetting.builder()
            .name("Font style")
            .value("Medium")
            .values("Light", "Italic", "Medium", "Semibold", "Bold", "Heavy")
            .listener((setting, oldValue, newValue) -> setFont())
            .build();

    private final StringSetting moduleCase = StringSetting.builder()
            .name("Module case")
            .value("Default")
            .values("Default", "Lowercase", "Uppercase")
            .build();

    private final NumberSetting fontSize = NumberSetting.builder()
            .name("Font size")
            .value(24)
            .min(10)
            .max(42)
            .places(0)
            .suffix("px")
            .listener((setting, oldValue, newValue) -> setFont())
            .build();

    private final BooleanSetting
            fontShadow = BooleanSetting.builder()
            .name("Font shadow")
            .value(true)
            .build(),

    suffixes = BooleanSetting.builder()
            .name("Show suffixes")
            .value(true)
            .build(),

    background = BooleanSetting.builder()
            .name("Background")
            .value(true)
            .build(),

    spaceOut = BooleanSetting.builder()
            .name("Space out")
            .value(false)
            .build();

    private final StringSetting line = StringSetting.builder()
            .name("Line")
            .value("Full")
            .values("Full", "Left", "Right", "Top", "Top+right", "None")
            .build().hide(() -> !background.getValue());

    private final NumberSetting backgroundAlpha = NumberSetting.builder()
            .name("Background alpha")
            .value(100)
            .min(0)
            .max(255)
            .places(0)
            .build()
            .hide(() -> !background.getValue());

    public Arraylist() {
        super("Arraylist", "Draws an arraylist of modules.", Category.HUD);
        displayNames = new String[]{"Arraylist", "ArrayList", "ModuleList"};
        hideFromArraylist = true;
        setEnabled(true);
    }

    private CFontRenderer font = CFonts.SFPT_MEDIUM_24;

    private void setFont() {
        font = CFonts.getFont("SFPT-" + fontStyle.getValue(), fontSize.getValue().intValue());
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
                        m -> font.getStringWidth(getModuleName((Module) m))
                ).reversed()).collect(Collectors.toList());

        float y = yValue.getValue().floatValue();
        for (int i = 0; i < modules.size(); i++) {
            Module module = modules.get(i);
            String name = getModuleName(module);

            float modWidth = font.getStringWidth(name);
            float modHeight = font.getFontHeight();

            float x = event.scaledResolution.getScaledWidth() - modWidth - xValue.getValue().floatValue() - 2.5f;

            if (background.getValue()) {
                float rectX = x - 2;
                float rectY = y;
                float rectWidth = modWidth + 4.5f;
                float rectHeight = modHeight + 5;

                boolean first = i == 0;
                boolean last = i == modules.size() - 1;

                switch (line.getValue()) {
                    case "Top+right":
                    case "Top":
                        if (first) {
                            // top
                            RenderUtil.rectangle(rectX, rectY - 1, rectWidth, 1, fg);
                        }

                        if (line.getValue().equals("Top+right")) {
                            // right side
                            RenderUtil.rectangle(rectX + rectWidth, rectY - 1, 1, rectHeight + 1, fg);
                        }
                        break;
                    case "Full":
                        if (first) {
                            // top
                            RenderUtil.rectangle(rectX, rectY - 1, rectWidth, 1, fg);
                            // top left corner
                            RenderUtil.rectangle(rectX - 1, rectY - 1, 1, 1, fg);
                        }

                        if (last) {
                            // bottom for last module
                            RenderUtil.rectangle(rectX, rectY + rectHeight, rectWidth, 1, fg);
                        } else {
                            // bottom for each middle module
                            Module nextModule = modules.get(i + 1);
                            String nextName = getModuleName(nextModule);
                            float nextModWidth = font.getStringWidth(nextName);
                            float nextX = event.scaledResolution.getScaledWidth() - nextModWidth - 5;
                            float nextRectX = nextX - xValue.getValue().floatValue();

                            float widthToNext = nextRectX - rectX;

                            RenderUtil.rectangle(rectX, rectY + rectHeight, widthToNext, 1, fg);
                        }

                        // left side
                        RenderUtil.rectangle(rectX - 1, rectY, 1, rectHeight, fg);
                        // bottom left corner
                        RenderUtil.rectangle(rectX - 1, rectY + rectHeight, 1, 1, fg);

                        // right side
                        RenderUtil.rectangle(rectX + rectWidth, rectY - 1, 1, modHeight + 7, fg);
                        break;

                    case "Left":
                        // left side
                        RenderUtil.rectangle(rectX - 1, rectY, 1, rectHeight, fg);
                        break;

                    case "Right":
                        // right side
                        RenderUtil.rectangle(rectX + rectWidth, rectY, 1, modHeight + 5, fg);
                        break;
                }

                RenderUtil.rectangle(rectX, rectY, rectWidth, rectHeight, bg);
            }

            if (fontShadow.getValue()) {
                font.drawStringWithShadow(name, x, y + 0.5f, fg);
            } else {
                font.drawString(name, x, y + 0.5f, fg);
            }

            y += font.getFontHeight() + (background.getValue() ? 3 : 0) + 2;
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