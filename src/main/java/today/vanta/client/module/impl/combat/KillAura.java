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
import today.vanta.client.processor.impl.RotationProcessor;
import today.vanta.client.processor.impl.TargetProcessor;
import today.vanta.client.setting.Setting;
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
            attackMode = Setting.of("Attack mode", "Single", "Single"),
            sortMode = Setting.of("Sort mode", "Range", "Range", "Health", "Armor", "Hurt-time", "Ticks", "Skin color"),
            autoBlockMode = Setting.of("Auto-block mode", "None", "None", "Vanilla", "Packet", "Hold"),
            swingMode = Setting.of("Swing mode", "Legit", "Legit", "Blatant");

    public final NumberSetting
            attackRange = Setting.of("Attack range", 3.4, 1, 6, 1, "m"),
            searchRange = Setting.of("Search range", 4.3, 1, 7, 1, "m");

    public final MultiStringSetting entities = Setting.of("Entities", new String[]{"Players"}, new String[]{"Players", "Animals", "Monsters"});

    private final NumberSetting
            maxCPS = Setting.of("Max CPS", 11, 1, 20),
            minCPS = Setting.of("Min CPS", 10, 1, 20);

    public final BooleanSetting
            raytrace = Setting.of("Raytrace", true),
            noSwing = Setting.of("No swing", false).hide(() -> !swingMode.getValue().equals("Blatant")),
            sprintReset = Setting.of("Sprint reset", true),
            keepSprint = Setting.of("Keep sprint", false).hide(sprintReset::getValue),
            swingOnHurtTime = Setting.of("Swing on hurt-time", false);

    private float previousAttackRange;

    public KillAura() {
        super("KillAura", "Attacks entities in proximity.", Category.COMBAT);
        displayNames = new String[]{"KillAura", "Killaura", "Aura"};

        previousAttackRange = attackRange.getValue().floatValue();

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
        if (Vanta.instance.moduleStorage.getT(KeepSprint.class).isEnabled()) return;
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
            RotationProcessor.getInstance().setTargetRotation(rots);
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

            if (TargetProcessor.getInstance().target != null && mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword) {
                if (autoBlockMode.getValue().equals("Packet") || autoBlockMode.getValue().equals("Hold")) {
                    stopPacketBlock();
                }
            }

            if (autoBlockMode.getValue().equals("Hold")) {
                if (TargetProcessor.getInstance().target != null &&
                        mc.thePlayer.getHeldItem() != null &&
                        mc.thePlayer.getHeldItem().getItem() instanceof ItemSword) {
                    if (!mc.thePlayer.isUsingItem()) {
                        mc.thePlayer.setItemInUse(mc.thePlayer.getHeldItem(), mc.thePlayer.getHeldItem().getMaxItemUseDuration());
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
        } else if (event.state == EventState.POST) {
            if (TargetProcessor.getInstance().target != null && mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword) {
                if (autoBlockMode.getValue().equals("Packet") || autoBlockMode.getValue().equals("Hold")) {
                    startPacketBlock();
                }
            }
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

                        if (autoBlockMode.getValue().equals("Vanilla") && isBlocking) {
                            stopVanillaBlock();
                        }

                        if (!noSwing.getValue())
                            if (swingOnHurtTime.getValue()) {
                                if (TargetProcessor.getInstance().target.hurtTime < 2) {
                                    mc.thePlayer.swingItem();
                                }
                            } else {
                                mc.thePlayer.swingItem();
                            }
                        else if (swingOnHurtTime.getValue()) {
                            if (TargetProcessor.getInstance().target.hurtTime < 1) {
                                mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
                            }
                        } else {
                            mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
                        }

                        switch (swingMode.getValue()) {
                            case "Legit":
                                mc.clickMouse();
                                break;
                            case "Blatant":
                                mc.playerController.attackEntity(mc.thePlayer, TargetProcessor.getInstance().target);
                                break;
                        }

                        if (autoBlockMode.getValue().equals("Vanilla")) {
                            blockDelay = 2;
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
                case "Hold":
                    startVanillaBlock();
                    break;
                case "Packet":
                    startPacketBlock();
                    break;
            }
        } else {
            if (isBlocking) {
                mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(
                        C07PacketPlayerDigging.Action.RELEASE_USE_ITEM,
                        BlockPos.ORIGIN,
                        EnumFacing.DOWN));

                if (mc.thePlayer.isUsingItem()) {
                    mc.thePlayer.stopUsingItem();
                }

                isBlocking = false;
            }
        }
    }

    private void startVanillaBlock() {
        if (!isBlocking && mc.thePlayer.getHeldItem() != null &&
                mc.thePlayer.getHeldItem().getItem() instanceof ItemSword) {
            mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem());
            mc.thePlayer.setItemInUse(mc.thePlayer.getHeldItem(), mc.thePlayer.getHeldItem().getMaxItemUseDuration());
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
        if (isBlocking)
            performBlock(false);

        isAttacking = false;
        rots = null;
    }

    @Override
    public String getSuffix() {
        return sortMode.getValue();
    }
}