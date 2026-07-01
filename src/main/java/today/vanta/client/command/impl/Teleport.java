package today.vanta.client.command.impl;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.pathfinder.WalkNodeProcessor;
import today.vanta.client.command.Command;
import today.vanta.util.game.player.ChatUtil;

public class Teleport extends Command {
    private static final float PATH_SEARCH_RANGE = 128.0F;

    public Teleport() {
        super("Teleport", "Teleports to a player or coordinates using pathfinding.");
        aliases = new String[]{"teleport", "tp"};
    }

    @Override
    public void execute(String[] args) {
        if (args.length != 1 && args.length != 3) {
            ChatUtil.error("Usage: teleport <player> or teleport <x> <y> <z>");
            return;
        }

        if (mc.thePlayer == null || mc.theWorld == null) {
            ChatUtil.error("You must be in-game to use this command!");
            return;
        }

        if (mc.thePlayer.isRiding()) {
            ChatUtil.error("You cannot teleport while riding an entity!");
            return;
        }

        double targetX;
        double targetY;
        double targetZ;

        if (args.length == 1) {
            EntityPlayer target = mc.theWorld.getPlayerEntityByName(args[0]);

            if (target == null) {
                ChatUtil.error("Player &c{}&f not found!", args[0]);
                return;
            }

            targetX = target.posX;
            targetY = target.posY;
            targetZ = target.posZ;
        } else {
            try {
                targetX = Double.parseDouble(args[0]);
                targetY = Double.parseDouble(args[1]);
                targetZ = Double.parseDouble(args[2]);
            } catch (NumberFormatException e) {
                ChatUtil.error("Invalid coordinates!");
                return;
            }
        }

        BlockPos targetBlockPos = new BlockPos(
                MathHelper.floor_double(targetX),
                MathHelper.floor_double(targetY),
                MathHelper.floor_double(targetZ)
        );

        PathEntity path = calculatePath(targetBlockPos);

        if (path == null || path.getCurrentPathLength() == 0) {
            ChatUtil.error("Could not find a valid path to the target!");
            return;
        }

        PathPoint finalPoint = path.getFinalPathPoint();
        boolean reachedTarget = finalPoint != null
                && finalPoint.xCoord == targetBlockPos.getX()
                && finalPoint.yCoord == targetBlockPos.getY()
                && finalPoint.zCoord == targetBlockPos.getZ();

        teleportAlongPath(path);

        if (reachedTarget) {
            mc.thePlayer.setPosition(targetX, targetY, targetZ);
            sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(targetX, targetY, targetZ, true));
        } else {
            ChatUtil.warn("Path could not reach the exact target, stopped at the closest reachable point.");
        }

        send("Teleported to &e{}&f, &e{}&f, &e{}&f!", targetX, targetY, targetZ);
    }

    private PathEntity calculatePath(BlockPos targetPos) {
        WalkNodeProcessor nodeProcessor = new WalkNodeProcessor();
        nodeProcessor.setEnterDoors(true);
        nodeProcessor.setCanSwim(true);

        PathFinder pathFinder = new PathFinder(nodeProcessor);

        BlockPos entityPos = new BlockPos(mc.thePlayer);
        int searchOffset = (int) (PATH_SEARCH_RANGE + 8.0F);

        ChunkCache chunkCache = new ChunkCache(
                mc.theWorld,
                entityPos.add(-searchOffset, -searchOffset, -searchOffset),
                entityPos.add(searchOffset, searchOffset, searchOffset),
                0
        );

        return pathFinder.createEntityPathTo(chunkCache, mc.thePlayer, targetPos, PATH_SEARCH_RANGE);
    }

    private void teleportAlongPath(PathEntity path) {
        for (int i = 0; i < path.getCurrentPathLength(); i++) {
            PathPoint point = path.getPathPointFromIndex(i);

            double x = point.xCoord + 0.5D;
            double y = point.yCoord;
            double z = point.zCoord + 0.5D;

            mc.thePlayer.setPosition(x, y, z);
            sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, true));
        }
    }

    @Override
    public String[] getArgs() {
        return new String[]{
                "teleport <player>",
                "teleport <x> <y> <z>"
        };
    }
}