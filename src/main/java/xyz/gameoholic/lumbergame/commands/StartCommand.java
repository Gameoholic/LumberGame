package xyz.gameoholic.lumbergame.commands;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.apache.commons.lang3.math.NumberUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import xyz.gameoholic.lumbergame.LumberGamePlugin;

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

        sender.sendMessage(MiniMessage.miniMessage().deserialize(
            plugin.getLumberConfig().strings().startCommandSuccessMessage())
        );

        double waveCRMultiplier = (args.length > 0 && NumberUtils.isCreatable(args[0])) ? Double.parseDouble(args[0]) : 1.0;
        double waveSpawnRateMultiplier = (args.length > 1 && NumberUtils.isCreatable(args[1])) ? Double.parseDouble(args[1]) : 1.0;

        plugin.startGame(waveCRMultiplier, waveSpawnRateMultiplier);
        return false;
    }
}
