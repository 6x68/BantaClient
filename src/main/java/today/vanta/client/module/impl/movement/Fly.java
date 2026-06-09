package today.vanta.client.module.impl.movement;

import today.vanta.client.event.impl.game.world.UpdateEvent;
import today.vanta.client.module.Category;
import today.vanta.client.module.Module;
import today.vanta.client.setting.impl.StringSetting;
import today.vanta.util.game.events.EventListen;
import today.vanta.util.game.player.MovementUtil;

public class Fly extends Module {
    private final StringSetting mode = StringSetting.builder()
            .name("Mode")
            .value("Miniblox")
            .values("Vanilla", "Miniblox")
            .build();

    public Fly() {
        super("Fly", "Allows you to fly like a pelican.", Category.MOVEMENT);
    }

    @EventListen
    private void onUpdate(UpdateEvent event) {
        switch (mode.getValue()) {
            case "Miniblox":
                mc.thePlayer.motionY = 0f;
                MovementUtil.strafe(0.15f);
                break;
            case "Vanilla":
                mc.thePlayer.motionY = 0f;
                MovementUtil.strafe(1f);
                if (mc.gameSettings.keyBindJump.isKeyDown()) {
                    mc.thePlayer.motionY = 1f;
                }

                if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                    mc.thePlayer.motionY = -1f;
                }
                break;
        }
//        if (mode.getValue().equals("Miniblox")) {
//            mc.thePlayer.motionY = 0f;
//            MovementUtil.strafe(0.2f);
//        }
    }

    @Override
    public void onEnable() {
        if (mc.thePlayer == null) return;

        if (mode.getValue().equals("Miniblox"))
            mc.thePlayer.sendChatMessage("/desync");
    }

    @Override
    public void onDisable() {
        if (mc.thePlayer == null) return;

        if (mode.getValue().equals("Miniblox"))
            mc.thePlayer.sendChatMessage("/desync");
    }

    @Override
    public String getSuffix() {
        return mode.getValue();
    }
}