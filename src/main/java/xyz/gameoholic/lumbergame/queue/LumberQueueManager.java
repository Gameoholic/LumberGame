package xyz.gameoholic.lumbergame.queue;

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
            player.sendMessage(text("You've joined the queue!").color(TextColor.color(0x34eb37)));
        }
        else if (queueChangeReason == QueueChangeReason.FORCED) {
            player.sendMessage(text("You were added to the queue!").color(TextColor.color(0x34eb37)));
        }
    }

    /**
     * Removes a player from the queue.
     */
    public void removePlayer(Player player, QueueChangeReason queueChangeReason) {
        queuedPlayers.remove(player.getUniqueId());
        if (queueChangeReason == QueueChangeReason.VOLUNTARY) {
            player.sendMessage(text("You've been removed from the queue!").color(TextColor.color(0x34eb37)));
        }
        else if (queueChangeReason == QueueChangeReason.FORCED) {
            player.sendMessage(text("You've left the queue!").color(TextColor.color(0x34eb37)));
        }
    }

    /**
     * Returns whether the player is in the queue.
     */
    public boolean containsPlayer(Player player) {
        return queuedPlayers.contains(player.getUniqueId());
    }
}
