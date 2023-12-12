package xyz.gameoholic.lumbergame;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.gameoholic.lumbergame.commands.AddAllToQueueCommand;
import xyz.gameoholic.lumbergame.commands.AddToQueueCommand;
import xyz.gameoholic.lumbergame.commands.RemoveAllFromQueueCommand;
import xyz.gameoholic.lumbergame.commands.RemoveFromQueueCommand;
import xyz.gameoholic.lumbergame.queue.LumberQueueManager;

public final class LumberGamePlugin extends JavaPlugin {

    private LumberQueueManager lumberQueueManager;
    @Override
    public void onEnable() {

        Bukkit.getPluginCommand("addtoqueue").setExecutor(new AddToQueueCommand(this));
        Bukkit.getPluginCommand("removefromqueue").setExecutor(new RemoveFromQueueCommand(this));
        Bukkit.getPluginCommand("addalltoqueue").setExecutor(new AddAllToQueueCommand(this));
        Bukkit.getPluginCommand("removeallfromqueue").setExecutor(new RemoveAllFromQueueCommand(this));


        lumberQueueManager = new LumberQueueManager(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public LumberQueueManager getLumberQueueManager() {
        return lumberQueueManager;
    }
}
