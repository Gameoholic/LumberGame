package xyz.gameoholic.lumbergame.commands;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
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

public class AddAllToQueueCommand implements CommandExecutor {
    private LumberGamePlugin plugin;

    public AddAllToQueueCommand(LumberGamePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
        List<Player> unqueuedPlayers = players.stream()
            .filter(player -> !plugin.getQueueManager().containsPlayer(player)).toList();
        if (unqueuedPlayers.size() == 0) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize(
                plugin.getLumberConfig().strings().addAllToQueueCommandNoneFoundMessage())
            );
            return false;
        }
        unqueuedPlayers.forEach(unqueuedPlayer -> {
            plugin.getQueueManager().addPlayer(unqueuedPlayer, QueueChangeReason.FORCED);
            sender.sendMessage(MiniMessage.miniMessage().deserialize(
                    plugin.getLumberConfig().strings().addAllToQueueCommandAddedMessage(),
                    Placeholder.component("player", text(unqueuedPlayer.getName()))
                )
            );
        });
        return false;
    }
}
