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
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;
import xyz.gameoholic.lumbergame.game.data.PlayerData;

import javax.annotation.Nullable;
import java.util.UUID;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class MongoDBUtil {
    private static String connectionURI = "mongodb://localhost:27017";
    private static String databaseName = "lumberDefenseDB";

    private static MongoClient getClient() {
        ConnectionString connectionString = new ConnectionString(connectionURI);
        CodecRegistry pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));
        MongoClientSettings clientSettings = MongoClientSettings.builder()
                .codecRegistry(pojoCodecRegistry)
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
        } catch (Exception e) {
            //todo error message in plugin's info e.getMessage()
            return null;
        }
    }

    /**
     * Uploads the player data of the player. Increments all existing data with the provided data, except for maxWaveCompleted (max operator'd).
     *
     * @param playerData The new player data.
     * @return Whether the operation was successful.
     */
    public static boolean uploadPlayerData(PlayerData playerData) {
        try (MongoClient client = getClient()) { // try-with-resources calls .close() at the end regardless of outcome
            MongoDatabase db = client.getDatabase(databaseName);
            MongoCollection<PlayerData> players = db.getCollection("players", PlayerData.class);

            if (getPlayerData(playerData.getUuid()) == null) // Ensure document for player data exists, create one if doesn't
                throw new MongoException("Couldn't get player data document");

            Bson filter = Filters.eq("uuid", playerData.getUuid());
            Bson update = Updates.combine(
                    Updates.inc("wins", playerData.getWins()),
                    Updates.inc("losses", playerData.getLosses()),
                    Updates.inc("wavesCompleted", playerData.getWavesCompleted()),
                    Updates.inc("kills", playerData.getKills()),
                    Updates.inc("deaths", playerData.getDeaths()),
                    Updates.inc("ironCollected", playerData.getIronCollected()),
                    Updates.inc("goldCollected", playerData.getGoldCollected()),
                    Updates.inc("woodCollected", playerData.getWoodCollected()),
                    Updates.max("maxWaveCompleted", playerData.getMaxWaveCompleted())
            );

            players.updateOne(filter, update);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

}
