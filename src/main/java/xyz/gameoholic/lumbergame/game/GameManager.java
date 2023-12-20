package xyz.gameoholic.lumbergame.game;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;
import xyz.gameoholic.lumbergame.LumberGamePlugin;
import xyz.gameoholic.lumbergame.game.player.LumberPlayer;
import xyz.gameoholic.lumbergame.game.wave.WaveManager;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static net.kyori.adventure.text.Component.text;

public class GameManager {
    private final LumberGamePlugin plugin;
    private Set<LumberPlayer> players = new HashSet<>();
    private TreeManager treeManager;
    private WaveManager waveManager;

    /**
     * The wave number, uses zeroth index numbering.
     */
    int waveNumber = 0;

    public GameManager(LumberGamePlugin plugin, Set<UUID> players) {
        this.plugin = plugin;
        treeManager = new TreeManager(plugin);
        players.forEach(
            uuid -> {
                LumberPlayer lumberPlayer = new LumberPlayer(plugin, uuid);
                this.players.add(lumberPlayer);
            }
        );
        startGame();
    }

    private void startGame() {
        clearOldEntities();
        startWave();
        plugin.getLogger().info("Game has started with " + players.size() + " players.");
    }

    private void clearOldEntities() {
        // If entity contains lumber_mob key, it's an old mob from previous waves.
        plugin.getLumberConfig().mapConfig().treeLocation().getWorld().getEntities().forEach(entity ->
            {
                if (entity.getPersistentDataContainer().get(
                    new NamespacedKey(plugin, "lumber_mob"), PersistentDataType.BOOLEAN) != null)
                    entity.remove();
            }
        );
    }

    /**
     * Should be called when the wave ends.
     */
    public void onWaveEnd() {
        startNewWave();
    }

    /**
     * Increments waveNumber and starts the wave.
     */
    private void startNewWave() {
        waveNumber++;
        startWaveWithScoreboardUpdate();
    }

    /**
     * Starts a specific wave.
     * @param waveNumber The new wave number.
     */
    public void startSpecificWave(int waveNumber) {
        this.waveNumber = waveNumber;
        startWaveWithScoreboardUpdate();
    }

    /**
     * Starts the wave as per the waveNumber variable.
     * Does not update the scoreboard.
     */
    private void startWave() {
        // Wave manager should be alerted that the wave has ended, if it doesn't already know (if forced by command for example)
        if (waveManager != null && !waveManager.getWaveEnded())
            waveManager.onWaveEnd();
        waveManager = new WaveManager(plugin, plugin.getLumberConfig().waves().get(waveNumber));
        Bukkit.broadcast(MiniMessage.miniMessage().deserialize(
            plugin.getLumberConfig().strings().newWaveStartMessage(),
            Placeholder.component("wave", text(waveNumber + 1))
        ));
    }

    /**
     * Starts the wave as per the waveNumber variable.
     * Also updates the scoreboard.
     */
    private void startWaveWithScoreboardUpdate() {
        startWave();
        updatePlayerScoreboards(); // Update wave number
    }

    /**
     * Called after the game has fully loaded and the first wave has started.
     */
    public void onGameLoad() {
        players.forEach(player -> player.onGameLoad());
    }

    /**
     * Called when the game has ended.
     */
    public void onGameEnd() {
        Bukkit.broadcastMessage("Game has ended! Tree is dead!");
        waveManager.onGameEnd();
    }

    /**
     * Updates scoreboards for every player.
     */
    public void updatePlayerScoreboards() {
        players.forEach(player -> player.updateScoreboard());
    }

    public int getWaveNumber() {
        return waveNumber;
    }

    public TreeManager getTreeManager() {
        return treeManager;
    }

    public WaveManager getWaveManager() {
        return waveManager;
    }

    public Set<LumberPlayer> getPlayers() {
        return players;
    }
}
