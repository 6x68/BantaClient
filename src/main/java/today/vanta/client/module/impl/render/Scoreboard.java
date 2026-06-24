package today.vanta.client.module.impl.render;

import net.minecraft.scoreboard.Score;
import today.vanta.client.event.impl.game.render.ScoreboardRenderEvent;
import today.vanta.client.module.Category;
import today.vanta.client.module.Module;
import today.vanta.client.setting.Setting;
import today.vanta.client.setting.impl.BooleanSetting;
import today.vanta.util.game.events.EventListen;

public class Scoreboard extends Module {
    private final BooleanSetting canleft = Setting.of("Left", true);
    public Scoreboard() {
        super("Scoreboard", "Modifies Minecraft Scoreboard.", Category.RENDER);
    }
    @EventListen
    private void onScoreboardRender(ScoreboardRenderEvent event) {
        event.cancelled = canleft.getValue();
    }
}

