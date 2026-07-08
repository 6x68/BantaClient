package today.vanta.client.module.impl.render;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Mouse;
import today.vanta.client.event.impl.client.RenderScreenEvent;
import today.vanta.client.event.impl.game.render.ScoreboardRenderEvent;
import today.vanta.client.module.Category;
import today.vanta.client.module.Module;
import today.vanta.client.setting.Setting;
import today.vanta.client.setting.impl.BooleanSetting;
import today.vanta.client.setting.impl.NumberSetting;
import today.vanta.util.game.events.EventListen;
import today.vanta.util.game.render.RenderUtil;
import today.vanta.util.game.render.shape.impl.Rectangle;

import java.awt.*;
import java.util.Collection;
import java.util.List;

public class Scoreboard extends Module {
    private final BooleanSetting removeScore = Setting.of("Remove score", true);

    private final NumberSetting
            x = Setting.of("X position", -1, -1, 2000),
            y = Setting.of("Y position", -1, -1, 2000);

    private boolean dragging;
    private float dragX, dragY;

    public Scoreboard() {
        super("Scoreboard", "Modifies Minecraft Scoreboard.", Category.RENDER);

        x.addListener((setting, oldValue, newValue) -> resetToDefaults());
        y.addListener((setting, oldValue, newValue) -> resetToDefaults());
    }

    @Override
    public void onEnable() {
        resetToDefaults();
    }

    private boolean resetting;

    private void resetToDefaults() {
        if (resetting) return;

        float xValue = x.getValue().floatValue();
        float yValue = y.getValue().floatValue();

        if (xValue >= 0 && yValue >= 0) return;

        ScoreboardLayout layout = resolveLayout();
        if (layout == null) return;

        resetting = true;
        try {
            if (xValue < 0) x.setValue(layout.defaultX);
            if (yValue < 0) y.setValue(layout.defaultY);
        } finally {
            resetting = false;
        }
    }

    @EventListen
    private void onScoreboardRender(ScoreboardRenderEvent event) {
        event.cancelled = true;
    }

    @EventListen
    private void onRenderScreen(RenderScreenEvent event) {
        if (mc.thePlayer == null || mc.theWorld == null) return;

        ScoreboardLayout layout = resolveLayout();
        if (layout == null) return;

        float boundsX = x.getValue().floatValue();
        float boundsY = y.getValue().floatValue();

        float renderX = boundsX + 2;
        float renderY = boundsY + layout.totalHeight;

        if (mc.currentScreen instanceof GuiChat) {
            handleDragging(event.mouseX, event.mouseY, boundsX, boundsY, layout.totalWidth, layout.totalHeight);
        } else if (mc.currentScreen != null) {
            return;
        }

        int lineIndex = 0;
        for (Score score : layout.scores) {
            lineIndex++;

            ScorePlayerTeam team = layout.scoreboard.getPlayersTeam(score.getPlayerName());
            String playerName = ScorePlayerTeam.formatPlayerName(team, score.getPlayerName());

            float rectY = renderY - lineIndex * mc.fontRendererObj.FONT_HEIGHT;
            float rightX = renderX + layout.width + 2;

            Rectangle
                    .create(boundsX, rectY, layout.totalWidth, mc.fontRendererObj.FONT_HEIGHT)
                    .color(new Color(0, 0, 0, 80))
                    .push(event);

            mc.fontRendererObj.drawString(playerName, (int) renderX, (int) rectY, 553648127);

            if (!removeScore.getValue()) {
                String points = EnumChatFormatting.RED + "" + score.getScorePoints();
                mc.fontRendererObj.drawString(points, (int) (rightX - mc.fontRendererObj.getStringWidth(points)), (int) rectY, 553648127);
            }

            if (lineIndex == layout.scores.size()) {
                String objectiveName = layout.objective.getDisplayName();

                Rectangle
                        .create(boundsX, rectY - mc.fontRendererObj.FONT_HEIGHT - 1, layout.totalWidth, mc.fontRendererObj.FONT_HEIGHT)
                        .color(new Color(0, 0, 0, 96))
                        .push(event);
                Rectangle
                        .create(boundsX, rectY - 1, layout.totalWidth, 1)
                        .color(new Color(0, 0, 0, 80))
                        .push(event);

                mc.fontRendererObj.drawString(objectiveName, (int) (renderX + layout.width / 2f - mc.fontRendererObj.getStringWidth(objectiveName) / 2f), (int) (rectY - mc.fontRendererObj.FONT_HEIGHT), 553648127);
            }
        }

        if (dragging && mc.currentScreen instanceof GuiChat) {
            Rectangle
                    .create(boundsX - 0.5, boundsY - 0.5, layout.totalWidth + 1, layout.totalHeight + 1)
                    .color(new Color(255, 255, 255, 150))
                    .outline(true)
                    .push(event);
        }
    }

