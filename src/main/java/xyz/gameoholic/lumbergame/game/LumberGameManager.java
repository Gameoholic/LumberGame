package xyz.gameoholic.lumbergame.game;

import org.bukkit.Location;
import xyz.gameoholic.lumbergame.LumberGamePlugin;
import xyz.gameoholic.lumbergame.game.mob.Mob;
import xyz.gameoholic.lumbergame.game.mob.MobType;
import xyz.gameoholic.lumbergame.game.mob.TreeMob;
import xyz.gameoholic.lumbergame.game.wave.Wave;
import xyz.gameoholic.lumbergame.game.wave.WaveManager;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class LumberGameManager {
    private LumberGamePlugin plugin;
    private Set<LumberPlayer> players = new HashSet<>();
    private TreeManager treeManager;
    private WaveManager waveManager;
    int waveNumber = 1;
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


    public TreeManager getTreeManager() {
        return treeManager;
    }

    public WaveManager getWaveManager() {
        return waveManager;
    }
}
