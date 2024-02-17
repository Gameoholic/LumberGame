package xyz.gameoholic.lumbergame.game.player;

import fr.mrmicky.fastboard.adventure.FastBoard;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.gameoholic.lumbergame.LumberGamePlugin;

import java.util.ArrayList;
import java.util.Arrays;
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

        // Get updated inventory item count
        int boneMeal = 0;
        int wood = 0;
        int gold = 0;
        int iron = 0;
        List<ItemStack> playerItems = new ArrayList(Arrays.asList(player.getInventory().getContents()));
        playerItems.add(player.getItemOnCursor()); // Fix item on cursor not being counted
        for (ItemStack itemStack : playerItems) {
            if (itemStack != null)
                switch (itemStack.getType()) {
                    case BONE_MEAL -> boneMeal += itemStack.getAmount();
                    case OAK_WOOD -> wood += itemStack.getAmount();
                    case GOLD_INGOT -> gold += itemStack.getAmount();
                    case IRON_INGOT -> iron += itemStack.getAmount();
                }
        }

        List<Component> lines = new ArrayList<>();
        for (String line : plugin.getLumberConfig().strings().scoreboardLines()) {
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
                    Placeholder.component("bone_meal", text(boneMeal)),
                    Placeholder.component("wood", text(wood)),
                    Placeholder.component("gold_amount", text(gold)),
                    Placeholder.component("iron", text(iron))
            ));
        }


        // Add line for every other player, X lines before the last line (margin)
        for (LumberPlayer otherLumberPlayer : plugin.getGameManager().getPlayers().stream().filter(
                filteredPlayer -> !filteredPlayer.getUuid().equals(player.getUniqueId())).toList()
        ) {
            Player otherPlayer = Bukkit.getPlayer(otherLumberPlayer.getUuid());
            Component otherPlayerHealth = text("N/A").color(NamedTextColor.RED);
            if (otherPlayer != null)
                otherPlayerHealth = text((int) Math.max(otherPlayer.getHealth(), 1));

            // Get updated inventory item count
            int otherPlayerBoneMeal = 0;
            int otherPlayerWood = 0;
            int otherPlayerGold = 0;
            int otherPlayerIron = 0;
            List<ItemStack> otherPlayerItems = new ArrayList(Arrays.asList(otherPlayer.getInventory().getContents()));
            otherPlayerItems.add(otherPlayer.getItemOnCursor()); // Fix item on cursor not being counted
            for (ItemStack itemStack : otherPlayerItems) {
                if (itemStack != null)
                    switch (itemStack.getType()) {
                        case BONE_MEAL -> otherPlayerBoneMeal += itemStack.getAmount();
                        case OAK_WOOD -> otherPlayerWood += itemStack.getAmount();
                        case GOLD_INGOT -> otherPlayerGold += itemStack.getAmount();
                        case IRON_INGOT -> otherPlayerIron += itemStack.getAmount();
                    }
            }

            lines.add(lines.size() - plugin.getLumberConfig().gameConfig().scoreboardPlayerLineMargin(),
                    MiniMessage.miniMessage().deserialize(
                            plugin.getLumberConfig().strings().playerScoreboardLine(),
                            Placeholder.parsed("health_fraction", String.valueOf(
                                    otherPlayer.getHealth() / otherPlayer.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue())
                            ),
                            Placeholder.component("player", otherPlayer.name()),
                            Placeholder.component("health", otherPlayerHealth),
                            Placeholder.component("bone_meal", text(otherPlayerBoneMeal)),
                            Placeholder.component("wood", text(otherPlayerWood)),
                            Placeholder.component("gold_amount", text(otherPlayerGold)),
                            Placeholder.component("iron", text(otherPlayerIron))
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
