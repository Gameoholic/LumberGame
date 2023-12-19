package xyz.gameoholic.lumbergame.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.gameoholic.lumbergame.LumberGamePlugin;

public class SpawnItemCommand implements CommandExecutor {
    private LumberGamePlugin plugin;

    public SpawnItemCommand(LumberGamePlugin plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 2)
            return false;

        if (!(sender instanceof Player))
            return false;

        Player player = (Player) sender;

        ItemStack itemStack = plugin.getItemManager().getItem(args[0]);
        itemStack.setAmount(Integer.parseInt(args[1]));

        player.getInventory().addItem(itemStack);
        return false;
    }
}
