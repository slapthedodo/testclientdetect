package de.jules.cheatdetector.bungee;

import de.jules.cheatdetector.DetectionRule;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BungeeActionManager {

    private final CheatDetectorBungee plugin;

    public BungeeActionManager(CheatDetectorBungee plugin) {
        this.plugin = plugin;
    }

    public void executeActions(ProxiedPlayer player, DetectionRule rule) {
        // Bungee scheduler is also recommended for thread safety
        plugin.getProxy().getScheduler().runAsync(plugin, () -> {
            for (String action : rule.getActions()) {
                switch (action.toLowerCase()) {
                    case "alert":
                        performAlert(player, rule);
                        break;
                    case "kick":
                        performKick(player, rule);
                        break;
                }
            }
        });
    }

    private void performAlert(ProxiedPlayer player, DetectionRule rule) {
        String alertMessage = ChatColor.RED + "[CheatDetector] " + ChatColor.YELLOW +
                player.getName() + " is suspected of using " + rule.getName() + ".";

        plugin.getLogger().info(alertMessage);

        ProxyServer.getInstance().getPlayers().forEach(staff -> {
            if (staff.hasPermission("cheatdetector.alerts")) {
                staff.sendMessage(new TextComponent(alertMessage));
            }
        });
    }

    private void performKick(ProxiedPlayer player, DetectionRule rule) {
        String kickMessage = ChatColor.RED + "You have been kicked.\n" +
                ChatColor.WHITE + "Reason: Unfair Advantage (Cheating)";
        player.disconnect(new TextComponent(kickMessage));
    }
}
