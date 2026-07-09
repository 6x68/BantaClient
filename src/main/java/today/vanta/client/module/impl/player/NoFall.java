package today.vanta.client.module.impl.player;

import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import today.vanta.client.event.impl.game.world.UpdateEvent;
import today.vanta.client.module.Category;
import today.vanta.client.module.Module;
import today.vanta.util.game.events.EventListen;
import today.vanta.util.game.player.ChatUtil;
import today.vanta.util.game.player.PlayerUtil;

public class NoFall extends Module {
    public NoFall() {
        super("NoFall", "No fall damage.", Category.PLAYER);
    }

    @EventListen
    private void onUpdate(UpdateEvent event) {
        ChatUtil.send(ChatUtil.Prefix.INFO,String.valueOf(mc.thePlayer.fallDistance));
        if (mc.thePlayer.onGround) {
            mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,mc.thePlayer.posY + 0.035,mc.thePlayer.posZ,false));

        }
    }
}