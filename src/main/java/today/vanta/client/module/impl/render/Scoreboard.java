package today.vanta.client.module.impl.render;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.EnumChatFormatting;
import today.vanta.client.event.impl.client.RenderOverlayEvent;
import today.vanta.client.event.impl.game.render.ScoreboardRenderEvent;
import today.vanta.client.module.Category;
import today.vanta.client.module.Module;
import today.vanta.client.setting.Setting;
import today.vanta.client.setting.impl.BooleanSetting;
import today.vanta.util.game.events.EventListen;
import today.vanta.util.game.render.shape.impl.Rectangle;

import java.awt.*;
import java.util.Collection;
import java.util.List;

public class Scoreboard extends Module {
    private final BooleanSetting removeScore = Setting.of("Remove score", true);

    public Scoreboard() {
        super("Scoreboard", "Modifies Minecraft Scoreboard.", Category.RENDER);
    }

    @EventListen
    private void onScoreboardRender(ScoreboardRenderEvent event) {
        event.cancelled = true;
    }

    @EventListen
    private void onRender(RenderOverlayEvent event) {
        ScaledResolution scaledRes = event.scaledResolution;

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
            int y = scaledRes.getScaledHeight() / 2 + height / 3;
            int padding = 3;
            int x = scaledRes.getScaledWidth() - width - padding;
            int lineIndex = 0;

            for (Score score : scoreCollection) {
                lineIndex++;

                ScorePlayerTeam team = scoreboard.getPlayersTeam(score.getPlayerName());
                String playerName = ScorePlayerTeam.formatPlayerName(team, score.getPlayerName());

                int rectY = y - lineIndex * mc.fontRendererObj.FONT_HEIGHT;
                int rightX = scaledRes.getScaledWidth() - padding + 2;

                int rectX = x - 2;
                int rectWidth = rightX - rectX;

                Rectangle
                        .create(rectX, rectY, rectWidth, mc.fontRendererObj.FONT_HEIGHT)
                        .color(new Color(0, 0, 0, 80))
                        .push(event);

                mc.fontRendererObj.drawString(playerName, x, rectY, 553648127);

                if (!removeScore.getValue()) {
                    String points = EnumChatFormatting.RED + "" + score.getScorePoints();
                    mc.fontRendererObj.drawString(points, rightX - mc.fontRendererObj.getStringWidth(points), rectY, 553648127);
                }

                if (lineIndex == scoreCollection.size()) {
                    String objectiveName = objective.getDisplayName();

                    Rectangle
                            .create(rectX, rectY - mc.fontRendererObj.FONT_HEIGHT - 1, rectWidth, mc.fontRendererObj.FONT_HEIGHT)
                            .color(new Color(0, 0, 0, 96))
                            .push(event);
                    Rectangle
                            .create(rectX, rectY - 1, rectWidth, 1)
                            .color(new Color(0, 0, 0, 80))
                            .push(event);

                    mc.fontRendererObj.drawString(objectiveName, x + width / 2 - mc.fontRendererObj.getStringWidth(objectiveName) / 2, rectY - mc.fontRendererObj.FONT_HEIGHT, 553648127);
                }
            }
        }
    }
}