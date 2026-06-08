package today.vanta.client.module.impl.combat;

import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import today.vanta.Vanta;
import today.vanta.client.event.impl.game.player.KeepSprintEvent;
import today.vanta.client.event.impl.game.player.MotionEvent;
import today.vanta.client.event.impl.game.player.SprintEvent;
import today.vanta.client.module.Category;
import today.vanta.client.module.Module;
import today.vanta.client.processor.impl.TargetProcessor;
import today.vanta.client.setting.impl.BooleanSetting;
import today.vanta.client.setting.impl.MultiStringSetting;
import today.vanta.client.setting.impl.NumberSetting;
import today.vanta.client.setting.impl.StringSetting;
import today.vanta.util.game.events.EventListen;
import today.vanta.util.game.events.EventPriority;
import today.vanta.util.game.events.EventState;
import today.vanta.util.game.player.RotationUtil;
import today.vanta.util.game.player.constructors.Rotation;
import today.vanta.util.system.math.Counter;

public class KillAura extends Module {
    public final StringSetting
            attackMode = StringSetting.builder()
            .name("Attack mode")
            .value("Single")
            .values("Single")
            .build(),

    sortMode = StringSetting.builder()
            .name("Sort mode")
            .value("Range")
            .values("Range", "Health", "Armor", "Hurt-time", "Ticks", "Skin color")
            .build(),

    autoBlockMode = StringSetting.builder()
            .name("Auto-block mode")
            .value("None")
            .values("None", "Vanilla", "Packet", "Hold")
            .build();

    private final StringSetting swingMode;
    public final NumberSetting attackRange, searchRange = NumberSetting.builder()
            .name("Search range")
            .value(4.3)
            .min(1)
            .max(7)
            .places(1)
            .suffix("m")
            .build();

    public final MultiStringSetting entities = MultiStringSetting.builder()
            .name("Entities")
            .value("Players")
            .values("Players", "Animals", "Monsters")
            .build();

    private final NumberSetting maxCPS, minCPS;

    public final BooleanSetting
            raytrace = BooleanSetting.builder()
            .name("Raytrace")
            .value(true)
            .build(),

    noSwing = BooleanSetting.builder()
            .name("No swing")
            .value(false)
            .build(),

    sprintReset = BooleanSetting.builder()
            .name("Sprint reset")
            .value(true)
            .build(),

    keepSprint = BooleanSetting.builder()
            .name("Keep sprint")
            .value(false)
            .build()
            .hide(sprintReset::getValue);

    private float previousAttackRange;

    public KillAura() {
        super("KillAura", "Attacks entities in proximity.", Category.COMBAT);
        displayNames = new String[]{"KillAura", "Killaura", "Aura"};

        swingMode = StringSetting.builder()
                .name("Swing mode")
                .value("Legit")
                .values("Legit", "Blatant")
                .build();

        attackRange = NumberSetting.builder()
                .name("Attack range")
                .value(3.4)
                .min(1)
                .max(6)
                .places(1)
                .suffix("m")
                .build();

        previousAttackRange = attackRange.getValue().floatValue();

        maxCPS = NumberSetting.builder()
                .name("Max CPS")
                .value(11)
                .min(1)
                .max(20)
                .build();

        minCPS = NumberSetting.builder()
                .name("Min CPS")
                .value(10)
                .min(1)
                .max(20)
                .build();

        swingMode.addListener((setting, oldValue, newValue) -> {
            if (newValue.equals("Legit") && attackRange.getValue().floatValue() > 3.4f) {
                previousAttackRange = attackRange.getValue().floatValue();
                attackRange.setValue(3.4f);
            }

            if (newValue.equals("Blatant") && attackRange.getValue().floatValue() >= 3.4f) {
                attackRange.setValue(previousAttackRange);
            }
        });

        attackRange.addListener((setting, oldValue, newValue) -> {
            if (swingMode.getValue().equals("Legit") && newValue.floatValue() > 3.4f) {
                setting.setValue(3.4f);
            }
        });

        maxCPS.addListener((setting, oldValue, newValue) -> {
            if (newValue.floatValue() < minCPS.getValue().floatValue()) {
                setting.setValue(minCPS.getValue());
            }
        });

        minCPS.addListener((setting, oldValue, newValue) -> {
            if (newValue.floatValue() > maxCPS.getValue().floatValue()) {
                setting.setValue(maxCPS.getValue());
            }
        });
    }

    private final Counter attackCounter = new Counter();
    private float rangeFix = 3;
    private boolean isBlocking = false;
    private int blockDelay = 0;
    private boolean isAttacking = false;

    private Rotation rots;

    @EventListen
    private void onSprintAttack(SprintEvent event) {
        if (sprintReset.getValue() && rots != null) {
            event.cancelled = true;
        }
    }

    @EventListen
    private void onSprint(KeepSprintEvent event) {
        if (keepSprint.getValue()) {
            event.greater = false;
        }
    }

    @EventListen(priority = EventPriority.HIGHEST)
    private void onRotation(MotionEvent event) {
        if (Vanta.instance.moduleStorage.getModule("Scaffold").isEnabled()) {
            return;
        }

        if (TargetProcessor.getInstance().target != null) {
            rots = RotationUtil.getSimpleRotations(TargetProcessor.getInstance().target);
            setRotations(rots, event);
        }
    }

