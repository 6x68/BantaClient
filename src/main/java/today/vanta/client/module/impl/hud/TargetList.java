package today.vanta.client.module.impl.hud;

import net.minecraft.client.gui.GuiChat;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.input.Mouse;
import today.vanta.Vanta;
import today.vanta.client.event.impl.client.RenderOverlayEvent;
import today.vanta.client.event.impl.client.RenderScreenEvent;
import today.vanta.client.module.Category;
import today.vanta.client.module.Module;
import today.vanta.client.module.impl.client.Theme;
import today.vanta.client.module.impl.combat.KillAura;
import today.vanta.client.setting.Setting;
import today.vanta.client.setting.impl.NumberSetting;
import today.vanta.client.setting.impl.StringSetting;
import today.vanta.util.game.events.EventListen;
import today.vanta.util.game.player.PlayerUtil;
import today.vanta.util.game.render.RenderUtil;
import today.vanta.util.game.render.shape.impl.Rectangle;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TargetList extends Module {
    private final StringSetting mode = Setting.of("Mode", "Novoline", "Novoline", "Window");
    private static final Color BACKGROUND = new Color(20, 20, 20, 200);
    private static final Color DARKER_BACKGROUND = new Color(20, 20, 20, 255);
    private final NumberSetting
            x = Setting.of("X position", 20, 0, 2000),
            y = Setting.of("Y position", 20, 0, 2000);
    private boolean dragging;
    private float dragX, dragY;
    private float WIDTH = 100f;
    private float HEIGHT = 10f;
    private final List<EntityPlayer> list = new ArrayList<>();

    public TargetList() {
        super("TargetList", "List of Targets.", Category.HUD);
    }

    private void handleDragging(float mouseX, float mouseY) {
        if (Mouse.isButtonDown(0)) {
            if (!dragging && RenderUtil.hovered(mouseX, mouseY, x.getValue().floatValue(), y.getValue().floatValue(), WIDTH, HEIGHT)) {
                dragging = true;
                dragX = mouseX - x.getValue().floatValue();
                dragY = mouseY - y.getValue().floatValue();
            }

            if (dragging) {
                x.setValue(mouseX - dragX);
                y.setValue(mouseY - dragY);
            }
        } else {
            dragging = false;
        }
    }

    @EventListen
    public void onDrawScreen(RenderScreenEvent event) {
        if (mc.currentScreen instanceof GuiChat) {
            handleDragging(event.mouseX, event.mouseY);
        }
    }

    @EventListen
    private void onRender2D(RenderOverlayEvent event) {
        if (mc.thePlayer == null) {
            return;
        }

        list.clear();

        mc.theWorld.getLoadedEntityList().stream()
                .filter(entity -> entity instanceof EntityPlayer && entity != mc.thePlayer && !entity.isDead && mc.thePlayer.getDistanceToEntity(entity) < Vanta.instance.moduleStorage.getT(KillAura.class).attackRange.getValue().floatValue())
                .map(entity -> (EntityPlayer) entity)
                .forEachOrdered(list::add);

        for (int i = 0; i < list.size(); i++) {
            boolean isIllegal = PlayerUtil.checkIllegal(list.get(i));
            if (isIllegal) {
                list.remove(i);
            }
        }
        switch(mode.getValue()) {
            case "Novoline":
                WIDTH = 100f;
                HEIGHT = 10f * list.size();
                Rectangle
                        .create(x.getValue().floatValue(), y.getValue().floatValue(), WIDTH, 10)
                        .color(DARKER_BACKGROUND)
                        .push(event);

                mc.exhiFontRendererObj.drawString("Targets:", x.getValue().floatValue() + 1, y.getValue().floatValue() + 1, Color.WHITE);


                Rectangle
                        .create(x.getValue().floatValue(), y.getValue().floatValue() + 10, WIDTH, HEIGHT)
                        .color(BACKGROUND)
                        .push(event);
                float ydraw = y.getValue().floatValue() + 10;

                for (EntityPlayer entityPlayer : list) {
                    String name = entityPlayer.getName();
                    mc.exhiFontRendererObj.drawString(name, x.getValue().floatValue(),y.getValue().floatValue() + ydraw, Color.WHITE);
                    ydraw += 10f;
                }
                break;
            case "Window":
                RenderUtil.drawWindowRectangle(event,"TargetList",x.getValue().floatValue(),y.getValue().floatValue(),WIDTH,HEIGHT);
                break;
        }

        if (dragging) {
            Rectangle
                    .create(x.getValue().floatValue() - 0.5f, y.getValue().floatValue() - 0.5f, WIDTH + 1, HEIGHT + 1)
                    .color(Vanta.instance.moduleStorage.getT(Theme.class).colors[0])
                    .outline(true)
                    .push(event);
        }

    }
}