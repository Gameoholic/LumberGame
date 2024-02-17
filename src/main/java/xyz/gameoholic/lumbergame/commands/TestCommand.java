package xyz.gameoholic.lumbergame.commands;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import org.bson.UuidRepresentation;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
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

        UUID uuid = ((Player)sender).getUniqueId();

        Bukkit.broadcastMessage("" + MongoDBUtil.uploadPlayerData(new PlayerData(uuid, 0,0 ,0,1,1,0,0,0,0)));
        Bukkit.broadcastMessage(MongoDBUtil.getPlayerData(uuid) + "");


        return false;
    }

    public MongoClient mongo() throws Exception {
        ConnectionString connectionString = new ConnectionString("mongodb://localhost:27017/test");
        MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .uuidRepresentation(UuidRepresentation.STANDARD)
                .applyConnectionString(connectionString).build();
        return MongoClients.create(mongoClientSettings);
    }
}
