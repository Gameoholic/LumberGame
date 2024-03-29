package xyz.gameoholic.lumbergame.game.player.npc;

import io.netty.channel.*;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.gameoholic.lumbergame.LumberGamePlugin;

import javax.annotation.Nullable;
import java.util.*;

/**
 * Manages NPC's visible and interactible with players.
 */
public class PlayerNPCManager implements Listener {
    private final LumberGamePlugin plugin;
    /**
     * List of NPC's visible to the player. If NPC amount is 0, removes from the map.
     */
    private final Map<UUID, List<LumberNPC>> players = new HashMap();

    public PlayerNPCManager(LumberGamePlugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Adds an NPC and makes it visible to the player.
     *
     * @param player The player to show the NPC to.
     * @param NPC    The NPC specific to this player.
     */
    public void addNPC(Player player, LumberNPC NPC) {
        List<LumberNPC> NPCS = players.get(player.getUniqueId());
        if (NPCS == null)
            players.put(player.getUniqueId(), new ArrayList<>());

        players.get(player.getUniqueId()).add(NPC);
        NPC.spawn();
    }

    /**
     * Removes an NPC for this player.
     *
     * @param player The player to remove the NPC from.
     * @param NPC    The NPC specific to this player.
     */
    public void removeNPC(Player player, LumberNPC NPC) {
        List<LumberNPC> NPCS = players.get(player.getUniqueId());
        if (NPCS == null)
            return;

        NPC.remove();
        players.get(player.getUniqueId()).remove(NPC);
        if (players.get(player.getUniqueId()).size() == 0)
            players.remove(player.getUniqueId());
    }

    /**
     * Removes all NPC's for this player.
     *
     * @param uuid The player uuid to remove the NPC's from.
     */
    public void removeAllNPCS(UUID uuid) {
        List<LumberNPC> NPCS = players.get(uuid);
        if (NPCS == null)
            return;

        NPCS.forEach( NPC -> NPC.remove());
        players.get(uuid).clear();
        players.remove(uuid);
    }

    /**
     * Removes ALL NPC's from all players.
     */
    public void reset() {
        players.entrySet().forEach( playerEntry -> {
            List<LumberNPC> NPCS = playerEntry.getValue();
            NPCS.forEach( NPC -> NPC.remove());
        });
        players.clear();
    }

    @EventHandler
    private void onPlayerJoinEvent(PlayerJoinEvent e) {
        injectPlayer(e.getPlayer());

        if (!players.containsKey(e.getPlayer().getUniqueId()))
            return;
        players.get(e.getPlayer().getUniqueId()).forEach(NPC -> NPC.spawn()); // Respawn all player NPC's
    }

    @EventHandler
    private void onPlayerQuitEvent(PlayerQuitEvent e) {
        uninjectPlayer(e.getPlayer());

        if (!players.containsKey(e.getPlayer().getUniqueId()))
            return;
        players.get(e.getPlayer().getUniqueId()).forEach(NPC -> NPC.remove()); // Remove all player NPC's
    }

    /**
     * Injects a listener for serverbound packets for the player.
     *
     * @param player The player to listen to packets from.
     */
    private void injectPlayer(Player player) {
        ChannelDuplexHandler channelDuplexHandler = new ChannelDuplexHandler() {
            // Listen to serverbound packets
            @Override
            public void channelRead(ChannelHandlerContext channelHandlerContext, Object packet) throws Exception {
                super.channelRead(channelHandlerContext, packet);

                if (packet instanceof ServerboundInteractPacket interactPacket) {
                    @Nullable List<LumberNPC> npcs = players.get(player.getUniqueId());
                    if (npcs == null) // Game could have not started, therefore the player could not have npcs assigned to them
                        return;
                    npcs.stream().filter(NPC -> NPC.getServerPlayer().getId() == interactPacket.getEntityId()).findFirst()
                            .ifPresent(NPC ->
                                    // Must run on main thread
                                    new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                            NPC.onInteract(interactPacket.isAttack());
                                        }
                                    }.runTask(plugin)
                            );
                }
            }
        };
        ChannelPipeline pipeline = ((CraftPlayer) player).getHandle().connection.connection.channel.pipeline();
        pipeline.addBefore("packet_handler", player.getName(), channelDuplexHandler);
    }

    /**
     * Uninjects the listener for serverbound packets for the player.
     *
     * @param player The player to stop listening to packets from.
     */
    private void uninjectPlayer(Player player) {
        Channel channel = ((CraftPlayer) player).getHandle().connection.connection.channel;
        channel.eventLoop().submit(() -> {
            channel.pipeline().remove(player.getName());
            return null;
        });
    }

}
