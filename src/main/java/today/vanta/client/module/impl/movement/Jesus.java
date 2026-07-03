package today.vanta.client.module.impl.movement;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import today.vanta.client.event.impl.game.player.MoveEvent;
import today.vanta.client.event.impl.game.world.BlockCollisionEvent;
import today.vanta.client.module.Category;
import today.vanta.client.module.Module;
import today.vanta.client.setting.Setting;
import today.vanta.client.setting.impl.StringSetting;
import today.vanta.util.game.events.EventListen;
import today.vanta.util.game.player.MovementUtil;

public class Jesus extends Module {
    private final StringSetting mode = Setting.of("Mode", "Basic", "Vulcan", "Basic", "Collision");

    private boolean canslow;

    public Jesus() {
        super("Jesus", "Lets you walk on water like jesus supposedly did.", Category.MOVEMENT);
        displayNames = new String[]{"Jesus", "LiquidWalk", "WaterWalk", "WaterHack"};
    }

    @EventListen
    private void onBB(BlockCollisionEvent event) {
        if (!mode.getValue().equals("Collision")) return;
        if (mc.thePlayer == null) return;

        if (event.block != Blocks.water) return;
        if (event.pos.getY() == mc.thePlayer.getPosition().getY() - 1) {
            event.axisalignedbb = getCollisionBoundingBox(event.pos, event.block);
        }
    }

    @EventListen
    private void onMove(MoveEvent event) {
        switch (mode.getValue()) {
            case "Basic":
                if (mc.thePlayer.isInWater()) {
                    if (!mc.gameSettings.keyBindJump.isKeyDown() || !mc.gameSettings.keyBindSneak.isKeyDown()) {
                        mc.thePlayer.motionY = 0;
                    }

                    if (mc.gameSettings.keyBindJump.isKeyDown()) {
                        mc.thePlayer.motionY = 0.2f;
                    } else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                        mc.thePlayer.motionY = 0.2f;
                    } else {
                        mc.thePlayer.motionY = 0;
                    }
                }
                break;

            case "Vulcan":
                if (mc.thePlayer.isInWater() || mc.thePlayer.isInLava() && !mc.thePlayer.onGround) {
//                mc.thePlayer.motionY = 0.5f;

                    if (mc.gameSettings.keyBindJump.isKeyDown()) {
                        mc.thePlayer.motionY = 0.15f;
                    }

                    if (!mc.gameSettings.keyBindJump.isKeyDown() || !mc.gameSettings.keyBindSneak.isKeyDown()) {
                        mc.thePlayer.motionY = -0.001f;
                        event.setSpeed(0.2f);
                        canslow = true;
                    } else {
                        if (canslow) {
                            MovementUtil.stop();
                            event.setSpeed(0.01f);
                            canslow = false;
                        }
                    }
                }
                break;
        }
    }

    @Override
    public String getSuffix() {
        return mode.getValue();
    }

    private AxisAlignedBB getCollisionBoundingBox(BlockPos pos, Block block) {
        return new AxisAlignedBB((double) pos.getX() + block.minX, (double) pos.getY() + block.minY, (double) pos.getZ() + block.minZ, (double) pos.getX() + block.maxX, (double) pos.getY() + block.maxY, (double) pos.getZ() + block.maxZ);
    }
}