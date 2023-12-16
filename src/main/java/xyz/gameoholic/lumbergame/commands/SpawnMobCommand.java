package xyz.gameoholic.lumbergame.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.gameoholic.lumbergame.LumberGamePlugin;
import xyz.gameoholic.lumbergame.util.MobUtil;


/**
 * DEBUG COMMAND
 */
public class SpawnMobCommand implements CommandExecutor {
    private LumberGamePlugin plugin;

    public SpawnMobCommand(LumberGamePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 2)
            return false;

        if (!(sender instanceof Player))
            return false;

        Player player = (Player) sender;

        plugin.getGameManager().getWaveManager().getMob(args[0], Integer.parseInt(args[1]), false).spawnMob(player.getLocation());

        return false;
    }
}
