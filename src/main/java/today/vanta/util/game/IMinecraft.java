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
        float gcd = RotationUtil.getMouseGCD();
        float yawDiff = MathHelper.wrapAngleTo180_float(rotations.yaw - rotations.lastYaw);
        float pitchDiff = rotations.pitch - rotations.lastPitch;

        yawDiff = Math.round(yawDiff / gcd) * gcd;
        pitchDiff = Math.round(pitchDiff / gcd) * gcd;

        float targetYaw = rotations.lastYaw + yawDiff;
        float targetPitch = MathHelper.clamp_float(rotations.lastPitch + pitchDiff, -90, 90);

        event.yaw = targetYaw;
        event.pitch = targetPitch;
        mc.thePlayer.rotationYawHead = event.yaw;
        mc.thePlayer.renderYawOffset = event.yaw;
        mc.thePlayer.rotationPitchHead = event.pitch;
    }

    default void sendPacket(Packet<?> packet) {
        if (mc.thePlayer != null) {
            mc.thePlayer.sendQueue.addToSendQueue(packet);
        }
    }

    default void send(String message, Object... args) {
        ChatUtil.send(message, args);
    }
}