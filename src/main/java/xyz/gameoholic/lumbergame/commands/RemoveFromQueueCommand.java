package xyz.gameoholic.lumbergame.commands;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.gameoholic.lumbergame.LumberGamePlugin;
import xyz.gameoholic.lumbergame.queue.QueueChangeReason;

import static net.kyori.adventure.text.Component.text;

public class RemoveFromQueueCommand implements CommandExecutor {
    private LumberGamePlugin plugin;
    public RemoveFromQueueCommand(LumberGamePlugin plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage(text("Please provide a player's name to remove from the queue.").color(NamedTextColor.RED));
            return false;
        }
        String desiredPlayerName = args[0];
        Player desiredPlayer = Bukkit.getPlayer(desiredPlayerName);
        if (desiredPlayer == null) {
            sender.sendMessage(text("Player is offline!").color(NamedTextColor.RED));
            return false;
        }
        if (!plugin.getQueueManager().containsPlayer(desiredPlayer)) {
            sender.sendMessage(text("Player is not in the queue!").color(NamedTextColor.RED));
            return false;
        }
        plugin.getQueueManager().removePlayer(desiredPlayer, QueueChangeReason.FORCED);
        sender.sendMessage(text("Successfully removed " + sender.getName() + " from the queue!").color(NamedTextColor.GREEN));
        return false;
    }

}
