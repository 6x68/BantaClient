package today.vanta.client.module.impl.player;

import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import today.vanta.client.event.impl.game.network.SendPacketEvent;
import today.vanta.client.event.impl.game.world.UpdateEvent;
import today.vanta.client.module.Category;
import today.vanta.client.module.Module;
import today.vanta.client.setting.Setting;
import today.vanta.client.setting.impl.StringSetting;
import today.vanta.util.game.events.EventListen;
import today.vanta.util.game.player.ChatUtil;
import today.vanta.util.game.player.MovementUtil;
import today.vanta.util.game.player.PlayerUtil;

public class AntiVoid extends Module {
    private StringSetting mode = Setting.of("Mode", "Flag", "Blink", "Flag", "Setback");
    private StringSetting setbackmode = Setting.of("Position Set Mode", "Ground", "Previous", "Ground");
    int tick;
    double prevPosZ,prevPosX,prevPosY;
    boolean wasonground;
    public AntiVoid() {
        super("AntiVoid", "teleport yes.", Category.PLAYER);
    }

    @EventListen
    public void onUpdate(UpdateEvent event) {
        switch (setbackmode.getValue()) {
            case "Previous":
                if (!PlayerUtil.isOverVoid()) {
                    prevPosY = mc.thePlayer.posY;
                    prevPosX = mc.thePlayer.posX;
                    prevPosZ = mc.thePlayer.posZ;
                }
                break;
            case "Ground":
                if (mc.thePlayer.onGround && !PlayerUtil.isOverVoid()) {
                    prevPosY = mc.thePlayer.posY;
                    prevPosX = mc.thePlayer.posX;
                    prevPosZ = mc.thePlayer.posZ;
                }
                break;
        }

        if (MovementUtil.movementModuleEnabled()) {return;}
        switch (mode.getValue()) {
            case "Flag":
                if (PlayerUtil.isOverVoid() && !mc.thePlayer.onGround) {
                    tick++;
                    if (tick > 11) {
                        mc.thePlayer.motionY -= 0.4f;
                    }
                } else {
                    tick = 0;
                }
                break;
            case "Blink":
//                if (PlayerUtil.isOverVoid() && !mc.thePlayer.onGround) {
//                    tick++;
//                    if (tick > 11) {
//                        mc.thePlayer.setPosition(prevPosX,prevPosY,prevPosZ);
//                        ChatUtil.send(ChatUtil.Prefix.INFO, "set");
//                        tick = 0;
//                    }
//                } else {
//                    tick = 0;
//                }
                break;
            case "Setback":
                if (PlayerUtil.isOverVoid()) {
                    mc.thePlayer.setPosition(prevPosX,prevPosY,prevPosZ);
                }
                break;
        }
    }

    @EventListen
    public void onPacket(SendPacketEvent event) {
        if (mode.getValue().equals("Blink") && PlayerUtil.isOverVoid()) {
            event.cancelled = true;
            mc.thePlayer.setPosition(prevPosX,prevPosY,prevPosZ);
            ChatUtil.send(ChatUtil.Prefix.INFO, "Cancel");
        }
    }
}
