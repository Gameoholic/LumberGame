package xyz.gameoholic.lumbergame.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import xyz.gameoholic.lumbergame.LumberGamePlugin;
import xyz.gameoholic.lumbergame.game.LumberGameManager;

public class StartCommand implements CommandExecutor {
    private LumberGamePlugin plugin;
    public StartCommand(LumberGamePlugin plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        plugin.setGameManager(new LumberGameManager(plugin, plugin.getQueueManager().getPlayers()));
        plugin.getQueueManager().resetQueue();
        return false;
    }
}
