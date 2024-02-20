package xyz.gameoholic.lumbergame.game.data;

import xyz.gameoholic.lumbergame.LumberGamePlugin;
import xyz.gameoholic.lumbergame.util.MongoDBUtil;

import javax.annotation.Nullable;
import java.util.*;

/**
 * Manages player data.
 */
public class PlayerDataManager {

    private LumberGamePlugin plugin;
    /**
     * Contains player data of this game only. Will later be merged with database data.
     */
    private final Map<UUID, PlayerData> cachedPlayerData = new HashMap<>();
    public PlayerDataManager(LumberGamePlugin plugin, Set<UUID> players) {
        this.plugin = plugin;
        players.forEach(playerUUID -> cachedPlayerData.put(playerUUID, PlayerData.getDefault(playerUUID)));
    }

    /**
     * Uploads all cached player data to the database.
     * @return Whether the operation was successful.
     */
    public boolean uploadAllData() {
        for (Map.Entry<UUID, PlayerData> playerData : cachedPlayerData.entrySet()) {
            if (!MongoDBUtil.uploadPlayerData(playerData.getValue()))
                return false;
        }
        return true;
    }

    /**
     * @return Returns the cached player data matching this uuid, or null if none found.
     */
    public @Nullable PlayerData getCachedPlayerData(UUID uuid) {
        return cachedPlayerData.get(uuid);
    }

}
