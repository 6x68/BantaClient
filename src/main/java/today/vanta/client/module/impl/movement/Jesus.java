package today.vanta.client.module.impl.movement;

import today.vanta.client.event.impl.game.player.MoveEvent;
import today.vanta.client.module.Category;
import today.vanta.client.module.Module;
import today.vanta.client.setting.Setting;
import today.vanta.client.setting.impl.StringSetting;
import today.vanta.util.game.events.EventListen;
import today.vanta.util.game.player.MovementUtil;

public class Jesus extends Module {
    private final StringSetting mode = Setting.of("Mode", "Basic", "Vulcan", "Basic");

    private boolean canslow;

    public Jesus() {
        super("Jesus", "Lets you walk on water like jesus supposedly did.", Category.MOVEMENT);
        displayNames = new String[]{"Jesus", "LiquidWalk", "WaterWalk", "WaterHack"};
    }

    @EventListen
    public void onMove(MoveEvent event) {
        if (mode.getValue().equals("Basic")) {
            if (mc.thePlayer.isInWater()) {
                if (!mc.gameSettings.keyBindJump.isKeyDown() || !mc.gameSettings.keyBindSneak.isKeyDown()) {
                    mc.thePlayer.motionY = 0;
                }

                if (mc.gameSettings.keyBindJump.isKeyDown()) {
                    mc.thePlayer.motionY = 0.2f;
                } else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                    mc.thePlayer.motionY = 0.2f;
                } else {
                    mc.thePlayer.motionY = 0;
                }
            }
        }
        if (mode.getValue().equals("Vulcan")) {
            if (mc.thePlayer.isInWater() || mc.thePlayer.isInLava() && !mc.thePlayer.onGround) {
//                mc.thePlayer.motionY = 0.5f;

                if (mc.gameSettings.keyBindJump.isKeyDown()) {
                    mc.thePlayer.motionY = 0.15f;
                }

                if (!mc.gameSettings.keyBindJump.isKeyDown() || !mc.gameSettings.keyBindSneak.isKeyDown()) {
                    mc.thePlayer.motionY = -0.001f;
                    event.setSpeed(0.2f);
                    canslow = true;
                } else {
                    if (canslow) {
                        MovementUtil.stop();
                        event.setSpeed(0.01f);
                        canslow = false;
                    }
                }
            }
        }

    }

    @Override
    public String getSuffix() {
        return mode.getValue();
    }
}