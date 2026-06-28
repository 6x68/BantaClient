package today.vanta.client.module.impl.player.temporary;

import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.item.*;
import net.minecraft.util.DamageSource;
import today.vanta.client.event.impl.game.world.UpdateEvent;
import today.vanta.client.module.Category;
import today.vanta.client.module.Module;
import today.vanta.client.setting.Setting;
import today.vanta.client.setting.impl.BooleanSetting;
import today.vanta.client.setting.impl.NumberSetting;
import today.vanta.util.game.events.EventListen;
import today.vanta.util.game.player.InventoryUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class oldInventoryManager extends Module {
    private final NumberSetting delayMin = Setting.of("Delay Min", 100, 0, 500);
    private final NumberSetting delayMax = Setting.of("Delay Max", 200, 0, 500);

    private final BooleanSetting legit = Setting.of("Legit", false);
    private final BooleanSetting dropCustomItems = Setting.of("Drop Custom Items", false);
    private final BooleanSetting useCustomItems = Setting.of("Use Custom Items", false);
    private final BooleanSetting prioritizeSplashPotions = Setting.of("Prioritize Splash Potions", false);
    private final NumberSetting blockLimit = Setting.of("Block Limit", 512, 0, 512);
    private final NumberSetting arrowLimit = Setting.of("Arrow Limit", 128, 0, 512);
    private final NumberSetting bucketLimit = Setting.of("Bucket Limit", 1, 0, 4);
    private final NumberSetting snowballEggLimit = Setting.of("Snowball/Egg Limit", 16, 0, 64);
    private final NumberSetting enderPearlLimit = Setting.of("Ender Pearl Limit", 16, 0, 64);

    private final NumberSetting swordSlot = Setting.of("Sword Slot", 1, 0, 9);
    private final NumberSetting pickaxeSlot = Setting.of("Pickaxe Slot", 2, 0, 9);
    private final NumberSetting axeSlot = Setting.of("Axe Slot", 3, 0, 9);
    private final NumberSetting shovelSlot = Setting.of("Shovel Slot", 4, 0, 9);
    private final NumberSetting blockSlot = Setting.of("Block Slot", 5, 0, 9);
    private final NumberSetting potionSlot = Setting.of("Potion Slot", 6, 0, 9);
    private final NumberSetting bowSlot = Setting.of("Bow Slot", 7, 0, 9);
    private final NumberSetting rodSlot = Setting.of("Rod Slot", 8, 0, 9);
    private final NumberSetting foodSlot = Setting.of("Food Slot", 9, 0, 9);

    private final int INVENTORY_ROWS = 4, INVENTORY_COLUMNS = 9, ARMOR_SLOTS = 4;
    private final int INVENTORY_SLOTS = (INVENTORY_ROWS * INVENTORY_COLUMNS) + ARMOR_SLOTS;

    private long nextClick = 0;
    private long lastAction = 0;
    private boolean moved = false;
    public oldInventoryManager() {
        super("InventoryManager", "Manages and cleans your inventory.", Category.PLAYER);
    }

    public static double random(double min, double max) {
        if (min == 0 && max == 0)
            return 0;

        return ThreadLocalRandom.current().nextDouble(min, max + 1);
    }

    @EventListen
    public final void onUpdate(UpdateEvent event) {
        // Legit mode check - only work when inventory is open
        if (legit.getValue() && !(mc.currentScreen instanceof GuiInventory)) {
            return;
        }

        // Delay check
        if (System.currentTimeMillis() - lastAction < nextClick) {
            return;
        }

        Container inventory = mc.thePlayer.inventoryContainer;

        // Only work when inventory is accessible
        if (!(mc.currentScreen instanceof GuiInventory) || mc.thePlayer.openContainer != inventory) {
            return;
        }

        moved = false;

        // Find best items
        int bestSword = -1;
        int bestBow = -1;
        int bestPickaxe = -1;
        int bestAxe = -1;
        int bestShovel = -1;
        int bestRod = -1;

        int[] bestArmors = new int[4];
        Arrays.fill(bestArmors, -1);

        List<Integer> trash = new ArrayList<>();
        List<ItemStackWithNumber> blockStacks = new ArrayList<>();
        List<ItemStackWithNumber> potionStacks = new ArrayList<>();
        List<ItemStackWithNumber> foodStacks = new ArrayList<>();

        // Track item counts for limits
        int totalBlocks = 0, totalArrows = 0, totalBuckets = 0;
        int totalSnowballsEggs = 0, totalEnderPearls = 0;
        List<Integer> blockSlots = new ArrayList<>(), arrowSlots = new ArrayList<>();
        List<Integer> bucketSlots = new ArrayList<>(), snowballEggSlots = new ArrayList<>();
        List<Integer> enderPearlSlots = new ArrayList<>();

        // Scan inventory
        for (int i = 0; i < INVENTORY_SLOTS; i++) {
            ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);

            if (stack == null || stack.getItem() == null) {
                continue;
            }

            Item item = stack.getItem();

            // Count items for limits
            if (item == Items.arrow) {
                totalArrows += stack.stackSize;
                arrowSlots.add(i);
            } else if (item == Items.bucket || item == Items.water_bucket) {
                totalBuckets += stack.stackSize;
                bucketSlots.add(i);
            } else if (item == Items.snowball || item == Items.egg) {
                totalSnowballsEggs += stack.stackSize;
                snowballEggSlots.add(i);
            } else if (item == Items.ender_pearl) {
                totalEnderPearls += stack.stackSize;
                enderPearlSlots.add(i);
            }

            // Find best armor
            if (item instanceof ItemArmor) {
                ItemArmor armor = (ItemArmor) item;
                // Minecraft armor slots: 36=boots, 37=leggings, 38=chestplate, 39=helmet
                // armorType: 0=helmet, 1=chestplate, 2=leggings, 3=boots
                // So we need: 39 - armorType
                int armorSlot = 39 - armor.armorType; // The actual equipped slot for this armor type

                System.out.println("Found armor: " + stack.getDisplayName() + " at slot " + i + " (armor type " + armor.armorType + ", target slot " + armorSlot + ")");

                // Skip if this is already equipped in the correct slot
                if (i == armorSlot) {
                    System.out.println("  -> This armor is already equipped");
                    bestArmors[armor.armorType] = i;
                    continue;
                }

                int reduction = armorReduction(stack);
                System.out.println("  -> Reduction: " + reduction);

                if (bestArmors[armor.armorType] == -1) {
                    System.out.println("  -> Setting as first best armor for type " + armor.armorType);
                    bestArmors[armor.armorType] = i;
                } else {
                    ItemStack currentBest = mc.thePlayer.inventory.getStackInSlot(bestArmors[armor.armorType]);
                    int currentBestReduction = armorReduction(currentBest);

                    System.out.println("  -> Current best: " + currentBest.getDisplayName() + " at slot " + bestArmors[armor.armorType] + " with reduction " + currentBestReduction);

                    // Only update if this armor is strictly better
                    if (reduction > currentBestReduction) {
                        System.out.println("  -> NEW BEST - better reduction");
                        bestArmors[armor.armorType] = i;
                    } else if (reduction == currentBestReduction) {
                        // If same protection, prefer better durability
                        int thisDurability = stack.getMaxDamage() - stack.getItemDamage();
                        int bestDurability = currentBest.getMaxDamage() - currentBest.getItemDamage();

                        System.out.println("  -> Same reduction. This durability: " + thisDurability + ", best durability: " + bestDurability);

                        if (thisDurability > bestDurability) {
                            System.out.println("  -> NEW BEST - better durability");
                            bestArmors[armor.armorType] = i;
                        } else {
                            System.out.println("  -> Keeping current best");
                        }
                    } else {
                        System.out.println("  -> Keeping current best - worse reduction");
                    }
                }
            }

            // Find best weapons and tools
            if (item instanceof ItemSword) {
                if (bestSword == -1 || damage(stack) > damage(mc.thePlayer.inventory.getStackInSlot(bestSword))) {
                    if (bestSword != -1) trash.add(bestSword);
                    bestSword = i;
                } else {
                    trash.add(i);
                }
            } else if (item instanceof ItemBow) {
                if (bestBow == -1 || power(stack) > power(mc.thePlayer.inventory.getStackInSlot(bestBow))) {
                    if (bestBow != -1) trash.add(bestBow);
                    bestBow = i;
                } else {
                    trash.add(i);
                }
            } else if (item instanceof ItemPickaxe) {
                if (bestPickaxe == -1 || mineSpeed(stack) > mineSpeed(mc.thePlayer.inventory.getStackInSlot(bestPickaxe))) {
                    if (bestPickaxe != -1) trash.add(bestPickaxe);
                    bestPickaxe = i;
                } else {
                    trash.add(i);
                }
            } else if (item instanceof ItemAxe) {
                if (bestAxe == -1 || mineSpeed(stack) > mineSpeed(mc.thePlayer.inventory.getStackInSlot(bestAxe))) {
                    if (bestAxe != -1) trash.add(bestAxe);
                    bestAxe = i;
                } else {
                    trash.add(i);
                }
            } else if (item instanceof ItemSpade) {
                if (bestShovel == -1 || mineSpeed(stack) > mineSpeed(mc.thePlayer.inventory.getStackInSlot(bestShovel))) {
                    if (bestShovel != -1) trash.add(bestShovel);
                    bestShovel = i;
                } else {
                    trash.add(i);
                }
            } else if (item instanceof ItemFishingRod) {
                if (bestRod == -1) {
                    bestRod = i;
                } else {
                    trash.add(i);
                }
            }

            // Collect blocks, potions, food
            if (item instanceof ItemBlock) {
                totalBlocks += stack.stackSize;
                blockSlots.add(i);
                blockStacks.add(new ItemStackWithNumber(stack, i));
            }

            if (item instanceof ItemPotion) {
                potionStacks.add(new ItemStackWithNumber(stack, i));
            }

            if (item instanceof ItemFood) {
                foodStacks.add(new ItemStackWithNumber(stack, i));
            }

            // Check if item is useful (you'll need to implement ItemUtil.useful or use your own logic)
            if (!isUsefulItem(stack, bestSword, bestBow, bestPickaxe, bestAxe, bestShovel, bestRod, bestArmors, i)) {
                if (!trash.contains(i)) {
                    trash.add(i);
                }
            }
        }

        // Remove duplicate armor - but don't mark equipped armor as trash if it's the best
        for (int i = 0; i < INVENTORY_SLOTS; i++) {
            ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
            if (stack != null && stack.getItem() instanceof ItemArmor) {
                ItemArmor armor = (ItemArmor) stack.getItem();
                // Minecraft armor slots: 36=boots, 37=leggings, 38=chestplate, 39=helmet
                // armorType: 0=helmet, 1=chestplate, 2=leggings, 3=boots
                int armorSlot = 39 - armor.armorType;

                // Don't trash if this is the best armor or if it's equipped and equal to best
                if (i != bestArmors[armor.armorType] && i != armorSlot) {
                    trash.add(i);
                }
            }
        }

        // Equip best armor
        for (int i = 0; i < bestArmors.length; i++) {
            int bestArmor = bestArmors[i];
            // Minecraft armor slots: 36=boots, 37=leggings, 38=chestplate, 39=helmet
            // armorType: 0=helmet, 1=chestplate, 2=leggings, 3=boots
            // So we need: 39 - armorType
            int armorSlot = 39 - i; // Correct armor slot for this armor type

            System.out.println("=== ARMOR TYPE " + i + " ===");
            System.out.println("Best armor slot: " + bestArmor);
            System.out.println("Target armor slot: " + armorSlot);

            // Skip if best armor is already in the correct armor slot
            if (bestArmor == armorSlot) {
                System.out.println("Skipping - armor already equipped in correct slot");
                continue;
            }

            if (bestArmor != -1) {
                ItemStack currentArmor = mc.thePlayer.inventory.getStackInSlot(armorSlot);
                ItemStack bestArmorStack = mc.thePlayer.inventory.getStackInSlot(bestArmor);

                System.out.println("Current armor: " + (currentArmor != null ? currentArmor.getDisplayName() : "null"));
                System.out.println("Best armor: " + (bestArmorStack != null ? bestArmorStack.getDisplayName() : "null"));

                // Only equip if slot is empty
                if (currentArmor == null) {
                    System.out.println("EQUIPPING - slot is empty");
                    equipItem(bestArmor);
                    return;
                }

                // Only swap if best armor is actually better than equipped armor
                if (bestArmorStack != null && currentArmor.getItem() instanceof ItemArmor) {
                    int bestReduction = armorReduction(bestArmorStack);
                    int currentReduction = armorReduction(currentArmor);

                    System.out.println("Best reduction: " + bestReduction);
                    System.out.println("Current reduction: " + currentReduction);

                    // Only equip if better protection, or same protection but better durability
                    if (bestReduction > currentReduction) {
                        System.out.println("EQUIPPING - better reduction");
                        equipItem(bestArmor);
                        return;
                    } else if (bestReduction == currentReduction) {
                        // If same protection, check durability (lower damage value = more durability)
                        int bestDurability = bestArmorStack.getMaxDamage() - bestArmorStack.getItemDamage();
                        int currentDurability = currentArmor.getMaxDamage() - currentArmor.getItemDamage();

                        System.out.println("Best durability: " + bestDurability);
                        System.out.println("Current durability: " + currentDurability);

                        if (bestDurability > currentDurability) {
                            System.out.println("EQUIPPING - better durability");
                            equipItem(bestArmor);
                            return;
                        } else {
                            System.out.println("NOT EQUIPPING - current is same or better");
                        }
                    } else {
                        System.out.println("NOT EQUIPPING - current has better reduction");
                    }
                }
            }
        }

        // Handle item limits - throw excess items
        if (totalBlocks > blockLimit.getValue().intValue()) {
            handleExcessItems(blockSlots, totalBlocks, blockLimit.getValue().intValue());
            return;
        }

        if (totalArrows > arrowLimit.getValue().intValue()) {
            handleExcessItems(arrowSlots, totalArrows, arrowLimit.getValue().intValue());
            return;
        }

        if (totalBuckets > bucketLimit.getValue().intValue()) {
            handleExcessItems(bucketSlots, totalBuckets, bucketLimit.getValue().intValue());
            return;
        }

        if (totalSnowballsEggs > snowballEggLimit.getValue().intValue()) {
            handleExcessItems(snowballEggSlots, totalSnowballsEggs, snowballEggLimit.getValue().intValue());
            return;
        }

        if (totalEnderPearls > enderPearlLimit.getValue().intValue()) {
            handleExcessItems(enderPearlSlots, totalEnderPearls, enderPearlLimit.getValue().intValue());
            return;
        }

        // Throw trash
        for (int trashSlot : trash) {
            if (shouldProcessSlot(trashSlot)) {
                throwItem(trashSlot);
                return;
            }
        }

        // Move items to hotbar slots
        if (bestSword != -1 && swordSlot.getValue().intValue() != 0) {
            int targetSlot = swordSlot.getValue().intValue() + 35; // Convert to inventory slot
            if (bestSword != targetSlot) {
                moveItem(bestSword, swordSlot.getValue().intValue() - 1);
                return;
            }
        }

        if (bestPickaxe != -1 && pickaxeSlot.getValue().intValue() != 0) {
            int targetSlot = pickaxeSlot.getValue().intValue() + 35;
            if (bestPickaxe != targetSlot) {
                moveItem(bestPickaxe, pickaxeSlot.getValue().intValue() - 1);
                return;
            }
        }

        if (bestAxe != -1 && axeSlot.getValue().intValue() != 0) {
            int targetSlot = axeSlot.getValue().intValue() + 35;
            if (bestAxe != targetSlot) {
                moveItem(bestAxe, axeSlot.getValue().intValue() - 1);
                return;
            }
        }

        if (bestShovel != -1 && shovelSlot.getValue().intValue() != 0) {
            int targetSlot = shovelSlot.getValue().intValue() + 35;
            if (bestShovel != targetSlot) {
                moveItem(bestShovel, shovelSlot.getValue().intValue() - 1);
                return;
            }
        }

        if (bestBow != -1 && bowSlot.getValue().intValue() != 0) {
            int targetSlot = bowSlot.getValue().intValue() + 35;
            if (bestBow != targetSlot) {
                moveItem(bestBow, bowSlot.getValue().intValue() - 1);
                return;
            }
        }

        if (bestRod != -1 && rodSlot.getValue().intValue() != 0) {
            int targetSlot = rodSlot.getValue().intValue() + 35;
            if (bestRod != targetSlot) {
                moveItem(bestRod, rodSlot.getValue().intValue() - 1);
                return;
            }
        }

        // Sort blocks by stack size
        if (blockSlot.getValue().intValue() != 0 && !blockStacks.isEmpty()) {
            blockStacks.sort(Comparator.comparingInt(ItemStackWithNumber::getStackSize).reversed());
            int targetSlot = blockSlot.getValue().intValue() + 35;
            if (!blockStacks.isEmpty() && blockStacks.get(0).getNumber() != targetSlot) {
                moveItem(blockStacks.get(0).getNumber(), blockSlot.getValue().intValue() - 1);
                return;
            }
        }

        // Sort potions
        if (potionSlot.getValue().intValue() != 0 && !potionStacks.isEmpty()) {
            sortPotions(potionStacks);
            int targetSlot = potionSlot.getValue().intValue() + 35;
            if (potionStacks.get(0).getNumber() != targetSlot) {
                moveItem(potionStacks.get(0).getNumber(), potionSlot.getValue().intValue() - 1);
                return;
            }
        }

        // Sort food
        if (foodSlot.getValue().intValue() != 0 && !foodStacks.isEmpty()) {
            foodStacks.sort((a, b) -> {
                ItemFood foodA = (ItemFood) a.getItemStack().getItem();
                ItemFood foodB = (ItemFood) b.getItemStack().getItem();
                return Float.compare(
                        foodB.getSaturationModifier(b.getItemStack()),
                        foodA.getSaturationModifier(a.getItemStack())
                );
            });
            int targetSlot = foodSlot.getValue().intValue() + 35;
            if (foodStacks.get(0).getNumber() != targetSlot) {
                moveItem(foodStacks.get(0).getNumber(), foodSlot.getValue().intValue() - 1);
                return;
            }
        }
    }

    private void handleExcessItems(List<Integer> slots, int total, int limit) {
        int excess = total - limit;
        for (int slot : slots) {
            if (excess <= 0) break;

            ItemStack stack = mc.thePlayer.inventory.getStackInSlot(slot);
            if (stack == null) continue;

            if (shouldProcessSlot(slot)) {
                throwItem(slot);
                excess -= stack.stackSize;
                return;
            }
        }
    }

    private void sortPotions(List<ItemStackWithNumber> potions) {
        potions.sort((a, b) -> {
            if (prioritizeSplashPotions.getValue()) {
                boolean isSplashA = ItemPotion.isSplash(a.getItemStack().getMetadata());
                boolean isSplashB = ItemPotion.isSplash(b.getItemStack().getMetadata());
                if (isSplashA && !isSplashB) return -1;
                if (!isSplashA && isSplashB) return 1;
            }
            return 0;
        });
    }

    private boolean shouldProcessSlot(int slot) {
        // Check custom items settings
        if (!useCustomItems.getValue() && isCustomItem(slot)) {
            return false;
        }
        return true;
    }

    private boolean isCustomItem(int slot) {
        // Implement your custom item detection logic here
        // For now, return false
        return false;
    }

    private boolean isUsefulItem(ItemStack stack, int sword, int bow, int pickaxe,
                                 int axe, int shovel, int rod, int[] armors, int currentSlot) {
        Item item = stack.getItem();

        // Check if it's one of our best items
        if (currentSlot == sword || currentSlot == bow || currentSlot == pickaxe ||
                currentSlot == axe || currentSlot == shovel || currentSlot == rod) {
            return true;
        }

        for (int armor : armors) {
            if (currentSlot == armor) return true;
        }

        // Keep useful items
        return item == Items.arrow || item == Items.water_bucket ||
                item == Items.bucket || item == Items.ender_pearl ||
                item == Items.snowball || item == Items.egg ||
                item instanceof ItemBlock || item instanceof ItemPotion ||
                item instanceof ItemFood;
    }

    private void throwItem(int slot) {
        if (!moved && shouldProcessSlot(slot)) {
            Container inventory = mc.thePlayer.inventoryContainer;
            InventoryUtil.windowClick(mc, inventory.windowId, convertSlot(slot), 1, InventoryUtil.ClickType.THROW);

            lastAction = System.currentTimeMillis();
            nextClick = Math.round(random((double) this.delayMin.getValue().intValue(), (double) this.delayMax.getValue().intValue()));
            moved = true;
        }
    }

    private void moveItem(int slot, int hotbarSlot) {
        if (!moved && shouldProcessSlot(slot)) {
            Container inventory = mc.thePlayer.inventoryContainer;
            InventoryUtil.windowClick(mc, inventory.windowId, convertSlot(slot), hotbarSlot, InventoryUtil.ClickType.SWAP);

            lastAction = System.currentTimeMillis();
            nextClick = Math.round(random((double) this.delayMin.getValue().intValue(), (double) this.delayMax.getValue().intValue()));
            moved = true;
        }
    }



    private void equipItem(int slot) {
        if (!moved && shouldProcessSlot(slot)) {
            Container inventory = mc.thePlayer.inventoryContainer;
            int convertedSlot = convertSlot(slot);

            System.out.println("EQUIP ITEM - Original slot: " + slot + ", Converted slot: " + convertedSlot);

            InventoryUtil.windowClick(mc, inventory.windowId, convertedSlot, 0, InventoryUtil.ClickType.QUICK_MOVE);

            lastAction = System.currentTimeMillis();
            nextClick = Math.round(random((double) this.delayMin.getValue().intValue(), (double) this.delayMax.getValue().intValue()));
            moved = true;
        }
    }

    private int convertSlot(int slot) {
        // Armor slots (36-39) map directly to container slots (5-8)
        if (slot >= 36 && slot <= 39) {
            return 8 - (slot - 36);
        }
        // Hotbar slots (0-8) map to container slots (36-44)
        if (slot < 9) {
            return slot + 36;
        }
        // Inventory slots (9-35) stay the same
        return slot;
    }

    private float damage(ItemStack stack) {
        ItemSword sword = (ItemSword) stack.getItem();
        int level = EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, stack);
        return (float) (sword.getDamageVsEntity() + level * 1.25);
    }

    private float power(ItemStack stack) {
        return EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, stack);
    }

    private float mineSpeed(ItemStack stack) {
        Item item = stack.getItem();
        int level = EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, stack);

        int[] bonuses = {0, 30, 69, 120, 186, 271};
        int bonus = (level >= 1 && level < bonuses.length) ? bonuses[level] : 0;

        if (item instanceof ItemPickaxe) {
            return ((ItemPickaxe) item).getToolMaterial().getEfficiencyOnProperMaterial() + bonus;
        } else if (item instanceof ItemSpade) {
            return ((ItemSpade) item).getToolMaterial().getEfficiencyOnProperMaterial() + bonus;
        } else if (item instanceof ItemAxe) {
            return ((ItemAxe) item).getToolMaterial().getEfficiencyOnProperMaterial() + bonus;
        }

        return 0;
    }

    private int armorReduction(ItemStack stack) {
        ItemArmor armor = (ItemArmor) stack.getItem();
        return armor.damageReduceAmount + EnchantmentHelper.getEnchantmentModifierDamage(
                new ItemStack[]{stack}, DamageSource.generic
        );
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    class ItemStackWithNumber {
        private final ItemStack itemStack;
        private final int number;

        public ItemStackWithNumber(ItemStack itemStack, int number) {
            this.itemStack = itemStack;
            this.number = number;
        }

        public ItemStack getItemStack() {
            return itemStack;
        }

        public int getNumber() {
            return number;
        }

        public int getStackSize() {
            return itemStack.stackSize;
        }
    }


}
