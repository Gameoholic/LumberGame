package xyz.gameoholic.lumbergame.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class EntityExplodeListener implements Listener {
    @EventHandler
    public void onEntityExplodeEvent(org.bukkit.event.entity.EntityExplodeEvent e) {
        e.setCancelled(true);
    }

}
