package xyz.gameoholic.lumbergame.util;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundBlockDestructionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Random;

public class NMSUtil {

    /**
     * Sends a block distruction packet to all online players.
     * @param blockX The X coordinate of the block to appear destroyed.
     * @param blockY The Y coordinate of the block to appear destroyed.
     * @param blockZ The Z coordinate of the block to appear destroyed.
     * @param progress The destruction progress of the block. Range is 0 - 9, where 9 is the most destroyed. Any other number will reset it back to no destruction.
     */
    public static void displayBlockDestruction(int blockX, int blockY, int blockZ, int progress) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            CraftPlayer craftPlayer = (CraftPlayer) player;
            ServerPlayer serverPlayer = craftPlayer.getHandle();
            ServerGamePacketListenerImpl listener = serverPlayer.connection;

            BlockPos blockPos = new BlockPos(blockX, blockY, blockZ);

            // An Entity ID can only affect one block.
            // Therefore, Entity ID's have to be different when affecting multiple blocks at a time.
            // At the same time, each block should have ONE distinct ID for it.
            // Using different ID's for the same block will result in newer packets not updating the block. (so doing it randomly
            // is not an option).
            // My solution is to pass the ID as the block's coordinates.
            // It's hacky, and will not work in certain edge cases (integer overflow, etc.)
            // but is okay for our use case.
            // Ideally, a hashmap should be used to map ID's to block locations.

            int id = Integer.parseInt((blockX + "0" + blockY + "0" + blockZ).replace("-", ""));

            listener.send(new ClientboundBlockDestructionPacket(id, blockPos, progress));
        }
    }

}
