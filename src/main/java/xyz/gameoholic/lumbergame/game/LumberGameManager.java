package xyz.gameoholic.lumbergame.game;

import xyz.gameoholic.lumbergame.LumberGamePlugin;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class LumberGameManager {
    private LumberGamePlugin plugin;
    private Set<UUID> players;
    public LumberGameManager(LumberGamePlugin plugin, Set<UUID> players) {
        this.plugin = plugin;
        this.players = players;


        System.out.println("Game has started!");
    }




}
