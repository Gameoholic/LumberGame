package xyz.gameoholic.lumbergame.game.player.perk;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import xyz.gameoholic.lumbergame.DoubleJumpLandParticle;
import xyz.gameoholic.lumbergame.DoubleJumpParticle;
import xyz.gameoholic.lumbergame.LumberGamePlugin;
import xyz.gameoholic.lumbergame.util.ExpressionUtil;
import xyz.gameoholic.partigon.particle.PartigonParticle;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import static xyz.gameoholic.lumbergame.util.ExpressionUtil.evaluateExpression;
import static xyz.gameoholic.lumbergame.util.OtherUtil.intToRoman;

public class DoubleJumpPerk extends Perk implements Listener {
    private final LumberGamePlugin plugin;
    private final UUID playerUUID;
    /**
     * Whether the player is currently midair and has already used their double jump.
     */
    private boolean doubleJumped = false;
    private static final int MIN_LEVEL_FOR_DAMAGE = 2;
    private @Nullable PartigonParticle doubleJumpParticle = null;

    public DoubleJumpPerk(int level, UUID playerUUID, LumberGamePlugin plugin) {
        this.level = level;
        this.playerUUID = playerUUID;
        this.plugin = plugin;

        new BukkitRunnable() {
            @Override
            public void run() {
                onTick();
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    @Override
    public void activate(Player player) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        player.setAllowFlight(true);
    }

    @Override
    public void onGameEnd() {
        PlayerToggleFlightEvent.getHandlerList().unregister(this);
    }

    @EventHandler
    public void onPlayerToggleFlightEvent(PlayerToggleFlightEvent e) {
        if (e.getPlayer().getUniqueId() != playerUUID)
            return;
        if (e.getPlayer().getGameMode() == GameMode.CREATIVE)
            return;
        e.setCancelled(true);
        if (doubleJumped) // Don't allow multiple double jumps until has touched ground
            return;
        e.getPlayer().setVelocity(
            e.getPlayer().getLocation().getDirection().multiply(1.25)
                .setY(0.75)
        );
        doubleJumped = true;
        e.getPlayer().setAllowFlight(false);
        doubleJumpParticle = DoubleJumpParticle.INSTANCE.getParticle(e.getPlayer());
        doubleJumpParticle.start();
    }
    private void onTick() {
        @Nullable Player player = Bukkit.getPlayer(playerUUID);
        if (player == null)
            return;
        if (doubleJumped && player.isOnGround()) { // We should not be using Player#isOnGround, unreliable & susceptible to spoofing
            doubleJumped = false; // Reset flag, allow double jumping again
            player.setAllowFlight(true);
            doubleJumpParticle.stop();
            doubleJumpParticle = null;

            if (level >= MIN_LEVEL_FOR_DAMAGE) {
                DoubleJumpLandParticle.INSTANCE.getParticle(player.getLocation(), getRadius(level)).start();
                // Hurt entities in radius
                Collection<Entity> hurtEntities = player.getLocation()
                    .getNearbyEntities(getRadius(level), getRadius(level), getRadius(level));
                hurtEntities.forEach(entity -> {
                    if (entity instanceof Mob mob) {
                        mob.damage(getDamage(level));
                    }
                });
            }
        }
    }

    @Override
    public void onRespawn(Player player) {
        player.setAllowFlight(true);
    }


    public String getCostExpression() {
        return "LEVEL * LEVEL + 3"; // {1, 2, 3, 4, 5, 6} -> {4, 7, 12, 19, 28, 39}
    }


    @Override
    public int getMaxLevel() {
        return 6;
    }

    @Override
    public String getCurrencyId() {
        return "GOLD";
    }

    @Override
    public PerkType getType() {
        return PerkType.DOUBLE_JUMP;
    }

    private int getDamage(int level) {
        // damage expression: {2, 3, 4, 5, 6} -> {10, 20, 34, 52, 74}
        return (int) evaluateExpression("LEVEL * LEVEL * 2.0 + 2.0", Map.of("LEVEL", (double) level));
    }
    private double getRadius(int level) {
        // radius expression: {2, 3, 4, 5, 6} -> {1.75, 2.5, 3.25, 4.0, 4.75}
        return evaluateExpression("1 + (LEVEL - 1) * 0.75", Map.of("LEVEL", (double) level));
    }

    @Override
    public String getDescription() {
        if (level == 0)
            return "<green>Grants you the ability to double jump.";
        if (level < MIN_LEVEL_FOR_DAMAGE)
            return "<gray>Grants you the ability to double jump.<br><green>When landing, deals <damage> damage,<br><green>in a <radius> block radius."
                .replace("<damage>", getDamage(level + 1) + "")
                .replace("<radius>", getRadius(level + 1) + "");
        if (level == getMaxLevel())
            return "<gray>Grants you the ability to double jump.<br><gray>When landing, deals <damage> damage,<br><gray>in a <radius> block radius."
                .replace("<damage>", getDamage(level) + "")
                .replace("<radius>", getRadius(level) + "");
        return "<gray>Grants you the ability to double jump.<br><gray>When landing, deals <damage> <green>-> <new_damage></green> damage,<br><gray> in a <radius> <green> -> <new_radius></green> block radius."
            .replace("<damage>", getDamage(level) + "")
            .replace("<radius>", getRadius(level) + "")
            .replace("<new_damage>", getDamage(level + 1) + "")
            .replace("<new_radius>", getRadius(level + 1) + "");
    }
}
