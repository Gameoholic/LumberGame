package xyz.gameoholic.lumbergame.game;

import org.bukkit.Bukkit;
import xyz.gameoholic.lumbergame.LumberGamePlugin;
import xyz.gameoholic.lumbergame.game.wave.WaveManager;

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
            uuid -> this.players.add(new LumberPlayer(uuid))
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

    public TreeManager getTreeManager() {
        return treeManager;
    }

    public WaveManager getWaveManager() {
        return waveManager;
    }

    /**
     * Called when the game has ended.
     */
    public void onGameEnd() {
        Bukkit.broadcastMessage("Game has ended! Tree is dead!");
        waveManager.onGameEnd();
    }
}
