package xyz.gameoholic.lumbergame;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.gameoholic.lumbergame.commands.*;
import xyz.gameoholic.lumbergame.config.LumberConfig;
import xyz.gameoholic.lumbergame.config.ConfigParser;
import xyz.gameoholic.lumbergame.game.GameManager;
import xyz.gameoholic.lumbergame.game.item.ItemManager;
import xyz.gameoholic.lumbergame.game.player.npc.PlayerNPCManager;
import xyz.gameoholic.lumbergame.queue.LumberQueueManager;

import javax.annotation.Nullable;

public final class LumberGamePlugin extends JavaPlugin {

    private LumberQueueManager queueManager;
    private LumberConfig config;
    private @Nullable GameManager gameManager = null;
    private @Nullable ItemManager itemManager = null;
    private PlayerNPCManager playerNPCManager;
    @Override
    public void onEnable() {
        saveResource("strings.yml", true);
        saveResource("hostile_mob_types.yml", true);
        saveResource("tree_mob_types.yml", true);
        saveResource("map.yml", true);
        saveResource("game.yml", true);
        saveResource("waves.yml", true);
        saveResource("sounds.yml", true);

        config = new ConfigParser(this).parse();

        config.mapConfig().treeLevelSchematicsProvided().forEach(level ->
            saveResource("schematics/tree/level_" + level + ".schem", true)
        );




        Bukkit.getPluginCommand("test").setExecutor(new TestCommand(this));

        Bukkit.getPluginCommand("addtoqueue").setExecutor(new AddToQueueCommand(this));
        Bukkit.getPluginCommand("removefromqueue").setExecutor(new RemoveFromQueueCommand(this));
        Bukkit.getPluginCommand("addalltoqueue").setExecutor(new AddAllToQueueCommand(this));
        Bukkit.getPluginCommand("removeallfromqueue").setExecutor(new RemoveAllFromQueueCommand(this));
        Bukkit.getPluginCommand("start").setExecutor(new StartCommand(this));
        Bukkit.getPluginCommand("startwave").setExecutor(new StartWaveCommand(this));
        Bukkit.getPluginCommand("spawnmob").setExecutor(new SpawnMobCommand(this));
        Bukkit.getPluginCommand("spawnitem").setExecutor(new SpawnItemCommand(this));
        Bukkit.getPluginCommand("shop").setExecutor(new ShopCommand(this));

        queueManager = new LumberQueueManager(this);
        playerNPCManager = new PlayerNPCManager(this);




    }

    /**
     * Starts the game with all the players currently queued.
     */
    public void startGame(double waveCRMultiplier, double waveSpawnRateMultiplier) {
        gameManager = new GameManager(this, queueManager.getPlayers(), waveCRMultiplier, waveSpawnRateMultiplier);
        queueManager.resetQueue();
        itemManager = new ItemManager(this);
        gameManager.onGameLoad();
    }

    /**
     * Called when the game has ended.
     */
    public void onGameEnd() {
        gameManager = null;
        itemManager = null;
    }

    public LumberQueueManager getQueueManager() {
        return queueManager;
    }

    public GameManager getGameManager() {
        return gameManager;
    }
    public ItemManager getItemManager() {
        return itemManager;
    }

    public PlayerNPCManager getPlayerNPCManager() {
        return playerNPCManager;
    }

    public LumberConfig getLumberConfig() {
        return config;
    }


    public void setConfig(LumberConfig config) {
        this.config = config;
    }


}
