package today.vanta.client.module.impl.movement;

import today.vanta.client.event.impl.game.player.MotionEvent;
import today.vanta.client.event.impl.game.world.UpdateEvent;
import today.vanta.client.module.Category;
import today.vanta.client.module.Module;
import today.vanta.client.setting.Setting;
import today.vanta.client.setting.impl.StringSetting;
import today.vanta.util.game.events.EventListen;
import today.vanta.util.game.player.MovementUtil;

public class Speed extends Module {
    private final StringSetting
            mode = Setting.of("Mode", "NCP", "OldNCP", "Mospixel-Tick"),
            oncpmode = Setting.of("OldNCP Mode", "Y-Port", "Y-Port", "Strafe").hide(() -> !mode.getValue().equals("OldNCP"));

    public Speed() {
        super("Speed", "Makes you go faster.", Category.MOVEMENT);
        displayNames = new String[]{"Speed", "FastMove", "Zoot", "SpeedyGonzales"};
    }

    private int offGroundTicks;

    @EventListen
    private void onMotionEvent(MotionEvent event) {
        if (!mc.thePlayer.onGround) {
            offGroundTicks++;
        } else {
            offGroundTicks = 0;
        }
    }

    @EventListen
    private void onUpdate(UpdateEvent event) {
        if (MovementUtil.isMoving()) {
            mc.gameSettings.keyBindSprint.pressed = true;

            switch (mode.getValue()) {
                case "OldNCP":
                    switch (oncpmode.getValue()) {
                        case "Y-Port":
                            if (mc.thePlayer.onGround) {
                                mc.thePlayer.jump();
                                MovementUtil.strafe(0.51);
                            } else {
                                mc.thePlayer.motionY -= 0.16;
                            }
                            break;
                        case "Strafe":
                            if (mc.thePlayer.onGround) {
                                mc.thePlayer.jump();
                                MovementUtil.strafe();
                            }
                            break;
                    }
                    break;
                case "NCP":
                    if (mc.thePlayer.onGround && !mc.gameSettings.keyBindJump.isKeyDown()) {
                        mc.thePlayer.jump();
                    }

                    MovementUtil.strafe();

                    if (!MovementUtil.isMoving()) {
                        MovementUtil.stop();
                    }
                    break;

                case "Mospixel-Tick":
                    if (mc.thePlayer.onGround && !mc.gameSettings.keyBindJump.isKeyDown()) {
                        mc.thePlayer.jump();
                    }
                    //if (mc.thePlayer.onGround) {
                    //  MoveUtil.strafe(0.3f);
                    //} else {
                    //  MoveUtil.strafe(0.3f);
                    //}

                    if (offGroundTicks == 1) {
                        MovementUtil.strafe(0.35f);
                    }
                    if (offGroundTicks > 2) {
                        //MovementUtil.strafe(0.3f);
                    }
                    if (mc.thePlayer.motionY < 0.17f && offGroundTicks > 3) {
                        mc.thePlayer.motionY -= 0.05f;
                    }
            }
        }
    }

    @Override
    public void onDisable() {
        mc.gameSettings.keyBindSprint.pressed = false;
        mc.gameSettings.keyBindJump.pressed = false;
        mc.timer.timerSpeed = 1.0f;
    }

    @Override
    public String getSuffix() {
        return mode.getValue();
    }
}