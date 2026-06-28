package today.vanta.client.module.impl.player.temporary;

import net.minecraft.block.*;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.entity.EntityList;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.Slot;
import net.minecraft.item.*;
import net.minecraft.util.StatCollector;
import org.apache.commons.lang3.time.StopWatch;
import today.vanta.client.event.impl.game.world.UpdateEvent;
import today.vanta.client.module.Category;
import today.vanta.client.module.Module;
import today.vanta.client.setting.Setting;
import today.vanta.client.setting.impl.BooleanSetting;
import today.vanta.client.setting.impl.NumberSetting;
import today.vanta.util.game.events.EventListen;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class oldContainerStealer extends Module {
    private final NumberSetting delayMin = Setting.of("Delay Min", 100, 0, 500);
    private final NumberSetting delayMax = Setting.of("Delay Max", 150, 0, 500);
    private final BooleanSetting ignoreTrash = Setting.of("Ignore Trash", true);
    private final BooleanSetting guiDetection = Setting.of("Gui Detection", true);

    private final StopWatch stopwatch = new StopWatch();
    private long nextClick;
    private int lastClick;
    private int lastSteal;
    private int open;
    private boolean finished;
    private long startTime;
    private static boolean userInterface;
    private static final List<Item> WHITELISTED_ITEMS = Arrays.asList(Items.fishing_rod, Items.water_bucket, Items.bucket, Items.arrow, Items.bow, Items.snowball, Items.egg, Items.ender_pearl);

    public oldContainerStealer() {
        super("ContainerStealer", "Steals from containers.", Category.PLAYER);
    }
    public void reset() {
        this.startTime = System.currentTimeMillis();
    }
    public boolean finished(long milliseconds) {
        return System.currentTimeMillis() - startTime >= milliseconds;
    }
    public long getElapsedTime() {
        return System.currentTimeMillis() - startTime;
    }

    private static final HashMap<Integer, Integer> GOOD_POTIONS = new HashMap<Integer, Integer>() {{
        put(6, 1); // Instant Health
        put(10, 2); // Regeneration
        put(11, 3); // Resistance
        put(21, 4); // Health Boost
        put(22, 5); // Absorption
        put(23, 6); // Saturation
        put(5, 7); // Strength
        put(1, 8); // Speed
        put(12, 9); // Fire Resistance
        put(14, 10); // Invisibility
        put(3, 11); // Haste
        put(13, 12); // Water Breathing
    }};

    public static boolean goodPotion(final int id) {
        return GOOD_POTIONS.containsKey(id);
    }

    public static double random(double min, double max) {
        if (min == 0 && max == 0)
            return 0;

        return ThreadLocalRandom.current().nextDouble(min, max + 1);
    }
    public static boolean useful(final ItemStack stack) {
        final Item item = stack.getItem();

        if (item instanceof ItemPotion) {
            final ItemPotion potion = (ItemPotion) item;
            return ItemPotion.isSplash(stack.getMetadata()) && goodPotion(potion.getEffects(stack).get(0).getPotionID());
        }

        if (item instanceof ItemBlock) {
            final Block block = ((ItemBlock) item).getBlock();
            if (block instanceof BlockGlass || block instanceof BlockStainedGlass || (block.isFullBlock() && !(block instanceof ITileEntityProvider || block instanceof BlockContainer || block instanceof BlockTNT || block instanceof BlockSlime || block instanceof BlockFalling))) {
                return true;
            }
        }

        return item instanceof ItemSword ||
                item instanceof ItemTool ||
                item instanceof ItemArmor ||
                item instanceof ItemFood ||
                WHITELISTED_ITEMS.contains(item);
    }
    
    private String expectedName(final ItemStack stack) {
        String s = ("" + StatCollector.translateToLocal(stack.getUnlocalizedName() + ".name")).trim();
        final String s1 = EntityList.getStringFromID(stack.getMetadata());

        if (s1 != null) {
            s = s + " " + StatCollector.translateToLocal("entity." + s1 + ".name");
        }

        return s;
    }

    public static boolean inGUI() {
        return userInterface;
    }

    @EventListen
    public final void onUpdate(UpdateEvent event) {
        
        if (mc.currentScreen instanceof GuiChest) {
            final Container container = mc.thePlayer.openContainer;

            int confidence = 0, totalSlots = 0, amount = 0;

            for (final Slot slot : container.inventorySlots) {
                if (slot.getHasStack() && amount++ <= 26 /* Amount of slots in a chest */) {
                    final ItemStack itemStack = slot.getStack();

                    if (itemStack == null) {
                        continue;
                    }

                    final String name = itemStack.getDisplayName();
                    final String expectedName = expectedName(itemStack);
                    final String strippedName = name.toLowerCase().replace(" ", "");
                    final String strippedExpectedName = expectedName.toLowerCase().replace(" ", "");

                    if (strippedName.contains(strippedExpectedName)) {
                        confidence -= 0.1;
                    } else {
                        confidence++;
                    }

                    totalSlots++;
                }
            }

            userInterface = (float) confidence / (float) totalSlots > 0.5f;
        }
        
        if (mc.currentScreen instanceof GuiChest) {
            open++;
            this.finished = false;
            final ContainerChest container = (ContainerChest) mc.thePlayer.openContainer;

            if ((guiDetection.getValue().booleanValue() && inGUI()) || !finished(this.nextClick)) {
                return;
            }

            this.lastSteal++;

            for (int i = 0; i < container.inventorySlots.size(); i++) {
                final ItemStack stack = container.getLowerChestInventory().getStackInSlot(i);

                if (stack == null || this.lastSteal <= 1) {
                    continue;
                }

                if (this.ignoreTrash.getValue() && !useful(stack)) {
                    continue;
                }

                this.nextClick = Math.round(random((double) this.delayMin.getValue().intValue(), (double) this.delayMax.getValue().intValue()));
                mc.playerController.windowClick(container.windowId, i, 0, 1, mc.thePlayer);
                this.stopwatch.reset();
                this.lastClick = 0;
                if (this.nextClick > 0) return;
            }

            this.lastClick++;

            if (this.lastClick > 1 && open > 2 + (2 * Math.random())) {
                mc.thePlayer.closeScreen();
                this.finished = true;
            }
        } else {
            this.lastClick = 0;
            this.open = 0;
            this.lastSteal = 0;
        }
    };

    public boolean isFinished() {
        return this.finished;
    }
    
    
}
