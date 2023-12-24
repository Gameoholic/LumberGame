package xyz.gameoholic.lumbergame.game.player;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import xyz.gameoholic.lumbergame.LumberGamePlugin;

import java.util.*;

/**
 *
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

    public void addNPC(Player player, LumberNPC NPC) {
        List<LumberNPC> NPCS = players.get(player.getUniqueId());
        if (NPCS == null)
            players.put(player.getUniqueId(), new ArrayList<>());

        players.get(player.getUniqueId()).add(NPC);
        NPC.spawn();
    }
    public void removeNPC(Player player, LumberNPC NPC) {
        List<LumberNPC> NPCS = players.get(player.getUniqueId());
        if (NPCS == null)
            return;

        NPC.remove();
        players.get(player.getUniqueId()).remove(NPC);
        if (players.get(player.getUniqueId()).size() == 0)
            players.remove(player.getUniqueId());
    }

    public void removeAllNPCS(Player player) {
        List<LumberNPC> NPCS = players.get(player.getUniqueId());
        if (NPCS == null)
            return;

        players.get(player.getUniqueId()).clear();
        players.remove(player.getUniqueId());
    }

    @EventHandler
    private void onPlayerJoinEvent(PlayerJoinEvent e) {
        if (!players.containsKey(e.getPlayer().getUniqueId()))
            return;
        players.get(e.getPlayer().getUniqueId()).forEach(NPC -> NPC.spawn()); // Respawn all player NPC's
    }

    @EventHandler
    private void onPlayerQuitEvent(PlayerQuitEvent e) {
        if (!players.containsKey(e.getPlayer().getUniqueId()))
            return;
        players.get(e.getPlayer().getUniqueId()).forEach(NPC -> NPC.remove()); // Remove all player NPC's
    }


}
