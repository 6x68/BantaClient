package today.vanta.client.event.impl.game.world;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import today.vanta.client.event.Event;

public class BlockCollisionEvent extends Event {
    public final Block block;
    public final BlockPos pos;
    public final Entity collidingEntity;
    public AxisAlignedBB axisalignedbb;

    public BlockCollisionEvent(Block block, BlockPos pos, Entity collidingEntity, AxisAlignedBB axisalignedbb) {
        this.block = block;
        this.pos = pos;
        this.collidingEntity = collidingEntity;
        this.axisalignedbb = axisalignedbb;
    }
}