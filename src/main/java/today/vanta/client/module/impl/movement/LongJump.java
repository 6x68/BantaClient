package today.vanta.client.module.impl.movement;

import today.vanta.client.event.impl.game.player.MotionEvent;
import today.vanta.client.event.impl.game.world.UpdateEvent;
import today.vanta.client.module.Category;
import today.vanta.client.module.Module;
import today.vanta.client.setting.impl.NumberSetting;
import today.vanta.client.setting.impl.StringSetting;
import today.vanta.util.game.events.EventListen;
import today.vanta.util.game.events.EventState;
import today.vanta.util.game.player.MovementUtil;

public class LongJump extends Module {
    private final StringSetting mode = StringSetting.builder()
            .name("Mode")
            .value("NCP")
            .values("NCP", "Mospixel-Jump")
            .build();

    private final NumberSetting
            timer = NumberSetting.builder()
            .name("Timer speed")
            .value(1)
            .min(0.1)
            .max(2)
            .places(1)
            .build()
            .hide(() -> !mode.getValue().equals("NCP")),

    groundSpeed = NumberSetting.builder()
            .name("Ground speed")
            .value(0.4)
            .min(0.1)
            .max(3)
            .places(1)
            .build()
            .hide(() -> !mode.getValue().equals("NCP")),

    airSpeed = NumberSetting.builder()
            .name("Air speed")
            .value(1.4)
            .min(0.1)
            .max(3)
            .places(1)
            .build()
            .hide(() -> !mode.getValue().equals("NCP"));

    public LongJump() {
        super("LongJump", "Makes you jump longer.", Category.MOVEMENT);
    }

    private int offGroundTicks;
    private boolean canDisable;

    @EventListen
    private void onMotion(MotionEvent event) {
        if (mc.thePlayer.onGround) {
            System.out.println(offGroundTicks);
            offGroundTicks = 0;
        } else {
            offGroundTicks++;
        }
        if (event.state == EventState.PRE) {
            switch (mode.getValue()) {
                case "NCP":
                    if (MovementUtil.isMoving()) {
                        if (mc.thePlayer.onGround) {
                            mc.thePlayer.jump();
                            mc.thePlayer.motionY = MovementUtil.getJumpMotion((float) (0.424 - Math.random() / 500));
                            MovementUtil.strafe(airSpeed.getValue().doubleValue());
                        }

                        if (offGroundTicks == 1)
                            MovementUtil.strafe(groundSpeed.getValue().doubleValue());

                        if (mc.thePlayer.fallDistance > 0 && mc.thePlayer.fallDistance < 3)
                            mc.thePlayer.motionY += 0.03 + Math.random() / 500;

                        mc.timer.timerSpeed = timer.getValue().floatValue();
                    } else {
                        MovementUtil.stop();
                        mc.timer.timerSpeed = 1;
                    }
                    MovementUtil.strafe();
                    break;
                case "Mospixel-Jump":
                    if (offGroundTicks > 21) {
//            MovementUtil.strafe(MovementUtil.getMovementSpeed() + 0.5f);
                        mc.thePlayer.motionY = -0.20f;
                        if (mc.thePlayer.onGround) {
                            System.out.println("what");
                        }
                        canDisable = true;
                    }

                    if (offGroundTicks == 1) {
                        canDisable = true;
                        MovementUtil.strafe(1.5f);
                    }

                    if (canDisable && mc.thePlayer.onGround) {
                        super.setEnabled(false);
                    }

                    break;
            }

        }
    }

    @Override
    public void onEnable() {
        offGroundTicks = 0;
        canDisable = false;
        if (mc.thePlayer.onGround) {
            mc.thePlayer.jump();
        }
    }

    @Override
    public void onDisable() {
        if (mc.thePlayer == null) {
            return;
        }

        mc.timer.timerSpeed = 1.0f;
        MovementUtil.stop();
    }

    @Override
    public String getSuffix() {
        return mode.getValue();
    }
}