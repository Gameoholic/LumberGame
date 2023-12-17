package xyz.gameoholic.lumbergame.commands;

import fr.mrmicky.fastboard.adventure.FastBoard;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.gameoholic.lumbergame.LumberGamePlugin;
import xyz.gameoholic.lumbergame.game.mob.Mob;
import xyz.gameoholic.lumbergame.game.wave.WaveManager;
import xyz.gameoholic.lumbergame.util.NMSUtil;

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

// Set the title
        board.updateTitle(text("Lumber Game").color(NamedTextColor.GOLD));
        board.updateLines(
            MiniMessage.miniMessage().deserialize("<>"),
            text("Gameoholic_").color(NamedTextColor.AQUA),
            text("test2").color(NamedTextColor.AQUA),
            text("test3").color(NamedTextColor.AQUA)
        );

        return false;
    }
}
