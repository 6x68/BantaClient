package today.vanta.client.module.impl.player;

import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import today.vanta.client.event.impl.game.world.UpdateEvent;
import today.vanta.client.module.Category;
import today.vanta.client.module.Module;
import today.vanta.client.setting.Setting;
import today.vanta.client.setting.impl.BooleanSetting;
import today.vanta.client.setting.impl.NumberSetting;
import today.vanta.util.game.events.EventListen;
import today.vanta.util.game.player.InventoryUtil;
import today.vanta.util.system.math.Counter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class ChestStealer extends Module {
    private final NumberSetting delay = Setting.of("Delay", 100, 0, 1000, "ms");
    private final NumberSetting initialDelay = Setting.of("Initial delay", 50, 0, 1000, "ms");
    private final BooleanSetting closeAfterStealing = Setting.of("Auto close", true);
    private final BooleanSetting titleCheck = Setting.of("Title check", true);
    private final BooleanSetting humanized = Setting.of("Humanized", true);
    private final NumberSetting fittsWeight = Setting.of("Fitts weight", 15, 0, 100, "%").hide(() -> !humanized.getValue());
    private final NumberSetting hickWeight = Setting.of("Hick weight", 10, 0, 100, "%").hide(() -> !humanized.getValue());

    private final Counter actionTimer = new Counter();
    private final Counter startTimer = new Counter();
    private final Random random = new Random();

    private int lastStolenSlot = -1;
    private boolean randomizeFirstSlot = true;
    private long randomizedDelay;
    private long randomizedInitialDelay;

    public ChestStealer() {
        super("ChestStealer", "Steals items from chests intelligently and efficiently.", Category.PLAYER);
    }

    @EventListen
    private void onUpdate(UpdateEvent event) {
        if (!(mc.currentScreen instanceof GuiChest)) {
            reset();
            return;
        }

        GuiChest chestGui = (GuiChest) mc.currentScreen;

        if (!startTimer.hasElapsed(randomizedInitialDelay)) return;

        if (titleCheck.getValue() && !InventoryUtil.isValidChest(chestGui)) return;

        ContainerChest containerChest;
        try {
            containerChest = (ContainerChest) chestGui.inventorySlots;
        } catch (ClassCastException e) {
            reset();
            return;
        }

        IInventory chestInventory = containerChest.getLowerChestInventory();
        handleChestStealing(chestInventory);
    }

    private void handleChestStealing(IInventory chestInventory) {
        List<ItemTakeRecord> slots = getSlots(chestInventory);

        if (slots.isEmpty() || InventoryUtil.isInventoryFull()) {
            handleCompletion();
            return;
        }

        if (!actionTimer.hasElapsed(randomizedDelay, true)) return;

        int targetSlot = selectTargetSlot(slots);
        lastStolenSlot = targetSlot;
        InventoryUtil.stealSlot(targetSlot);

        List<ItemTakeRecord> remainingSlots = getSlots(chestInventory);
        remainingSlots.removeIf(record -> record.slot == targetSlot);

        if (!remainingSlots.isEmpty()) {
            int nextSlot = selectTargetSlot(remainingSlots);
            int nextDistance = distance(nextSlot, lastStolenSlot);
            randomizedDelay = calculateHumanizedDelay(nextDistance, remainingSlots.size());
        }
    }

    private void handleCompletion() {
        if (closeAfterStealing.getValue()) {
            mc.thePlayer.closeScreen();
        }
        reset();
    }

    private List<ItemTakeRecord> getSlots(IInventory chestInventory) {
        List<ItemTakeRecord> slots = new ArrayList<>();
        for (int slot : InventoryUtil.getNonEmptySlots(chestInventory)) {
            slots.add(new ItemTakeRecord(slot, distance(slot, lastStolenSlot)));
        }
        return slots;
    }

    private int selectTargetSlot(List<ItemTakeRecord> slots) {
        if (randomizeFirstSlot) {
            randomizeFirstSlot = false;
            return slots.get(random.nextInt(slots.size())).slot;
        }
        slots.sort(Comparator.comparingInt(ItemTakeRecord::getDistance));
        return slots.get(0).slot;
    }

    private long calculateHumanizedDelay(int distance, int choices) {
        double baseDelay = delay.getValue().doubleValue();

        if (!humanized.getValue()) {
            return (long) (baseDelay * ThreadLocalRandom.current().nextDouble(0.8, 1.2));
        }

        double fittsComponent = Math.min(Math.log(distance + 1) / Math.log(2), 4.0);
        double hickComponent = Math.log(choices + 1) / Math.log(2);

        double fittsInfluence = fittsWeight.getValue().doubleValue() / 100.0;
        double hickInfluence = hickWeight.getValue().doubleValue() / 100.0;

        double calculatedDelay = baseDelay * (1.0 + fittsInfluence * fittsComponent + hickInfluence * hickComponent);

        double randomFactor = ThreadLocalRandom.current().nextDouble(0.9, 1.1);

        return (long) (calculatedDelay * randomFactor);
    }

    private int distance(int slot1, int slot2) {
        if (slot2 == -1) return Integer.MAX_VALUE;
        int row1 = slot1 / 9, col1 = slot1 % 9;
        int row2 = slot2 / 9, col2 = slot2 % 9;
        return Math.abs(row1 - row2) + Math.abs(col1 - col2);
    }

    private void reset() {
        startTimer.reset();
        actionTimer.reset();
        lastStolenSlot = -1;
        randomizeFirstSlot = true;

        randomizedInitialDelay = (long) (initialDelay.getValue().longValue()
                * ThreadLocalRandom.current().nextDouble(0.8, 1.2));
    }

    @Override
    public void onDisable() {
        reset();
    }

    public static class ItemTakeRecord {
        public final int slot;
        private final int distance;

        public ItemTakeRecord(int slot, int distance) {
            this.slot = slot;
            this.distance = distance;
        }

        public int getDistance() {
            return distance;
        }
    }
}