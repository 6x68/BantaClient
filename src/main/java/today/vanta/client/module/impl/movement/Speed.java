package today.vanta.client.module.impl.movement;

import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.potion.Potion;
import today.vanta.client.event.impl.game.network.ReceivePacketEvent;
import today.vanta.client.event.impl.game.player.MotionEvent;
import today.vanta.client.event.impl.game.render.Render2DEvent;
import today.vanta.client.event.impl.game.world.UpdateEvent;
import today.vanta.client.module.Category;
import today.vanta.client.module.Module;
import today.vanta.client.setting.Setting;
import today.vanta.client.setting.impl.BooleanSetting;
import today.vanta.client.setting.impl.NumberSetting;
import today.vanta.client.setting.impl.StringSetting;
import today.vanta.util.game.events.EventListen;
import today.vanta.util.game.player.ChatUtil;
import today.vanta.util.game.player.MovementUtil;

public class Speed extends Module {
    private final StringSetting
            mode = Setting.of("Mode", "NCP", "OldNCP", "Mospixel-Basic", "Mospixel", "NCP", "Miniblox-Ground", "Custom"),
            oncpmode = Setting.of("OldNCP Mode", "Y-Port", "Y-Port", "Strafe").hide(() -> !mode.getValue().equals("OldNCP"));
    private final BooleanSetting shouldjump = Setting.of("Should Jump",true).hide(() -> !mode.getValue().equals("Custom"));
    private final NumberSetting jumpamount = Setting.of("Jump Motion",0.42f,0.01,2,3).hide(() -> !shouldjump.getValue() || !mode.getValue().equals("Custom"));
    private final BooleanSetting strafe = Setting.of("Should Strafe", true).hide(() -> !mode.getValue().equals("Custom"));
    private final NumberSetting strafeamount = Setting.of("Strafe Amount", 0, 0, 2,2).hide(() -> !strafe.getValue() || !mode.getValue().equals("Custom"));
    private final BooleanSetting groundstrafe = Setting.of("Should Ground Strafe", true).hide(() -> !mode.getValue().equals("Custom"));
    private final NumberSetting groundstrafeamount = Setting.of("Ground Strafe Amount", 0.2, 0.01, 2,2).hide(() -> !groundstrafe.getValue() || !mode.getValue().equals("Custom"));
    private final BooleanSetting groundonstrafe = Setting.of("Ground Strafe Only on Strafe", true).hide(() -> !mode.getValue().equals("Custom") && !groundstrafe.getValue());
    private final BooleanSetting shouldtickstrafe = Setting.of("Should Tick Strafe", false).hide(() -> !mode.getValue().equals("Custom"));
    private final NumberSetting tickstrafeamount = Setting.of("Tick Strafe Amount", 0.2,0.01,2,2).hide(() -> !shouldtickstrafe.getValue() || !mode.getValue().equals("Custom"));
    private final BooleanSetting shouldlowhop = Setting.of("Should Lowhop", false).hide(() -> !mode.getValue().equals("Custom"));
    private final NumberSetting lowhopstrength = Setting.of("Lowhop Strength", 0.2,0.01,10,2).hide(() -> !shouldlowhop.getValue() || !mode.getValue().equals("Custom")),
            lowhoptick = Setting.of("Lowhop Tick", 2,1,50,0).hide(() -> !shouldlowhop.getValue() || !mode.getValue().equals("Custom"));


    public Speed() {
        super("Speed", "Makes you go faster.", Category.MOVEMENT);
        displayNames = new String[]{"Speed", "FastMove", "Zoot", "SpeedyGonzales"};
        super.setEnabled(false);
    }

    private int offGroundTicks;
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
            mc.fontRendererObj.drawString("Detected flag! Ticks left: " + (60 - tick), event.scaledResolution.getScaledWidth() / 2 - (mc.fontRendererObj.getStringWidth("Detected flag! Ticks left: " + (60 - tick)) / 2), 400, 0xFFFFFF, true);
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

        if (MovementUtil.isMoving() && !mc.thePlayer.isInWater()) {
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

                    if (offGroundTicks == 2 && mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
                        MovementUtil.strafe(0.48f);
                        ChatUtil.send(ChatUtil.Prefix.INFO, "yes" + Math.random());
                    }
                    if (offGroundTicks == 2 && !mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
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

                case "Custom":
                    if (shouldjump.getValue()) {
                        if (mc.thePlayer.onGround && !mc.gameSettings.keyBindJump.isKeyDown()) {
                            if (jumpamount.getValue().floatValue() != 0.42f) {
                                mc.thePlayer.motionY += jumpamount.getValue().floatValue();
                            } else {
                                mc.thePlayer.jump();
                            }
                        }
                    }
                    if (strafe.getValue()) {
                        if (strafeamount.getValue().floatValue() == 0) {
                            MovementUtil.strafe();
                        } else {
                            MovementUtil.strafe(strafeamount.getValue().floatValue());
                        }
                    }

                    if (groundstrafe.getValue() && mc.thePlayer.onGround) {
                        if (groundonstrafe.getValue()) {
                            if (mc.gameSettings.keyBindLeft.isKeyDown() || mc.gameSettings.keyBindRight.isKeyDown()) {
                                MovementUtil.strafe(groundstrafeamount.getValue().floatValue());
                            }
                        } else {
                            MovementUtil.strafe(groundstrafeamount.getValue().floatValue());
                        }
                    }

                    if (shouldtickstrafe.getValue()) {
                        if (offGroundTicks == 2) {
                            MovementUtil.strafe(tickstrafeamount.getValue().floatValue());
                        }
                    }

                    if (shouldlowhop.getValue()) {
                        if (offGroundTicks == lowhoptick.getValue().intValue()) {
                            mc.thePlayer.motionY -= lowhopstrength.getValue().floatValue();
                        }
                    }
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