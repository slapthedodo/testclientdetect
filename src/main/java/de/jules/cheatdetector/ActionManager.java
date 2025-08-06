package de.jules.cheatdetector;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ActionManager {

    private final CheatDetectorPlugin plugin;

    public ActionManager(CheatDetectorPlugin plugin) {
        this.plugin = plugin;
    }

    public void executeActions(Player player, DetectionRule rule) {
        // Ensure all actions are run on the main server thread
        Bukkit.getScheduler().runTask(plugin, () -> {
            for (String action : rule.getActions()) {
                switch (action.toLowerCase()) {
                    case "alert":
                        performAlert(player, rule);
                        break;
                    case "kick":
                        performKick(player, rule);
                        break;
                    case "ban":
                        performBan(player, rule);
                        break;
                }
            }
        });
    }

    private void performAlert(Player player, DetectionRule rule) {
        String alertMessage = ChatColor.RED + "[CheatDetector] " + ChatColor.YELLOW +
                player.getName() + " is suspected of using " + rule.getName() + ".";

        // Log to console
        plugin.getLogger().info(ChatColor.stripColor(alertMessage));

        // Send to online staff
        Bukkit.getOnlinePlayers().forEach(staff -> {
            if (staff.hasPermission("cheatdetector.alerts")) {
                staff.sendMessage(alertMessage);
            }
        });
    }

    private void performKick(Player player, DetectionRule rule) {
        String kickMessage = ChatColor.RED + "You have been kicked.\n" +
                ChatColor.WHITE + "Reason: Unfair Advantage (Cheating)";
        player.kickPlayer(kickMessage);
    }

    private void performBan(Player player, DetectionRule rule) {
        String reason = "Unfair Advantage (Cheating)";
        String command = "ban " + player.getName() + " " + reason;
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
    }
}
