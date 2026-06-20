package today.vanta.util.game.player;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.optifine.BlockPosM;
import today.vanta.util.game.IMinecraft;

public class PlayerUtil implements IMinecraft {

    public static boolean isOverVoid() {
        BlockPosM position = new BlockPosM(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
        for (int i = 0; i < position.getX(); i++) {
            IBlockState state = mc.theWorld.getBlockState(new BlockPos(position.getX(), i, position.getZ()));
            if (state.getBlock() != Blocks.air)
                return false;
        }
        return true;
    }
}
