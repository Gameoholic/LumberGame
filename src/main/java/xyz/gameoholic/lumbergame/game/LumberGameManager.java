package xyz.gameoholic.lumbergame.game;

import xyz.gameoholic.lumbergame.LumberGamePlugin;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class LumberGameManager {
    private LumberGamePlugin plugin;
    private Set<LumberPlayer> players = new HashSet<>();
    private TreeManager treeManager;
    public LumberGameManager(LumberGamePlugin plugin, Set<UUID> players) {
        this.plugin = plugin;
        treeManager = new TreeManager(plugin);
        players.forEach(
            uuid -> this.players.add(new LumberPlayer(uuid))
        );

        plugin.getLogger().info("Game has started with " + players.size() + " players.");
    }


    public TreeManager getTreeManager() {
        return treeManager;
    }



}
