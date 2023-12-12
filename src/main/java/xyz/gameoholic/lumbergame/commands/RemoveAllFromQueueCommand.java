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

import java.util.ArrayList;
import java.util.List;

import static net.kyori.adventure.text.Component.text;

public class RemoveAllFromQueueCommand implements CommandExecutor {
    private LumberGamePlugin plugin;
    public RemoveAllFromQueueCommand(LumberGamePlugin plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
        List<Player> queuedPlayers = players.stream()
            .filter(player ->  !plugin.getLumberQueueManager().containsPlayer(player)).toList();
        if (queuedPlayers.size() == 0) {
            sender.sendMessage(text("Could not find queued players to add!").color(NamedTextColor.RED));
            return false;
        }
        queuedPlayers.forEach(queuedPlayer -> {
            plugin.getLumberQueueManager().removePlayer(queuedPlayer, QueueChangeReason.FORCED);
            sender.sendMessage(text("Remove " + queuedPlayer.getName() + " from the queue.").color(NamedTextColor.GREEN));
        });
        return false;
    }
}
