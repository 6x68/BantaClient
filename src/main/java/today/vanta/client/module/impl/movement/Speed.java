package today.vanta.client.module.impl.movement;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import today.vanta.client.event.impl.game.network.ReceivePacketEvent;
import today.vanta.client.event.impl.game.player.MotionEvent;
import today.vanta.client.event.impl.game.render.Render2DEvent;
import today.vanta.client.event.impl.game.world.UpdateEvent;
import today.vanta.client.module.Category;
import today.vanta.client.module.Module;
import today.vanta.client.setting.Setting;
import today.vanta.client.setting.impl.StringSetting;
import today.vanta.util.game.events.EventListen;
import today.vanta.util.game.player.ChatUtil;
import today.vanta.util.game.player.MovementUtil;

public class Speed extends Module {
    private final StringSetting
            mode = Setting.of("Mode", "NCP", "OldNCP", "Mospixel-Basic", "Mospixel", "NCP", "Miniblox-Ground"),
            oncpmode = Setting.of("OldNCP Mode", "Y-Port", "Y-Port", "Strafe").hide(() -> !mode.getValue().equals("OldNCP"));

    public Speed() {
        super("Speed", "Makes you go faster.", Category.MOVEMENT);
        displayNames = new String[]{"Speed", "FastMove", "Zoot", "SpeedyGonzales"};
    }

    private int offGroundTicks;
    ScaledResolution sr = new ScaledResolution(mc);
    private int tick;
    private boolean flag;

    @EventListen
    private void onMotionEvent(MotionEvent event) {
        if (!mc.thePlayer.onGround) {
            offGroundTicks++;
        } else {
            offGroundTicks = 0;
        }
    }

    @EventListen
    private void onRender2D(Render2DEvent event) {
        if (flag) {
            mc.fontRendererObj.drawString("Detected flag! Ticks left: "+ String.valueOf(60-tick),565, 400, 0xFFFFFF,true);
        }
    }

    @EventListen
    private void onPacket(ReceivePacketEvent event) {
        if (event.packet instanceof S08PacketPlayerPosLook) {
            flag = true;
        }
    }

    @EventListen
    private void onUpdate(UpdateEvent event) {
        if (flag) {
            tick++;
        }
        if (tick > 60) {
            tick = 0;
            flag = false;
        }
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

                case "Mospixel-Basic":
                    if (mc.thePlayer.onGround && !mc.gameSettings.keyBindJump.isKeyDown()) {
                        mc.thePlayer.jump();
                    }
                    //if (mc.thePlayer.onGround) {
                    //  MovementUtil.strafe(0.3f);
                    //} else {
                    //  MovementUtil.strafe(0.3f);
                    //}

                    if (offGroundTicks > 2) {
                        //MovementUtil.strafe(0.3f);
                    }
                    if (mc.thePlayer.motionY < 0.17f && offGroundTicks > 3) {
                        mc.thePlayer.motionY -= 0.05f;
                    }
                    break;
                case "Mospixel":
                    if (flag && tick >= 60) {
                        tick = 0;
                        flag = false;
                    }
                    if (flag) {
                        return;
                    }
//                    if (mc.thePlayer.onGround && !mc.gameSettings.keyBindJump.isKeyDown()) {
//                        mc.thePlayer.jump();
//                    }
//                    if (mc.thePlayer.onGround) {
//                        MovementUtil.strafe(0.39f);
//                    }
//                    if (mc.thePlayer.motionY < 0.421) {
//                        MovementUtil.strafe(MovementUtil.getSpeed() * 1.03f);
//                    }
                    if (mc.thePlayer.onGround && !mc.gameSettings.keyBindJump.isKeyDown()) {
                        mc.thePlayer.jump();
                    }
                    if (offGroundTicks == 2) {
                        MovementUtil.strafe(0.33f);
//                        ChatUtil.send(ChatUtil.Prefix.INFO, String.valueOf(MovementUtil.getBPS()));
                    } else {
                        MovementUtil.strafe();
                    }
                    if (offGroundTicks > 12 && mc.thePlayer.motionY < 0.421f) {
                        mc.thePlayer.motionY -= 0.07f;
                    }

                    if (!MovementUtil.isMoving()) {
                        MovementUtil.stop();
                    }
                    break;
                case "Miniblox-Ground":
                    if (mc.thePlayer.onGround) {
                        MovementUtil.strafe(0.16f);
                    }
                    mc.thePlayer.setSprinting(false);

                    break;
            }
        }
    }

    @Override
    public void onDisable() {
        mc.gameSettings.keyBindSprint.pressed = false;
        mc.gameSettings.keyBindJump.pressed = false;
        mc.timer.timerSpeed = 1.0f;
        offGroundTicks = 0;
        tick = 0;
        flag = false;

    }

    @Override
    public String getSuffix() {
        return mode.getValue();
    }
}