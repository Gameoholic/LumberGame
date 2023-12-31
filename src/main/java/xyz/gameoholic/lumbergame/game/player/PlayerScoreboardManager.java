package xyz.gameoholic.lumbergame.game.player;

import fr.mrmicky.fastboard.adventure.FastBoard;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import xyz.gameoholic.lumbergame.LumberGamePlugin;

import java.util.ArrayList;
import java.util.List;

import static net.kyori.adventure.text.Component.text;

/**
 * Handles the scoreboard for the player.
 */
public class PlayerScoreboardManager {
    private final LumberPlayer lumberPlayer;
    private final LumberGamePlugin plugin;
    private final FastBoard board;

    public PlayerScoreboardManager(LumberGamePlugin plugin, Player player, LumberPlayer lumberPlayer) {
        this.plugin = plugin;
        this.board = new FastBoard(player);
        this.lumberPlayer = lumberPlayer;

        update(player);
    }

    public void update(Player player) {
        board.updateTitle(
            MiniMessage.miniMessage().deserialize(plugin.getLumberConfig().strings().scoreboardTitle())
        );

        List<Component> lines = new ArrayList<>();
        plugin.getLumberConfig().strings().scoreboardLines().forEach(
            line ->
                lines.add(MiniMessage.miniMessage().deserialize(
                    line,
                    Placeholder.component("wave", text(plugin.getGameManager().getWaveNumber() + 1)),
                    Placeholder.component("queued_mobs", text(plugin.getGameManager().getWaveManager().getMobQueueSize())),
                    Placeholder.component("alive_mobs", text(plugin.getGameManager().getWaveManager().getAliveMobsSize())),
                    Placeholder.component("tree_health_percentage", text(plugin.getGameManager().getTreeManager()
                        .getHealthToMaxHealthRatio())),
                    Placeholder.parsed("tree_health_fraction", String.valueOf(plugin.getGameManager().getTreeManager()
                        .getHealthToMaxHealthRatio() / 100.0)),
                    Placeholder.component("tree_health", text(plugin.getGameManager().getTreeManager().getHealth())),
                    Placeholder.component("tree_max_health", text(plugin.getGameManager().getTreeManager().getMaxHealth())),
                    Placeholder.component("bone_meal", text(lumberPlayer.getBoneMeal())),
                    Placeholder.component("wood", text(lumberPlayer.getWood())),
                    Placeholder.component("gold_amount", text(lumberPlayer.getGold())),
                    Placeholder.component("iron", text(lumberPlayer.getIron()))
                ))
        );


        // Add line for every other player, X lines before the last line (margin)
        for (LumberPlayer otherLumberPlayer: plugin.getGameManager().getPlayers().stream().filter(
            filteredPlayer -> !filteredPlayer.getUuid().equals(player.getUniqueId())).toList()
        ) {
            Player otherPlayer = Bukkit.getPlayer(otherLumberPlayer.getUuid());
            Component otherPlayerHealth = text("N/A").color(NamedTextColor.RED);
            if (otherPlayer != null)
                otherPlayerHealth = text((int)Math.max(otherPlayer.getHealth(), 1));

            lines.add(lines.size() - plugin.getLumberConfig().gameConfig().scoreboardPlayerLineMargin(),
                MiniMessage.miniMessage().deserialize(
                plugin.getLumberConfig().strings().playerScoreboardLine(),
                Placeholder.parsed("health_fraction", String.valueOf(
                    otherPlayer.getHealth() / otherPlayer.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue())
                ),
                Placeholder.component("player", otherPlayer.name()),
                Placeholder.component("health", otherPlayerHealth),
                Placeholder.component("bone_meal", text(otherLumberPlayer.getBoneMeal())),
                Placeholder.component("wood", text(otherLumberPlayer.getWood())),
                Placeholder.component("gold_amount", text(otherLumberPlayer.getGold())),
                Placeholder.component("iron", text(otherLumberPlayer.getIron()))
            ));
        }

        board.updateLines(lines);
    }

    /**
     * Deletes the FastBoard instance.
     */
    public void delete() {
        board.delete();
    }
}
