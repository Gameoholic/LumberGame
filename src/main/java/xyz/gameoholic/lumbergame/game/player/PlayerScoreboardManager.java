package xyz.gameoholic.lumbergame.game.player;

import fr.mrmicky.fastboard.adventure.FastBoard;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import xyz.gameoholic.lumbergame.LumberGamePlugin;

import static net.kyori.adventure.text.Component.text;

/**
 * Handles the scoreboard for the player.
 */
public class PlayerScoreboardManager {
    private LumberGamePlugin plugin;
    private FastBoard board;
    public PlayerScoreboardManager(LumberGamePlugin plugin, Player player) {
        this.plugin = plugin;
        this.board = new FastBoard(player);

        board.delete();

        update();
    }

    public void update() {
        board.updateTitle(text("Lumber Game").color(NamedTextColor.GOLD));
        board.updateLines(
            MiniMessage.miniMessage().deserialize("Wave <color:#f08000>1</color>"),
            text("Gameoholic_").color(NamedTextColor.AQUA),
            text("test2").color(NamedTextColor.AQUA),
            text("test3").color(NamedTextColor.AQUA)
        );
    }

    /**
     * Deletes the FastBoard instance.
     */
    public void delete() {
        board.delete();
    }
}
