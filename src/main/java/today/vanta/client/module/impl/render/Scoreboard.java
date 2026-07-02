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
    }

    @Override
    public void onEnable() {
        if (x.getValue().floatValue() < 0 || y.getValue().floatValue() < 0) {
            float[] defaultPos = calculateDefaultPosition();
            if (defaultPos != null) {
                x.setValue(defaultPos[0]);
                y.setValue(defaultPos[1]);
            }
        }
    }

    @EventListen
    private void onScoreboardRender(ScoreboardRenderEvent event) {
        event.cancelled = true;
    }

    @EventListen
    private void onDrawScreen(RenderScreenEvent event) {
        if (mc.thePlayer == null || mc.theWorld == null) return;

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

        if (objective != null) {
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

            if (x.getValue().floatValue() < 0 || y.getValue().floatValue() < 0) {
                float defaultX = scaledRes.getScaledWidth() - width - padding - 2;
                float defaultY = scaledRes.getScaledHeight() / 2 + height / 3 - totalHeight;
                x.setValue(defaultX);
                y.setValue(defaultY);
            }

            float boundsX = x.getValue().floatValue();
            float boundsY = y.getValue().floatValue();

            float renderX = boundsX + 2;
            float renderY = boundsY + totalHeight;

            if (mc.currentScreen instanceof GuiChat) {
                handleDragging(event.mouseX, event.mouseY, boundsX, boundsY, totalWidth, totalHeight);
            } else if (mc.currentScreen != null) {
                return;
            }

            int lineIndex = 0;

            for (Score score : scoreCollection) {
                lineIndex++;

                ScorePlayerTeam team = scoreboard.getPlayersTeam(score.getPlayerName());
                String playerName = ScorePlayerTeam.formatPlayerName(team, score.getPlayerName());

                float rectY = renderY - lineIndex * mc.fontRendererObj.FONT_HEIGHT;
                float rightX = renderX + width + 2;

                Rectangle
                        .create(boundsX, rectY, totalWidth, mc.fontRendererObj.FONT_HEIGHT)
                        .color(new Color(0, 0, 0, 80))
                        .push(event);

                mc.fontRendererObj.drawString(playerName, (int) renderX, (int) rectY, 553648127);

                if (!removeScore.getValue()) {
                    String points = EnumChatFormatting.RED + "" + score.getScorePoints();
                    mc.fontRendererObj.drawString(points, (int) (rightX - mc.fontRendererObj.getStringWidth(points)), (int) rectY, 553648127);
                }

                if (lineIndex == scoreCollection.size()) {
                    String objectiveName = objective.getDisplayName();

                    Rectangle
                            .create(boundsX, rectY - mc.fontRendererObj.FONT_HEIGHT - 1, totalWidth, mc.fontRendererObj.FONT_HEIGHT)
                            .color(new Color(0, 0, 0, 96))
                            .push(event);
                    Rectangle
                            .create(boundsX, rectY - 1, totalWidth, 1)
                            .color(new Color(0, 0, 0, 80))
                            .push(event);

                    mc.fontRendererObj.drawString(objectiveName, (int) (renderX + width / 2 - mc.fontRendererObj.getStringWidth(objectiveName) / 2), (int) (rectY - mc.fontRendererObj.FONT_HEIGHT), 553648127);
                }
            }

            if (dragging && mc.currentScreen instanceof GuiChat) {
                Rectangle
                        .create(boundsX - 0.5, boundsY - 0.5, totalWidth + 1, totalHeight + 1)
                        .color(new Color(255, 255, 255, 150))
                        .outline(true)
                        .push(event);
            }
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

    private float[] calculateDefaultPosition() {
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

        float totalHeight = (scoreCollection.size() + 1) * mc.fontRendererObj.FONT_HEIGHT + 1;

        float defaultX = scaledRes.getScaledWidth() - width - padding - 2;
        float defaultY = scaledRes.getScaledHeight() / 2 + height / 3 - totalHeight;

        return new float[]{defaultX, defaultY};
    }
}
