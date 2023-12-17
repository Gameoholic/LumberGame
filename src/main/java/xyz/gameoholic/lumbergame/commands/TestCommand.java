package xyz.gameoholic.lumbergame.commands;

import fr.mrmicky.fastboard.adventure.FastBoard;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.gameoholic.lumbergame.LumberGamePlugin;
import xyz.gameoholic.lumbergame.config.ConfigParser;
import xyz.gameoholic.lumbergame.game.mob.Mob;
import xyz.gameoholic.lumbergame.game.wave.WaveManager;
import xyz.gameoholic.lumbergame.util.NMSUtil;

import java.util.ArrayList;
import java.util.List;

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
        plugin.saveResource("strings.yml", true);
        plugin.saveResource("hostile_mob_types.yml", true);
        plugin.saveResource("tree_mob_types.yml", true);
        plugin.saveResource("map.yml", true);
        plugin.saveResource("game.yml", true);
        plugin.saveResource("waves.yml", true);
        plugin.setConfig(new ConfigParser(plugin).parse());

        sender.playSound(plugin.getLumberConfig().soundsConfig().treeDamagedSound());

        return false;
    }
}
