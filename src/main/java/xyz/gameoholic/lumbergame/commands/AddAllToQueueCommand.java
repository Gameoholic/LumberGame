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
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static net.kyori.adventure.text.Component.text;

public class AddAllToQueueCommand implements CommandExecutor {
    private LumberGamePlugin plugin;
    public AddAllToQueueCommand(LumberGamePlugin plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
        List<Player> unqueuedPlayers = players.stream()
            .filter(player ->  !plugin.getLumberQueueManager().containsPlayer(player)).toList();
        if (unqueuedPlayers.size() == 0) {
            sender.sendMessage(text("Could not find unqueued players to add!").color(NamedTextColor.RED));
            return false;
        }
        unqueuedPlayers.forEach(unqueuedPlayer -> {
            plugin.getLumberQueueManager().addPlayer(unqueuedPlayer, QueueChangeReason.FORCED);
            sender.sendMessage(text("Added " + unqueuedPlayer.getName() + " to the queue.").color(NamedTextColor.GREEN));
        });
        return false;
    }
}
