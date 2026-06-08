package today.vanta.client.module.impl.movement;

import net.minecraft.item.*;
import today.vanta.Vanta;
import today.vanta.client.event.impl.game.player.SlowdownEvent;
import today.vanta.client.module.Category;
import today.vanta.client.module.Module;
import today.vanta.client.setting.impl.BooleanSetting;
import today.vanta.client.setting.impl.MultiStringSetting;
import today.vanta.client.setting.impl.NumberSetting;
import today.vanta.util.game.events.EventListen;
import today.vanta.util.game.player.MovementUtil;

public class NoSlowdown extends Module {
    private final MultiStringSetting items = MultiStringSetting.builder()
            .name("Items")
            .value("Swords")
            .values("Swords", "Bows", "Consumables")
            .build();

    private final NumberSetting forwardMultiplier = NumberSetting.builder()
            .name("Forward multiplier")
            .value(1f)
            .min(0)
            .max(1)
            .places(1)
            .build();

    private final NumberSetting strafeMultiplier = NumberSetting.builder()
            .name("Strafe multiplier")
            .value(1f)
            .min(0)
            .max(1)
            .places(1)
            .build();

    private final BooleanSetting
            shouldSprint = BooleanSetting.builder()
            .name("Should Sprint")
            .value(false)
            .build();


    public NoSlowdown() {
        super("NoSlowdown", "Removes the slowdown effect when using an item.", Category.MOVEMENT);
        displayNames = new String[]{"NoSlowdown", "NoSlow", "NoSlowDown"};
    }

    @EventListen
    private void onSlow(SlowdownEvent event) {
        ItemStack currentItem = mc.thePlayer.getCurrentEquippedItem();

        if (currentItem == null || !mc.thePlayer.isUsingItem() || !MovementUtil.isMoving()) {
            return;
        }

        if (shouldSprint.getValue() == false) {

            mc.gameSettings.keyBindSprint.pressed = false;
        }


        if ((items.isEnabled("Swords") && currentItem.getItem() instanceof ItemSword) ||
                (items.isEnabled("Consumables") && (currentItem.getItem() instanceof ItemFood || currentItem.getItem() instanceof ItemPotion)) ||
                (items.isEnabled("Bows") && currentItem.getItem() instanceof ItemBow)) {
            event.forward = forwardMultiplier.getValue().floatValue();
            event.strafe = strafeMultiplier.getValue().floatValue();
        }
    }
    @Override
    public void onDisable() {
        if (Vanta.instance.moduleStorage.getT(Sprint.class).isEnabled()) {
            mc.gameSettings.keyBindSprint.pressed = true;
        }
    }
}