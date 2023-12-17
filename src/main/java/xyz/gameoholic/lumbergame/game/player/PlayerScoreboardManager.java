package xyz.gameoholic.lumbergame.game.player;

import fr.mrmicky.fastboard.adventure.FastBoard;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import xyz.gameoholic.lumbergame.LumberGamePlugin;

import java.util.ArrayList;
import java.util.List;

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

        update();
    }

    public void update() {
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
    }

    /**
     * Deletes the FastBoard instance.
     */
    public void delete() {
        board.delete();
    }
}
