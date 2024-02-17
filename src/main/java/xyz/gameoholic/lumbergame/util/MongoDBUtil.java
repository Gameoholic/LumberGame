package xyz.gameoholic.lumbergame.util;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.UuidRepresentation;
import org.bson.conversions.Bson;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.gameoholic.lumbergame.game.data.PlayerData;

import javax.annotation.Nullable;
import java.util.UUID;

public class MongoDBUtil {
    private static String connectionURI = "mongodb://localhost:27017";
    private static String databaseName = "lumberDefenseDB";

    private static MongoClient getClient() {
        ConnectionString connectionString = new ConnectionString(connectionURI);
        MongoClientSettings clientSettings = MongoClientSettings.builder()
                .uuidRepresentation(UuidRepresentation.STANDARD) // uuid support
                .applyConnectionString(connectionString).build();
        return MongoClients.create(clientSettings);
    }

    /**
     * Gets the player data for a UUID, will create a document in the database if it doesn't exist.
     *
     * @param uuid The player's UUID.
     * @return The player data, or null if the operation failed.
     */
    public static @Nullable PlayerData getPlayerData(UUID uuid) {
        try (MongoClient client = getClient()) { // try-with-resources calls .close() at the end regardless of outcome
            MongoDatabase db = client.getDatabase(databaseName);
            MongoCollection<PlayerData> players = db.getCollection("players", PlayerData.class);
            @Nullable PlayerData playerDocument = players.find(Filters.eq("uuid", uuid)).first();

            if (playerDocument == null) {
                PlayerData defaultPlayerData = PlayerData.getDefault(uuid);
                players.insertOne(defaultPlayerData);
                playerDocument = defaultPlayerData;
            }
            return playerDocument;
        } catch (MongoException me) {
            return null;
        }
    }

    /**
     * Uploads the player data of the player. Increments all existing data with the provided data, except for maxWaveReached.
     *
     * @param playerData The new player data.
     * @return Whether the operation was successful.
     */
    public static boolean uploadPlayerData(PlayerData playerData) {
        try (MongoClient client = getClient()) { // try-with-resources calls .close() at the end regardless of outcome
            MongoDatabase db = client.getDatabase(databaseName);
            MongoCollection<PlayerData> players = db.getCollection("players", PlayerData.class);
            assert getPlayerData(playerData.uuid()) != null; // Ensure document for player data exists, create one if doesn't

            Bson filter = Filters.eq("uuid", playerData.uuid());
            Bson update = Updates.combine(
                    Updates.inc("wins", playerData.wins()),
                    Updates.inc("losses", playerData.losses()),
                    Updates.inc("wavesCompleted", playerData.wavesCompleted()),
                    Updates.inc("kills", playerData.kills()),
                    Updates.inc("deaths", playerData.deaths()),
                    Updates.inc("ironCollected", playerData.ironCollected()),
                    Updates.inc("goldCollected", playerData.goldCollected()),
                    Updates.inc("woodCollected", playerData.woodCollected()),
                    Updates.max("maxWavesReached", playerData.maxWaveReached())
            );
            players.updateOne(filter, update);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

}
