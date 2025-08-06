package de.jules.cheatdetector;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CyberCheatCommand implements CommandExecutor {

    private final PlayerManager playerManager;

    public CyberCheatCommand(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;
        openGui(player);
        return true;
    }

    private void openGui(Player player) {
        List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
        int inventorySize = (int) (Math.ceil(onlinePlayers.size() / 9.0) * 9);
        if (inventorySize == 0) inventorySize = 9;

        Inventory gui = Bukkit.createInventory(null, inventorySize, "CyberCheat - Online Players");

        for (Player onlinePlayer : onlinePlayers) {
            ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD, 1);
            SkullMeta skullMeta = (SkullMeta) playerHead.getItemMeta();
            skullMeta.setOwningPlayer(onlinePlayer);
            skullMeta.setDisplayName(ChatColor.YELLOW + onlinePlayer.getName());

            PlayerProfile profile = playerManager.getProfile(onlinePlayer);
            List<String> lore = new ArrayList<>();
            if (profile != null) {
                lore.add(ChatColor.GRAY + "Client: " + ChatColor.WHITE + profile.getDetectedClient());
                lore.add(ChatColor.GRAY + "Registered Channels:");
                List<String> channels = profile.getRegisteredChannels().stream().limit(10).collect(Collectors.toList());
                for (String channel : channels) {
                    lore.add(ChatColor.DARK_GRAY + "- " + ChatColor.AQUA + channel);
                }
                if (profile.getRegisteredChannels().size() > 10) {
                    lore.add(ChatColor.DARK_GRAY + "...and " + (profile.getRegisteredChannels().size() - 10) + " more.");
                }
            } else {
                lore.add(ChatColor.RED + "No profile data available.");
            }
            skullMeta.setLore(lore);
            playerHead.setItemMeta(skullMeta);
            gui.addItem(playerHead);
        }

        player.openInventory(gui);
    }
}
