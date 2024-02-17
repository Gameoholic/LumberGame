package xyz.gameoholic.lumbergame.game.data;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.UuidRepresentation;
import org.bson.conversions.Bson;
import org.bukkit.entity.Player;
import xyz.gameoholic.lumbergame.LumberGamePlugin;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Manages player data.
 */
public class PlayerDataManager {

    private LumberGamePlugin plugin;
    private List<PlayerData> cachedPlayerData = new ArrayList<>();
    public PlayerDataManager(LumberGamePlugin plugin) {
        this.plugin = plugin;
    }
    public void test() {

    }
}
