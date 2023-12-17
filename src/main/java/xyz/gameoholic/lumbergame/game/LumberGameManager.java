package xyz.gameoholic.lumbergame.game;

import org.bukkit.Bukkit;
import xyz.gameoholic.lumbergame.LumberGamePlugin;
import xyz.gameoholic.lumbergame.game.player.LumberPlayer;
import xyz.gameoholic.lumbergame.game.wave.WaveManager;
import xyz.gameoholic.lumbergame.listeners.EntityDamageListener;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class LumberGameManager {
    private LumberGamePlugin plugin;
    private Set<LumberPlayer> players = new HashSet<>();
    private TreeManager treeManager;
    private WaveManager waveManager;

    /**
     * The wave number, uses zeroth index numbering.
     */
    int waveNumber = 0;

    public LumberGameManager(LumberGamePlugin plugin, Set<UUID> players) {
        this.plugin = plugin;
        treeManager = new TreeManager(plugin);
        players.forEach(
            uuid -> {
                LumberPlayer lumberPlayer = new LumberPlayer(plugin, uuid);
                Bukkit.getPluginManager().registerEvents(lumberPlayer, plugin); // Register all needed LumberPlayer listeners
                this.players.add(lumberPlayer);
            }
        );
        startGame();
    }

    private void startGame() {
        waveManager = new WaveManager(plugin, plugin.getLumberConfig().waves().get(0));
        plugin.getLogger().info("Game has started with " + players.size() + " players.");
    }

    /**
     * Should be called when the wave ends.
     */
    public void onWaveEnd() {
        startNewRound();
    }

    private void startNewRound() {
        waveNumber++;
        Bukkit.broadcastMessage("Starting Round " + (waveNumber + 1));
        waveManager = new WaveManager(plugin, plugin.getLumberConfig().waves().get(waveNumber));
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

    public int getWaveNumber() {
        return waveNumber;
    }

    public TreeManager getTreeManager() {
        return treeManager;
    }

    public WaveManager getWaveManager() {
        return waveManager;
    }
}
