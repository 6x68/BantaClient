package today.vanta.util.game;

import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.util.MathHelper;
import today.vanta.client.event.impl.game.player.MotionEvent;
import today.vanta.util.game.player.ChatUtil;
import today.vanta.util.game.player.RotationUtil;
import today.vanta.util.game.player.constructors.Rotation;

public interface IMinecraft {
    Minecraft mc = Minecraft.getMinecraft();

    default void setRotations(Rotation rotations, MotionEvent event) {
        Rotation lastRotations = new Rotation(rotations.lastYaw, rotations.lastPitch);

        float yawDiff = MathHelper.wrapAngleTo180_float(rotations.yaw - lastRotations.yaw);
        float wrappedYaw = rotations.lastYaw + yawDiff;
        Rotation adjustedRotations = new Rotation(wrappedYaw, rotations.pitch);

        Rotation gcd = RotationUtil.gcd(adjustedRotations, lastRotations);

        mc.thePlayer.prevRenderYawOffset = gcd.yaw;

        event.yaw = gcd.yaw;
        event.pitch = gcd.pitch;

        mc.thePlayer.rotationYawHead = gcd.yaw;
        mc.thePlayer.renderYawOffset = gcd.yaw;
        mc.thePlayer.rotationPitchHead = gcd.pitch;
    }

    default void sendPacket(Packet<?> packet) {
        if (mc.thePlayer != null) {
            mc.thePlayer.sendQueue.addToSendQueue(packet);
        }
    }

    default void send(String message, Object... args) {
        ChatUtil.info(message, args);
    }
}