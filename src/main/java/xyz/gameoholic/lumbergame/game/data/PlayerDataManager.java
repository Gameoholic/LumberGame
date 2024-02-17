package xyz.gameoholic.lumbergame.game.data;

import xyz.gameoholic.lumbergame.LumberGamePlugin;
import xyz.gameoholic.lumbergame.util.MongoDBUtil;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Manages player data.
 */
public class PlayerDataManager {

    private LumberGamePlugin plugin;
    /**
     * Contains player data of this game only. Will later be merged with database data.
     */
    private final List<PlayerData> cachedPlayerData = new ArrayList<>();
    public PlayerDataManager(LumberGamePlugin plugin, Set<UUID> players) {
        this.plugin = plugin;
        players.forEach(playerUUID -> cachedPlayerData.add(PlayerData.getDefault(playerUUID)));
    }

    /**
     * Uploads all cached player data to the database.
     * @return Whether the operation was successful.
     */
    public boolean uploadAllData() {
        for (PlayerData playerData : cachedPlayerData) {
            if (!MongoDBUtil.uploadPlayerData(playerData))
                return false;
        }
        return true;
    }

    /**
     * @return Returns the cached player data matching this uuid, if it exists.
     */
    public @Nullable PlayerData getCachedPlayerData(UUID uuid) {
        return cachedPlayerData.stream().filter( playerData -> playerData.getUuid().equals(uuid)).findFirst().orElse(null); //TODO repalce all == in uuids to equals()
    }

}
