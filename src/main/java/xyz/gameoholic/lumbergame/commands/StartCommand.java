package xyz.gameoholic.lumbergame.commands;

import net.kyori.adventure.text.minimessage.MiniMessage;
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
        if (plugin.getQueueManager().getPlayers().size() < 1) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize(
                plugin.getLumberConfig().strings().startCommandNoQueuedPlayersMessage())
            );
            return false;
        }
        if (plugin.getGameManager() != null) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize(
                plugin.getLumberConfig().strings().startCommandGameInProgressMessage())
            );
            return false;
        }

        plugin.setGameManager(new LumberGameManager(plugin, plugin.getQueueManager().getPlayers()));
        plugin.getQueueManager().resetQueue();
        return false;
    }
}
