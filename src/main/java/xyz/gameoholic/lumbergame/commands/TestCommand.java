package xyz.gameoholic.lumbergame.commands;

import fr.mrmicky.fastboard.adventure.FastBoard;
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

        FastBoard board = new FastBoard((Player) sender);

        board.updateTitle(
            MiniMessage.miniMessage().deserialize(plugin.getLumberConfig().strings().scoreboardTitle())
        );
        List<Component> lines = new ArrayList<>();
        plugin.getLumberConfig().strings().scoreboardLines().forEach(
            line ->
                MiniMessage.miniMessage().deserialize(
                    line,
                    Placeholder.component("wave", text(plugin.getGameManager().getWaveNumber() + 1)),
                    Placeholder.component("alive_mobs", text(plugin.getGameManager().getWaveManager().getAliveMobsSize()))
                )
        );
        board.updateLines(lines);

        return false;
    }
}
