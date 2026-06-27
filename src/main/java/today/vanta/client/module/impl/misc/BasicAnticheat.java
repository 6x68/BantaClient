package today.vanta.client.module.impl.misc;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import today.vanta.client.event.impl.game.world.UpdateEvent;
import today.vanta.client.module.Category;
import today.vanta.client.module.Module;
import today.vanta.client.processor.impl.TargetProcessor;
import today.vanta.util.game.events.EventListen;
import today.vanta.util.game.player.ChatUtil;
import today.vanta.util.game.world.EntityUtil;

import java.util.ArrayList;
import java.util.List;

public class BasicAnticheat extends Module {
    int tick = 0;
    List<EntityLivingBase> list = new ArrayList<>();
    EntityLivingBase entity;
    String lastCheck;
    String lastName;
    double prevpos;

    public BasicAnticheat() {
        super("Anticheat", "Checks for illegal behaviour on players.", Category.MISC);
    }

    @EventListen
    public void onUpdate(UpdateEvent event) {
        list.clear();

        mc.theWorld.getLoadedEntityList().stream()
                .filter(entity -> entity instanceof EntityPlayer && entity != mc.thePlayer && !entity.isDead)
                .map(entity -> (EntityPlayer) entity)
                .forEachOrdered(list::add);

        entity = list.isEmpty() ? null : list.get(0);
        if (list.isEmpty()) {return;}
        for (int i = 0; i < list.size(); i++) {
            if (entity instanceof EntityPlayer) {
                if (((EntityPlayer) entity).isBlocking() || entity.isSneaking()) {
                    if (entity.isSprinting() && lastCheck != "a" && lastName != entity.getName()) {
                        ChatUtil.send(ChatUtil.Prefix.WARNING, entity.getName() + " is Sprinting wrongly");
                        lastCheck = "a";
                        lastName = entity.getName();
                    }
                }
                if (entity.isInWater() || entity.isInLava()) {
                    prevpos = entity.posY;
                }
            }
        }
    }

    @Override
    public void onEnable() {
        tick = 0;
    }
}
