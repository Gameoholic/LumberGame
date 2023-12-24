package xyz.gameoholic.lumbergame.util;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.NPC;
import org.bukkit.entity.Player;
import org.bukkit.profile.PlayerTextures;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.gameoholic.lumbergame.LumberGamePlugin;
import xyz.gameoholic.lumbergame.game.player.LumberPlayer;

import java.util.*;

public class NMSUtil {

    /**
     * Sends a block distruction packet to all Lumber players.
     *
     * @param blockX   The X coordinate of the block to appear destroyed.
     * @param blockY   The Y coordinate of the block to appear destroyed.
     * @param blockZ   The Z coordinate of the block to appear destroyed.
     * @param progress The destruction progress of the block. Range is 0 - 9, where 9 is the most destroyed. Any other number will reset it back to no destruction.
     */
    public static void displayBlockDestruction(LumberGamePlugin plugin, int blockX, int blockY, int blockZ, int progress) {
        for (LumberPlayer lumberPlayer : plugin.getGameManager().getPlayers()) {
            Player player = Bukkit.getPlayer(lumberPlayer.getUuid());
            if (player != null)
                displayBlockDestruction(player, blockX, blockY, blockZ, progress);
        }
    }

    /**
     * Sends a block distruction packet to a specific player.
     *
     * @param blockX   The X coordinate of the block to appear destroyed.
     * @param blockY   The Y coordinate of the block to appear destroyed.
     * @param blockZ   The Z coordinate of the block to appear destroyed.
     * @param progress The destruction progress of the block. Range is 0 - 9, where 9 is the most destroyed. Any other number will reset it back to no destruction.
     */
    public static void displayBlockDestruction(Player player, int blockX, int blockY, int blockZ, int progress) {
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


    public static int spawnNPC(LumberGamePlugin plugin, Player player, Location NPCLocation, OfflinePlayer offlineNPCPlayer) {
        PlayerProfile NPCPlayerProfile = offlineNPCPlayer.getPlayerProfile();

        // Update profile because it's from an offline player, async operation
        NPCPlayerProfile.update().thenAcceptAsync(updatedProfile -> {
            Set<ProfileProperty> profileProperties = updatedProfile.getProperties();
            ProfileProperty textures = profileProperties.stream()
                .filter(property -> property.getName().equals("textures")).findFirst().get();
            String texture = textures.getValue();
            String signature = textures.getSignature();
        }, runnable -> Bukkit.getScheduler().runTask(plugin, runnable));

//        return spawnNPC(player, NPCLocation, texture, signature);
        return -1;
    }

    public static ServerPlayer spawnNPC(Player player, Location NPCLocation, String texture, String signature, Component name) {
        CraftPlayer craftPlayer = (CraftPlayer) player; //CraftBukkit Player
        ServerPlayer serverPlayer = craftPlayer.getHandle(); //NMS Player

        ServerGamePacketListenerImpl listener = serverPlayer.connection;

        MinecraftServer server = serverPlayer.getServer(); //NMS Server
        ServerLevel level = serverPlayer.serverLevel(); //NMS World

        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), LegacyComponentSerializer.legacy('§').serialize(name));

        gameProfile.getProperties().put("textures", new Property("textures", texture, signature));

        ServerPlayer npc = new ServerPlayer(server, level, gameProfile);
        npc.setPos(NPCLocation.getBlockX(), NPCLocation.getBlockY(), NPCLocation.getBlockZ());

        EnumSet<ClientboundPlayerInfoUpdatePacket.Action> playerInfo = EnumSet.of
            (ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER);
        ClientboundPlayerInfoUpdatePacket playerInfoPacket = new ClientboundPlayerInfoUpdatePacket(
            playerInfo, Collections.singletonList(npc));
        ClientboundAddPlayerPacket addPlayerPacket = new ClientboundAddPlayerPacket(npc);

        listener.send(playerInfoPacket);
        listener.send(addPlayerPacket);
        return npc;
    }

    /**
     * Sends the player a packet to rotate the npc towards a certain location.
     * @param player The player.
     * @param npc The NPC.
     * @param targetLocation The location to look at.
     */
    public static void rotateNPC(Player player, ServerPlayer npc, Location targetLocation) {
        CraftPlayer craftPlayer = (CraftPlayer) player; //CraftBukkit Player
        ServerPlayer serverPlayer = craftPlayer.getHandle(); //NMS Player

        ServerGamePacketListenerImpl listener = serverPlayer.connection;

        Location targetLoc = targetLocation.clone();
        Location NPCLocation = new Location(npc.serverLevel().getWorld(), npc.getX(), npc.getY(), npc.getZ());
        targetLoc.setDirection(targetLoc.subtract(NPCLocation).toVector());
        float yaw = targetLoc.getYaw();
        float pitch = targetLoc.getPitch();


        ClientboundRotateHeadPacket rotateHeadPacket = new ClientboundRotateHeadPacket(
            npc,
            (byte) ((yaw % 360) / 360 * 256)
            );
        ClientboundMoveEntityPacket.Rot moveEntityPacket = new ClientboundMoveEntityPacket.Rot(
            npc.getId(),
            (byte) ((yaw % 360) / 360 * 256),
            (byte) ((pitch % 360) / 360 * 256),
            true
        );
        listener.send(moveEntityPacket);
        listener.send(rotateHeadPacket);

    }

    public static void removeNPC(Player player, int NPCEntityId) {
        CraftPlayer craftPlayer = (CraftPlayer) player; //CraftBukkit Player
        ServerPlayer serverPlayer = craftPlayer.getHandle(); //NMS Player

        ServerGamePacketListenerImpl listener = serverPlayer.connection;

        ClientboundRemoveEntitiesPacket removeEntitiesPacket = new ClientboundRemoveEntitiesPacket(NPCEntityId);
        listener.send(removeEntitiesPacket);
    }

}
