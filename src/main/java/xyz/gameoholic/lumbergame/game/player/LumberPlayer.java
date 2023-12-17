package xyz.gameoholic.lumbergame.game.player;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import xyz.gameoholic.lumbergame.LumberGamePlugin;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Represents a player in the Lumber game.
 * Can be offline.
 * Handles displaying scoreboard when logging on/off.
 */
public class LumberPlayer implements Listener {
    private final UUID uuid;
    private int wood = 0;
    private int iron = 0;
    private int gold = 0;
    private int diamond = 0;

    /**
     * Is null when player is logged off.
     */
    private @Nullable PlayerScoreboardManager scoreboardManager;
    private final LumberGamePlugin plugin;

    public LumberPlayer(LumberGamePlugin plugin, UUID uuid) {
        this.uuid = uuid;
        this.plugin = plugin;
    }

    /**
     * Called after the game fully loads and the first wave starts.
     */
    public void onGameLoad() {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null)
            scoreboardManager = new PlayerScoreboardManager(plugin, player);
    }

    @EventHandler
    private void onPlayerJoinEvent(PlayerJoinEvent e) {
        scoreboardManager = new PlayerScoreboardManager(plugin, e.getPlayer());
    }
    @EventHandler
    private void onPlayerQuitEvent(PlayerQuitEvent e) {
        scoreboardManager.delete();
        scoreboardManager = null;
    }

    public void updateScoreboard() {
        scoreboardManager.update();
    }

}
