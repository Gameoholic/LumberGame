package xyz.gameoholic.lumbergame;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.gameoholic.lumbergame.commands.*;
import xyz.gameoholic.lumbergame.game.LumberGameManager;
import xyz.gameoholic.lumbergame.queue.LumberQueueManager;

public final class LumberGamePlugin extends JavaPlugin {

    private LumberQueueManager queueManager;
    public LumberQueueManager getQueueManager() {
        return queueManager;
    }
    private LumberGameManager gameManager;
    public LumberGameManager getGameManager() {
        return gameManager;
    }
    public void setGameManager(LumberGameManager gameManager) {
        this.gameManager = gameManager;
    }
    @Override
    public void onEnable() {

        Bukkit.getPluginCommand("addtoqueue").setExecutor(new AddToQueueCommand(this));
        Bukkit.getPluginCommand("removefromqueue").setExecutor(new RemoveFromQueueCommand(this));
        Bukkit.getPluginCommand("addalltoqueue").setExecutor(new AddAllToQueueCommand(this));
        Bukkit.getPluginCommand("removeallfromqueue").setExecutor(new RemoveAllFromQueueCommand(this));
        Bukkit.getPluginCommand("start").setExecutor(new StartCommand(this));


        queueManager = new LumberQueueManager(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }




}
