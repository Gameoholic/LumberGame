package xyz.gameoholic.lumbergame.commands;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.gameoholic.lumbergame.LumberGamePlugin;

import static com.mongodb.client.model.Filters.eq;
import static net.kyori.adventure.text.Component.text;

import xyz.gameoholic.lumbergame.game.data.PlayerData;
import xyz.gameoholic.lumbergame.util.MongoDBUtil;

import javax.annotation.Nullable;


public class StatsCommand implements CommandExecutor {
    private LumberGamePlugin plugin;

    public StatsCommand(LumberGamePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player))
            return false;

        @Nullable PlayerData playerData = MongoDBUtil.getPlayerData(plugin, player.getUniqueId());
        if (playerData == null) {
            // todo: configurable error message
            return true;
        }

        player.sendMessage(MiniMessage.miniMessage().deserialize(plugin.getLumberConfig().strings().statsCommandMessage(),
                Placeholder.component("kills", text(playerData.getKills())),
                Placeholder.component("deaths", text(playerData.getDeaths())),
                Placeholder.component("iron", text(playerData.getIronCollected())),
                Placeholder.component("gold_collected", text(playerData.getGoldCollected())),
                Placeholder.component("wood", text(playerData.getWoodCollected())),
                Placeholder.component("losses", text(playerData.getLosses())),
                Placeholder.component("wins", text(playerData.getWins())),
                Placeholder.component("max_waves_completed", text(playerData.getMaxWaveCompleted() + 1)),
                Placeholder.component("waves_completed", text(playerData.getWavesCompleted()))
        ));
        return false;
    }


}
