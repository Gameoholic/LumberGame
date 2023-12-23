package xyz.gameoholic.lumbergame.commands;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.network.protocol.game.ClientboundAddPlayerPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.profile.PlayerTextures;
import org.jetbrains.annotations.NotNull;
import xyz.gameoholic.lumbergame.LumberGamePlugin;
import xyz.gameoholic.lumbergame.TreeDeadParticle;
import xyz.gameoholic.lumbergame.config.ConfigParser;
import xyz.gameoholic.lumbergame.util.NMSUtil;

import java.util.*;

import static net.kyori.adventure.text.Component.text;


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

        Player player = (Player) sender;
        OfflinePlayer p = Bukkit.getOfflinePlayer(args[0]); //Bukkit Player

        // merchant texture: ewogICJ0aW1lc3RhbXAiIDogMTcwMzM1ODg0MjQ5OCwKICAicHJvZmlsZUlkIiA6ICIyOTlhZTlhNDQ2NDk0OTMxYjM2NzZiYWVmNGI0MWUyZCIsCiAgInByb2ZpbGVOYW1lIiA6ICJBY3R1YWxNZXJjaGFudCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9kYzJlYWQ5NzIyMjc3YjdiMTM5MjM5NzhhMGU2M2Y2ODBjMTc5OGZjZDg5MmI0MDk2OWNlNmZhZmUxM2QzM2JmIgogICAgfQogIH0KfQ==
        // merchant signature: eYLUKe/NGUU+kwGSA4IjZebvjDEFu1YJNyEeuzXJnf/Vbv1serk7GRJ9ArwwW4nfh+67+Wz3Lwcc7rLy2Ymgl4ECcNc3qW+J8Wc1O+1sAdd5KexXx0CpXnO9HIfzTm5UykEJGow7MVGNkvMdElXl0gi3Rs/iWjo2JNhdgkEhWWVn00eW9qOriuA2LrfVk1GZKx5/uMSWjQ6Y7WRU2j6waYTzfgxMAc9fU9jMGUApymiX9Vk5MUDVt671PURYnm0nhIzXgTrxDJqt+Ly6iKo+efdCvXuDXTbGK3wOyRL9IUy0D0XWNpF4CyabKFQ0h1LA7osIjB4uG3JkDXi3a+mIWWfKJI9F6P6EzhFBUAcyqZs4PeM0NvWNR/SnO3cxY8MJB4Bpc7uGqykjlXEPTXOXR50zmNfhZqR43am6/RnDqtrdx7fv5frJnL7QzEln24vwEQbH2ckFy5WgY6QUochUTqnM/rGqY8yn3nwdn0NvTxg0sVWlMNU4vbe68JXQv5bFKsLyG/Ij+zUyTneiFR4Zjo2Ph6ahkrGjJr4dVYLLVDGaY3vTVAPDHTk69W3Jy3bGaVrRpMNJp5Uw//fSykcUaXAXM7p+qFsHvQpBBl1Y4oaKkTKTLX2CSEaU/TuIHRbAs2suZszmtRs/zuDuMPb24bO/6GtbZKcZW7rXUmDVwsg=
        NMSUtil.spawnNPC(plugin, player, player.getLocation(), p);



        return false;
    }
}
