package today.vanta.client.module.impl.misc;

import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import today.vanta.client.event.impl.game.network.SendPacketEvent;
import today.vanta.client.event.impl.game.world.UpdateEvent;
import today.vanta.client.module.Category;
import today.vanta.client.module.Module;
import today.vanta.client.setting.Setting;
import today.vanta.client.setting.impl.MultiStringSetting;
import today.vanta.client.setting.impl.NumberSetting;
import today.vanta.util.game.events.EventListen;

import java.util.*;

public class Disabler extends Module {
    private final MultiStringSetting disable = Setting.of("Disable", new String[]{"Miniblox"}, new String[]{"Miniblox", "Grim"});
    private final NumberSetting holdLength = Setting.of("Hold length", 50, 0, 1000, "ms");

    public Disabler() {
        super("Disabler", "Disable anticheats, or at least parts of them.", Category.MISC);
    }

    private final Queue<Packet<?>> packetQueue = new LinkedList<>();
    private long lastSendTime = 0;
    private boolean isProcessing = false;

    @EventListen
    private void onUpdate(UpdateEvent event) {
        if (disable.isEnabled("Miniblox")) {
            sendPacket(new C0CPacketInput(
                    mc.thePlayer.moveStrafing,
                    mc.thePlayer.moveForward,
                    mc.thePlayer.movementInput.jump,
                    mc.thePlayer.movementInput.sneak
            ));
        }

        if (disable.isEnabled("Grim")) {
            //needs some kind of interact packet to work properly
            //sendPacket(...)

            long now = System.currentTimeMillis();

            if (!packetQueue.isEmpty() && now - lastSendTime >= holdLength.getValue().longValue()) {
                isProcessing = true;
                Packet<?> packet = packetQueue.poll();
                sendPacket(packet);
                lastSendTime = now;
                isProcessing = false;
            }
        }
    }

    @EventListen
    private void onPacket(SendPacketEvent event) {
        if (isProcessing || mc.thePlayer == null) return;

        if (disable.isEnabled("Grim")) {
            if (event.packet instanceof C03PacketPlayer) {
                packetQueue.add(event.packet);
                event.cancelled = true;
            }
        }
    }

    @Override
    public void onDisable() {
        if (mc.thePlayer == null) return;
        while (!packetQueue.isEmpty()) {
            sendPacket(packetQueue.poll());
        }
    }

    @Override
    public String getSuffix() {
        return "" + (disable.getValue().length == 1 ? Arrays.toString(disable.getValue()).replaceAll("[\\[\\](){}]", "") : disable.getValue().length);
    }
}