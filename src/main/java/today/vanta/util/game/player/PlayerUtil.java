package today.vanta.util.game.player;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.optifine.BlockPosM;
import today.vanta.util.game.IMinecraft;

public class PlayerUtil implements IMinecraft {

    public static boolean isOverVoid() {
        BlockPosM position = new BlockPosM(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
        for (int i = 0; i < 256; i++) {
            IBlockState state = mc.theWorld.getBlockState(new BlockPos(position.getX(), i, position.getZ()));
            if (state.getBlock() != Blocks.air)
                return false;
        }
        return true;
    }

    public static boolean isSlabUnderneath() {
        BlockPosM position = new BlockPosM(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
        for (int i = 0; i < 2; i++) {
            IBlockState state = mc.theWorld.getBlockState(new BlockPos(position.getX(), position.getY() + i, position.getZ()));
            if (state.getBlock() != Blocks.wooden_slab || state.getBlock() != Blocks.stone_slab || state.getBlock() != Blocks.stone_slab)
                return false;
        }
        return true;
    }

    // from Seline :sob:
    public static boolean checkIllegal(EntityLivingBase entity) {
        float length = entity.getName().length();
        if (length >= 17) {
            return true;
        }

        NetworkPlayerInfo info = mc.getNetHandler()
                .getPlayerInfo(entity.getUniqueID());
        if (info == null) return true;
        if (info.getResponseTime() == 0) return true;
        if (entity.getName().startsWith("§")) return true;

        return false;
    }
}
