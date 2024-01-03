package xyz.gameoholic.lumbergame.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.gameoholic.lumbergame.LumberGamePlugin;
import xyz.gameoholic.lumbergame.TreeLevelUpParticle;
import xyz.gameoholic.lumbergame.config.ConfigParser;

import java.util.Collection;

import static net.kyori.adventure.text.Component.text;


/**
 * DEBUG COMMAND
 */
public class TestCommand implements CommandExecutor {
    private LumberGamePlugin plugin;

    public TestCommand(LumberGamePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        plugin.setConfig(new ConfigParser(plugin).parse());

        Player player = (Player) sender;
        Collection<Entity> hurtEntities = player.getLocation()
            .getNearbyEntities(Double.parseDouble(args[0]), Double.parseDouble(args[1]), Double.parseDouble(args[0]));
        hurtEntities.forEach(
            it -> {
                if (it instanceof Mob mob) {
                    Bukkit.broadcast(mob.name());
                }
            }
        );
        return false;
    }
}
