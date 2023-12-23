package xyz.gameoholic.lumbergame.util;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundAddPlayerPacket;
import net.minecraft.network.protocol.game.ClientboundBlockDestructionPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.profile.PlayerTextures;
import xyz.gameoholic.lumbergame.LumberGamePlugin;
import xyz.gameoholic.lumbergame.game.player.LumberPlayer;

import java.util.*;

public class NMSUtil {

    /**
     * Sends a block distruction packet to all Lumber players.
     * @param blockX The X coordinate of the block to appear destroyed.
     * @param blockY The Y coordinate of the block to appear destroyed.
     * @param blockZ The Z coordinate of the block to appear destroyed.
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
     * @param blockX The X coordinate of the block to appear destroyed.
     * @param blockY The Y coordinate of the block to appear destroyed.
     * @param blockZ The Z coordinate of the block to appear destroyed.
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


    public static void spawnNPC(LumberGamePlugin plugin, Player player, Location NPCLocation, OfflinePlayer offlineNPCPlayer) {
        PlayerProfile NPCPlayerProfile = offlineNPCPlayer.getPlayerProfile();

        // Update profile because it's from an offline player, async operation
        NPCPlayerProfile.update().thenAcceptAsync(updatedProfile -> {
            Set<ProfileProperty> profileProperties = updatedProfile.getProperties();
            ProfileProperty textures = profileProperties.stream()
                .filter(property -> property.getName().equals("textures")).findFirst().get();
            String texture = textures.getValue();
            String signature = textures.getSignature();
            spawnNPC(player, NPCLocation, texture, signature);
        }, runnable -> Bukkit.getScheduler().runTask(plugin, runnable));
    }

    public static void spawnNPC(Player player, Location NPCLocation, String texture, String signature) {
        CraftPlayer craftPlayer = (CraftPlayer) player; //CraftBukkit Player
        ServerPlayer serverPlayer = craftPlayer.getHandle(); //NMS Player

        ServerGamePacketListenerImpl listener = serverPlayer.connection;

        MinecraftServer server = serverPlayer.getServer(); //NMS Server
        ServerLevel level = serverPlayer.serverLevel(); //NMS World

        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), "Player");

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
    }

}
