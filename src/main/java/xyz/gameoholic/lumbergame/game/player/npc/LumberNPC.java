package xyz.gameoholic.lumbergame.game.player.npc;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import xyz.gameoholic.lumbergame.LumberGamePlugin;
import xyz.gameoholic.lumbergame.util.NMSUtil;

import javax.annotation.Nullable;
import java.util.UUID;

abstract class LumberNPC {

    protected final LumberGamePlugin plugin;
    private final String MERCHANT_TEXTURE = "ewogICJ0aW1lc3RhbXAiIDogMTcwMzM1ODg0MjQ5OCwKICAicHJvZmlsZUlkIiA6ICIyOTlhZTlhNDQ2NDk0OTMxYjM2NzZiYWVmNGI0MWUyZCIsCiAgInByb2ZpbGVOYW1lIiA6ICJBY3R1YWxNZXJjaGFudCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9kYzJlYWQ5NzIyMjc3YjdiMTM5MjM5NzhhMGU2M2Y2ODBjMTc5OGZjZDg5MmI0MDk2OWNlNmZhZmUxM2QzM2JmIgogICAgfQogIH0KfQ==";
    private final String MERCHANT_SIGNATURE = "eYLUKe/NGUU+kwGSA4IjZebvjDEFu1YJNyEeuzXJnf/Vbv1serk7GRJ9ArwwW4nfh+67+Wz3Lwcc7rLy2Ymgl4ECcNc3qW+J8Wc1O+1sAdd5KexXx0CpXnO9HIfzTm5UykEJGow7MVGNkvMdElXl0gi3Rs/iWjo2JNhdgkEhWWVn00eW9qOriuA2LrfVk1GZKx5/uMSWjQ6Y7WRU2j6waYTzfgxMAc9fU9jMGUApymiX9Vk5MUDVt671PURYnm0nhIzXgTrxDJqt+Ly6iKo+efdCvXuDXTbGK3wOyRL9IUy0D0XWNpF4CyabKFQ0h1LA7osIjB4uG3JkDXi3a+mIWWfKJI9F6P6EzhFBUAcyqZs4PeM0NvWNR/SnO3cxY8MJB4Bpc7uGqykjlXEPTXOXR50zmNfhZqR43am6/RnDqtrdx7fv5frJnL7QzEln24vwEQbH2ckFy5WgY6QUochUTqnM/rGqY8yn3nwdn0NvTxg0sVWlMNU4vbe68JXQv5bFKsLyG/Ij+zUyTneiFR4Zjo2Ph6ahkrGjJr4dVYLLVDGaY3vTVAPDHTk69W3Jy3bGaVrRpMNJp5Uw//fSykcUaXAXM7p+qFsHvQpBBl1Y4oaKkTKTLX2CSEaU/TuIHRbAs2suZszmtRs/zuDuMPb24bO/6GtbZKcZW7rXUmDVwsg=";

    protected final UUID playerUUID;
    private final Location NPCLocation;
    private final String skin;
    private final String signature;
    private int entityId;
    public LumberNPC(LumberGamePlugin plugin, UUID playerUUID, Location NPCLocation) {
        this.plugin = plugin;
        this.playerUUID = playerUUID;
        this.NPCLocation = NPCLocation;
        this.skin = MERCHANT_TEXTURE;
        this.signature = MERCHANT_SIGNATURE;
    }

    /**
     * Removes the NPC
     */
    public void remove() {
        @Nullable Player player = Bukkit.getPlayer(playerUUID);
        if (player != null)
            NMSUtil.removeNPC(player, entityId);
    }

    /**
     * Spawns the NPC for the player
     */
    public void spawn() {
        remove(); // If was spawned before
        @Nullable Player player = Bukkit.getPlayer(playerUUID);
        if (player != null)
            entityId = NMSUtil.spawnNPC(player, NPCLocation, skin, signature);
    }

    //todo fix 4 interactions on right xlixck

    /**
     * On player interact with the NPC.
     * @param isAttack Whether the interaction was an attack or not.
     */
    abstract public void onInteract(boolean isAttack);

    public int getEntityId() {
        return entityId;
    }


}
