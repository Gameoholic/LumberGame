package xyz.gameoholic.lumbergame.queue;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;
import xyz.gameoholic.lumbergame.LumberGamePlugin;

import java.util.*;

import static net.kyori.adventure.text.Component.text;

public class LumberQueueManager {
    private LumberGamePlugin plugin;
    private Set<UUID> queuedPlayers = new HashSet<>();

    public LumberQueueManager(LumberGamePlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Adds a player to the queue.
     */
    public void addPlayer(Player player, QueueChangeReason queueChangeReason) {
        queuedPlayers.add(player.getUniqueId());
        if (queueChangeReason == QueueChangeReason.VOLUNTARY) {
            player.sendMessage(text("You've joined the queue!").color(NamedTextColor.GREEN));
        }
        else if (queueChangeReason == QueueChangeReason.FORCED) {
            player.sendMessage(text("You've been added to the queue!").color(NamedTextColor.GREEN));
        }
    }

    /**
     * Removes a player from the queue.
     */
    public void removePlayer(Player player, QueueChangeReason queueChangeReason) {
        queuedPlayers.remove(player.getUniqueId());
        if (queueChangeReason == QueueChangeReason.VOLUNTARY) {
            player.sendMessage(text("You've left the queue!").color(NamedTextColor.RED));
        }
        else if (queueChangeReason == QueueChangeReason.FORCED) {
            player.sendMessage(text("You've been removed from the queue!").color(NamedTextColor.RED));
        }
    }

    /**
     * Returns whether the player is in the queue.
     */
    public boolean containsPlayer(Player player) {
        return queuedPlayers.contains(player.getUniqueId());
    }

    /**
     * Returns the players in the queue.
     */
    public Set<UUID> getPlayers() {
        return queuedPlayers;
    }

    /**
     * Empties the queued players without letting the players know.
     */
    public void resetQueue() {
        queuedPlayers = new HashSet<>();
    }
}
