package xyz.gameoholic.lumbergame.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.gameoholic.lumbergame.LumberGamePlugin;
import xyz.gameoholic.lumbergame.game.mob.Mob;

import java.util.Objects;

public class SpawnMobCommand implements CommandExecutor {
    private LumberGamePlugin plugin;

    public SpawnMobCommand(LumberGamePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 2)
            return false;
        if (sender instanceof Player)
            return false;
        Player player = (Player) sender;
        new Mob(Objects.requireNonNull(plugin.getLumberConfig().mobTypes().stream().filter(mobType -> mobType.id() == args[0])
            .findFirst().get()), Integer.parseInt(args[1]), player.getLocation());

        return false;
    }
}
