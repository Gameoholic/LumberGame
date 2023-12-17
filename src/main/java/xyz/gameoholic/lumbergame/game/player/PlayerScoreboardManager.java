package xyz.gameoholic.lumbergame.game.player;

import fr.mrmicky.fastboard.adventure.FastBoard;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.util.RGBLike;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import xyz.gameoholic.lumbergame.LumberGamePlugin;
import xyz.gameoholic.lumbergame.util.ColorUtil;

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
                lines.add(MiniMessage.miniMessage().deserialize(
                    line,
                    Placeholder.component("wave", text(plugin.getGameManager().getWaveNumber() + 1)),
                    Placeholder.component("alive_mobs", text(plugin.getGameManager().getWaveManager().getAliveMobsSize())),
                    Placeholder.component("tree_health_percentage", getTreeHealthPercentageComponent()),
                    Placeholder.component("tree_health", text(plugin.getGameManager().getTreeManager().getHealth())),
                    Placeholder.component("tree_max_health", text(plugin.getGameManager().getTreeManager().getMaxHealth()))
                ))
        );
        board.updateLines(lines);
    }
    private Component getTreeHealthPercentageComponent() {
        int healthRatioPercentage = plugin.getGameManager().getTreeManager().getHealthToMaxHealthRatio();
        return text(healthRatioPercentage).color(ColorUtil.getGreenRedColor(healthRatioPercentage / 100.0));
    }

    /**
     * Deletes the FastBoard instance.
     */
    public void delete() {
        board.delete();
    }
}
