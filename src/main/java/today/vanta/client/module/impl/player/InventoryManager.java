package today.vanta.client.module.impl.player;

import net.minecraft.block.Block;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.item.*;
import today.vanta.client.event.impl.game.world.UpdateEvent;
import today.vanta.client.module.Category;
import today.vanta.client.module.Module;
import today.vanta.client.processor.impl.TargetProcessor;
import today.vanta.client.setting.Setting;
import today.vanta.client.setting.impl.BooleanSetting;
import today.vanta.client.setting.impl.NumberSetting;
import today.vanta.util.game.events.EventListen;
import today.vanta.util.game.player.InventoryUtil;
import today.vanta.util.system.math.Counter;
import today.vanta.util.system.math.MathUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class InventoryManager extends Module {
    private final NumberSetting
            minDelay = Setting.of("Min Delay", 300, 10,1000),
            maxDelay = Setting.of("Max Delay", 300, 10, 1000);
    private final NumberSetting startDelay = Setting.of("Start Delay", 100,10,1000);
    private final BooleanSetting
            inventoryOnly = Setting.of("Inventory Only", true),
            exitOnEnemy = Setting.of("Exit on enemy", true);
    private final BooleanSetting
            keepSword = Setting.of("Keep Swords", true),
            keepPickaxe = Setting.of("Keep Pickaxes", true),
            keepAxe = Setting.of("Keep axes", true),
            keepShovel = Setting.of("Keep shovels", true),
            keepBow = Setting.of("Keep bows", true),
            keepFood = Setting.of("Keep food", true);
    private final NumberSetting
            swordSlot = Setting.of("Sword Slot" , 1, 1, 9),
            bowSlot = Setting.of("Bow Slot", 2,1,9),
            axeSlot = Setting.of("Axe Slot", 3,1,9),
            pickaxeSlot = Setting.of("Pickaxe Slot", 4, 1, 9),
            shovelSlot = Setting.of("Shovel Slot", 5,1,9),
            foodSlot = Setting.of("Food Slot", 6, 1,9),
            blockSlot = Setting.of("Block Slot", 7, 1, 9),
            keepBlocks = Setting.of("Max Stacks of Blocks", 3,1,9),
            keepThrowables = Setting.of("Max Stacks of Throwables", 3, 1, 9);

    private final Counter actionTimer = new Counter();
    private final Counter startTimer = new Counter();
    public InventoryManager() {
        super("InventoryManager", "Manages and cleans your inventory.", Category.PLAYER);
    }
    @Override
    public void onEnable() {
        actionTimer.reset();
    }

    @Override
    public void onDisable() {
        actionTimer.reset();
    }


    @EventListen
    private void onUpdate(UpdateEvent event) {
        if (inventoryOnly.getValue() && mc.currentScreen instanceof GuiInventory) {
            resetTimers();
        }
        if (TargetProcessor.getInstance().target != null && exitOnEnemy.getValue() && mc.currentScreen instanceof GuiInventory) {
            mc.thePlayer.closeScreen();
        }

        if (mc.currentScreen instanceof GuiInventory && inventoryOnly.getValue()) {
            manageInventory();
        }
    }

    private void manageInventory() {
        List<Integer> throwableItems = new ArrayList<>();
        List<Integer> blockItems = new ArrayList<>();
        int[] bestItems = getBestItems();

        Map<Class<? extends Item>, Consumer<Integer>> itemHandlers = createItemHandlers(throwableItems, blockItems, bestItems);

        for (int i = 0; i < 40; i++) {
            ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
            if (stack == null) continue;

            itemHandlers.getOrDefault(stack.getItem().getClass(), this::handleTrash).accept(i);
        }

        dropExtraStacks(blockItems, keepBlocks.getValue().intValue());
        dropExtraStacks(throwableItems, keepThrowables.getValue().intValue());
        handleArmor(bestItems);
        handleHotbarItems(bestItems);
    }

    private int[] getBestItems() {
        return new int[]{
                InventoryUtil.getBestArmor(0),
                InventoryUtil.getBestArmor(1),
                InventoryUtil.getBestArmor(2),
                InventoryUtil.getBestArmor(3),
                InventoryUtil.getBestSword(false),
                InventoryUtil.getBestPickaxe(),
                InventoryUtil.getBestAxe(),
                InventoryUtil.getBestShovel(),
                InventoryUtil.getBestBlock(),
                InventoryUtil.getBestFood(),
                InventoryUtil.getBestBow()
        };
    }

    private Map<Class<? extends Item>, Consumer<Integer>> createItemHandlers(List<Integer> throwableItems, List<Integer> blockItems, int[] bestItems) {
        Map<Class<? extends Item>, Consumer<Integer>> itemHandlers = new HashMap<>();
        itemHandlers.put(ItemBlock.class, i -> handleBlockItem(i, blockItems));
        itemHandlers.put(ItemSnowball.class, i -> handleThrowableItem(i, throwableItems));
        itemHandlers.put(ItemEgg.class, i -> handleThrowableItem(i, throwableItems));
        itemHandlers.put(ItemSword.class, i -> handleBestItem(i, bestItems[4]));
        itemHandlers.put(ItemFood.class, i -> handleBestItem(i, bestItems[9]));
        itemHandlers.put(ItemPickaxe.class, i -> handleBestItem(i, bestItems[5]));
        itemHandlers.put(ItemAxe.class, i -> handleBestItem(i, bestItems[6]));
        itemHandlers.put(ItemSpade.class, i -> handleBestItem(i, bestItems[7]));
        itemHandlers.put(ItemBow.class, i -> handleBestItem(i, bestItems[10]));
        return itemHandlers;
    }

    private void handleBlockItem(int i, List<Integer> blockItems) {
        Block block = ((ItemBlock) mc.thePlayer.inventory.getStackInSlot(i).getItem()).getBlock();
        if (!InventoryUtil.canPlaceOnBlock(block)) {
            dropItem(i);
        } else {
            blockItems.add(i);
        }
    }

    private void handleThrowableItem(int i, List<Integer> throwableItems) {
        throwableItems.add(i);
    }

    private void dropExtraStacks(List<Integer> items, int maxStacks) {
        if (items.size() <= maxStacks) {
            return;
        }

        List<Integer> sortedByStackSize = InventoryUtil.getSortedByStackSize(items);
        for (int i = maxStacks; i < sortedByStackSize.size(); i++) {
            dropItem(sortedByStackSize.get(i));
        }
    }

    private void handleBestItem(int i, int bestItem) {
        if (bestItem != -1 && bestItem != i) {
            dropItem(i);
        }
    }

    private void handleTrash(int i) {
        if (InventoryUtil.isTrash(mc.thePlayer.inventory.getStackInSlot(i).getItem())) {
            dropItem(i);
        }
    }

    private void handleArmor(int[] bestItems) {
        for (int i = 0; i < 40; i++) {
            ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
            if (stack == null) continue;
            if (stack.getItem() instanceof ItemArmor) {
                ItemArmor armor = (ItemArmor) stack.getItem();
                switch (armor.armorType) {
                    case 0: if (i != bestItems[0]) dropItem(i); break;
                    case 1: if (i != bestItems[1]) dropItem(i); break;
                    case 2: if (i != bestItems[2]) dropItem(i); break;
                    case 3: if (i != bestItems[3]) dropItem(i); break;
                }
            }
        }

        equipArmor(bestItems);
    }

    private void equipArmor(int[] bestItems) {
        if (bestItems[0] != -1 && bestItems[0] != 39) shiftClickItem(bestItems[0]);
        if (bestItems[1] != -1 && bestItems[1] != 38) shiftClickItem(bestItems[1]);
        if (bestItems[2] != -1 && bestItems[2] != 37) shiftClickItem(bestItems[2]);
        if (bestItems[3] != -1 && bestItems[3] != 36) shiftClickItem(bestItems[3]);
    }

    private void handleHotbarItems(int[] bestItems) {
        Map<BooleanSetting, Integer> hotbarItems = new HashMap<>();
        hotbarItems.put(keepSword, bestItems[4]);
        hotbarItems.put(keepPickaxe, bestItems[5]);
        hotbarItems.put(keepAxe, bestItems[6]);
        hotbarItems.put(keepShovel, bestItems[7]);
        hotbarItems.put(keepFood, bestItems[9]);
        hotbarItems.put(keepBow, bestItems[10]);

        Map<BooleanSetting, NumberSetting> hotbarSlots = new HashMap<>();
        hotbarSlots.put(keepSword, swordSlot);
        hotbarSlots.put(keepPickaxe, pickaxeSlot);
        hotbarSlots.put(keepAxe, axeSlot);
        hotbarSlots.put(keepShovel, shovelSlot);
        hotbarSlots.put(keepFood, foodSlot);
        hotbarSlots.put(keepBow, bowSlot);

        hotbarItems.forEach((setting, bestItem) -> {
            if (setting.getValue() && bestItem != -1 && bestItem != hotbarSlots.get(setting).getValue().intValue() - 1) {
                swapItem(bestItem, hotbarSlots.get(setting).getValue().intValue() - 1);
            }
        });

        if (bestItems[8] != -1 && bestItems[8] != blockSlot.getValue().intValue() - 1) {
            swapItem(bestItems[8], blockSlot.getValue().intValue() - 1);
        }
    }

    private void dropItem(int slot) {
        if (!actionTimer.hasElapsed((long) MathUtil.range(minDelay.getValue().intValue(), maxDelay.getValue().intValue())) || !startTimer.hasElapsed(startDelay.getValue().longValue())) {
            return;
        }
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, correctSlot(slot), 1, 4, mc.thePlayer);
        actionTimer.reset();
    }

    private void swapItem(int slot, int targetSlot) {
        if (!actionTimer.hasElapsed((long) MathUtil.range(minDelay.getValue().intValue(), maxDelay.getValue().intValue())) || !startTimer.hasElapsed(startDelay.getValue().longValue())) {
            return;
        }
        mc.playerController.windowClick(0, correctSlot(slot), targetSlot, 2, mc.thePlayer);
        actionTimer.reset();
    }

    private void shiftClickItem(int item) {
        if (!actionTimer.hasElapsed((long) MathUtil.range(minDelay.getValue().intValue(), maxDelay.getValue().intValue())) || !startTimer.hasElapsed(startDelay.getValue().longValue())) {
            return;
        }
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, correctSlot(item), 0, 1, mc.thePlayer);
        actionTimer.reset();
    }

    private int correctSlot(int slot) {
        if (slot >= 36) {
            return 8 - (slot - 36);
        }
        if (slot < 9) {
            return slot + 36;
        }
        return slot;
    }

    private void resetTimers() {
        actionTimer.reset();
        startTimer.reset();
    }
}