    private void handleDragging(float mouseX, float mouseY, float boundsX, float boundsY, float boundsWidth, float boundsHeight) {
        if (Mouse.isButtonDown(0)) {
            if (!dragging && RenderUtil.hovered(mouseX, mouseY, boundsX, boundsY, boundsWidth, boundsHeight)) {
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

    private ScoreboardLayout resolveLayout() {
        if (mc.thePlayer == null || mc.theWorld == null) return null;

        ScaledResolution scaledRes = new ScaledResolution(mc);
        net.minecraft.scoreboard.Scoreboard worldScoreboard = mc.theWorld.getScoreboard();

        ScoreObjective sidebarObjective = null;
        ScorePlayerTeam playerTeam = worldScoreboard.getPlayersTeam(mc.thePlayer.getName());

        if (playerTeam != null) {
            int teamColor = playerTeam.getChatFormat().getColorIndex();
            if (teamColor >= 0) {
                sidebarObjective = worldScoreboard.getObjectiveInDisplaySlot(3 + teamColor);
            }
        }

        ScoreObjective objective = sidebarObjective != null ? sidebarObjective : worldScoreboard.getObjectiveInDisplaySlot(1);
        if (objective == null) return null;

        net.minecraft.scoreboard.Scoreboard scoreboard = objective.getScoreboard();
        Collection<Score> scoreCollection = scoreboard.getSortedScores(objective);

        List<Score> visibleScores = Lists.newArrayList(Iterables.filter(scoreCollection, score -> score.getPlayerName() != null && !score.getPlayerName().startsWith("#")));

        if (visibleScores.size() > 15) {
            scoreCollection = Lists.newArrayList(Iterables.skip(visibleScores, visibleScores.size() - 15));
        } else {
            scoreCollection = visibleScores;
        }

        int width = mc.fontRendererObj.getStringWidth(objective.getDisplayName());

        for (Score score : scoreCollection) {
            ScorePlayerTeam team = scoreboard.getPlayersTeam(score.getPlayerName());
            String scoreLine = ScorePlayerTeam.formatPlayerName(team, score.getPlayerName()) + ": " + EnumChatFormatting.RED + score.getScorePoints();
            width = Math.max(width, mc.fontRendererObj.getStringWidth(scoreLine));
        }

        int height = scoreCollection.size() * mc.fontRendererObj.FONT_HEIGHT;
        int padding = 3;

        float totalWidth = width + 4;
        float totalHeight = (scoreCollection.size() + 1) * mc.fontRendererObj.FONT_HEIGHT + 1;
        float defaultX = scaledRes.getScaledWidth() - width - padding - 2;
        float defaultY = scaledRes.getScaledHeight() / 2f + height / 3f - totalHeight;

        return new ScoreboardLayout(scoreboard, objective, Lists.newArrayList(scoreCollection), width, totalWidth, totalHeight, defaultX, defaultY);
    }

    private static class ScoreboardLayout {
        private final net.minecraft.scoreboard.Scoreboard scoreboard;
        private final ScoreObjective objective;
        private final List<Score> scores;
        private final int width;
        private final float totalWidth;
        private final float totalHeight;
        private final float defaultX;
        private final float defaultY;

        ScoreboardLayout(net.minecraft.scoreboard.Scoreboard scoreboard, ScoreObjective objective, List<Score> scores, int width, float totalWidth, float totalHeight, float defaultX, float defaultY) {
            this.scoreboard = scoreboard;
            this.objective = objective;
            this.scores = scores;
            this.width = width;
            this.totalWidth = totalWidth;
            this.totalHeight = totalHeight;
            this.defaultX = defaultX;
            this.defaultY = defaultY;
        }
    }
}