    @EventListen
    private void onMotion(MotionEvent event) {
        if (event.state == EventState.PRE) {
            if (mc.thePlayer.ticksExisted % 20 == 0) {
                rangeFix = (int) (attackRange.getValue().floatValue() + Math.random() * 0.4);
            }

            if (blockDelay > 0) {
                blockDelay--;
            }

            if (autoBlockMode.getValue().equals("Hold")) {
                if (TargetProcessor.getInstance().target != null &&
                        mc.thePlayer.getHeldItem() != null &&
                        mc.thePlayer.getHeldItem().getItem() instanceof ItemSword) {
                    if (!isBlocking && !isAttacking) {
                        startVanillaBlock();
                    }
                } else if (isBlocking) {
                    stopVanillaBlock();
                }
            }

            if (autoBlockMode.getValue().equals("Vanilla") &&
                    TargetProcessor.getInstance().target != null &&
                    !isBlocking &&
                    blockDelay == 0 &&
                    !isAttacking &&
                    mc.thePlayer.getHeldItem() != null &&
                    mc.thePlayer.getHeldItem().getItem() instanceof ItemSword) {
                startVanillaBlock();
            }

            handleAttack();
        }
    }

    private void handleAttack() {
        if (Vanta.instance.moduleStorage.getModule("Scaffold").isEnabled()) {
            if (isBlocking && !autoBlockMode.getValue().equals("Hold")) {
                performBlock(false);
            }
            return;
        }

        if (TargetProcessor.getInstance().target == null) {
            rots = null;
            if (isBlocking && !autoBlockMode.getValue().equals("Hold")) {
                performBlock(false);
            }
            return;
        }

        if (mc.thePlayer != null && TargetProcessor.getInstance().target != null) {
            switch (attackMode.getValue()) {
                case "Single":
                    if (attackCounter.hasElapsed(calculateAttackDelay(), true) &&
                            TargetProcessor.getInstance().target.getDistanceToEntity(mc.thePlayer) <= rangeFix) {

                        isAttacking = true;

                        if (autoBlockMode.getValue().equals("Hold") && isBlocking) {
                            stopVanillaBlock();
                        }

                        if (autoBlockMode.getValue().equals("Vanilla") && isBlocking) {
                            stopVanillaBlock();
                        }

                        if (autoBlockMode.getValue().equals("Packet")) {
                            startPacketBlock();
                        }

                        if (!noSwing.getValue())
                            mc.thePlayer.swingItem();
                        else
                            mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());

                        switch (swingMode.getValue()) {
                            case "Legit":
                                mc.clickMouse();
                                break;
                            case "Blatant":
                                mc.playerController.attackEntity(mc.thePlayer, TargetProcessor.getInstance().target);
                                break;
                        }

                        if (autoBlockMode.getValue().equals("Packet")) {
                            stopPacketBlock();
                        }

                        if (autoBlockMode.getValue().equals("Vanilla")) {
                            blockDelay = 2;
                        }

                        if (autoBlockMode.getValue().equals("Hold") &&
                                TargetProcessor.getInstance().target != null) {
                            startVanillaBlock();
                        }

                        isAttacking = false;
                    }
                    break;
            }
        }
    }

    private long calculateAttackDelay() {
        long cps = (minCPS.getValue().longValue() + maxCPS.getValue().longValue()) / 2;
        return 1000 / cps;
    }

    private void performBlock(boolean start) {
        if (start) {
            switch (autoBlockMode.getValue()) {
                case "Vanilla":
                    startVanillaBlock();
                    break;
                case "Packet":
                    startPacketBlock();
                    break;
            }
        } else {
            switch (autoBlockMode.getValue()) {
                case "Vanilla":
                    stopVanillaBlock();
                    break;
                case "Packet":
                    stopPacketBlock();
                    break;
            }
        }
    }

    private void startVanillaBlock() {
        if (!isBlocking && mc.thePlayer.getHeldItem() != null &&
                mc.thePlayer.getHeldItem().getItem() instanceof ItemSword) {
            mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem());
            isBlocking = true;
        }
    }

    private void startPacketBlock() {
        if (mc.thePlayer.getHeldItem() != null &&
                mc.thePlayer.getHeldItem().getItem() instanceof ItemSword &&
                !isBlocking) {
            mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
            isBlocking = true;
        }
    }

    private void stopPacketBlock() {
        if (isBlocking) {
            mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(
                    C07PacketPlayerDigging.Action.RELEASE_USE_ITEM,
                    BlockPos.ORIGIN,
                    EnumFacing.DOWN));
            isBlocking = false;
        }
    }

    private void stopVanillaBlock() {
        if (isBlocking) {
            mc.thePlayer.stopUsingItem();
            isBlocking = false;
        }
    }

    @Override
    public void onDisable() {
        if (isBlocking) {
            performBlock(false);
        }
        isAttacking = false;
        rots = null;
    }

    @Override
    public String getSuffix() {
        return sortMode.getValue();
    }
}