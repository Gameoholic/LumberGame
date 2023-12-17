package xyz.gameoholic.lumbergame.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.gameoholic.lumbergame.LumberGamePlugin;


/**
 * DEBUG COMMAND
 */
public class StartWaveCommand implements CommandExecutor {
    private LumberGamePlugin plugin;

    public StartWaveCommand(LumberGamePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 1)
            return false;


        plugin.getGameManager().startSpecificWave(Integer.parseInt(args[0]) - 1);
        return false;
    }
}
