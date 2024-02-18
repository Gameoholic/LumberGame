package xyz.gameoholic.lumbergame.commands;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import org.bson.UuidRepresentation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Display;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import xyz.gameoholic.lumbergame.LumberGamePlugin;
import xyz.gameoholic.lumbergame.config.ConfigParser;

import static com.mongodb.client.model.Filters.eq;
import static net.kyori.adventure.text.Component.text;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import xyz.gameoholic.lumbergame.game.data.PlayerData;
import xyz.gameoholic.lumbergame.util.MongoDBUtil;

import java.util.UUID;

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

        Location loc = plugin.getLumberConfig().mapConfig().goldVaultLocation();

        Location textLoc = loc.clone();
        textLoc.setY(loc.getY() + 2);
        Bukkit.broadcastMessage(textLoc + "");
        TextDisplay textDisplay = (TextDisplay) textLoc.getWorld().spawnEntity(textLoc, EntityType.TEXT_DISPLAY);
        textDisplay.text(text("asdasdasdasds"));
        textDisplay.setBillboard(Display.Billboard.VERTICAL);
        textDisplay.getPersistentDataContainer().set(new NamespacedKey(plugin, "lumber_mob"), PersistentDataType.BOOLEAN, true);


        return false;
    }


}
