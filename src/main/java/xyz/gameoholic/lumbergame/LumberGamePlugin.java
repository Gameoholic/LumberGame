package xyz.gameoholic.lumbergame;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import xyz.gameoholic.lumbergame.commands.*;
import xyz.gameoholic.lumbergame.config.LumberConfig;
import xyz.gameoholic.lumbergame.config.ConfigParser;
import xyz.gameoholic.lumbergame.game.LumberGameManager;
import xyz.gameoholic.lumbergame.listeners.EntityDamageListener;
import xyz.gameoholic.lumbergame.queue.LumberQueueManager;

import javax.annotation.Nullable;

public final class LumberGamePlugin extends JavaPlugin {

    private LumberQueueManager queueManager;
    private @Nullable LumberGameManager gameManager = null;
    private LumberConfig config;
    @Override
    public void onEnable() {

        saveResource("strings.yml", true);
        saveResource("mob_types.yml", true);

        config = ConfigParser.parse(this);


        Bukkit.getPluginCommand("test").setExecutor(new TestCommand(this));

        Bukkit.getPluginCommand("addtoqueue").setExecutor(new AddToQueueCommand(this));
        Bukkit.getPluginCommand("removefromqueue").setExecutor(new RemoveFromQueueCommand(this));
        Bukkit.getPluginCommand("addalltoqueue").setExecutor(new AddAllToQueueCommand(this));
        Bukkit.getPluginCommand("removeallfromqueue").setExecutor(new RemoveAllFromQueueCommand(this));
        Bukkit.getPluginCommand("start").setExecutor(new StartCommand(this));

        Bukkit.getPluginCommand("spawnmob").setExecutor(new SpawnMobCommand(this));

        Bukkit.getPluginManager().registerEvents(new EntityDamageListener(this), this);

        queueManager = new LumberQueueManager(this);




    }


    public LumberQueueManager getQueueManager() {
        return queueManager;
    }

    public LumberGameManager getGameManager() {
        return gameManager;
    }

    public void setGameManager(LumberGameManager gameManager) {
        this.gameManager = gameManager;
    }


    public LumberConfig getLumberConfig() {
        return config;
    }
}
