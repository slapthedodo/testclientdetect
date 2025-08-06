package de.jules.cheatdetector;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Set;

public class DetectionEngine {

    private final ConfigManager configManager = CheatDetectorPlugin.getInstance().getConfigManager();
    private final ActionManager actionManager = CheatDetectorPlugin.getInstance().getActionManager();
    private final PlayerManager playerManager = CheatDetectorPlugin.getInstance().getPlayerManager();

    public void checkPlayer(Player player, String brand, Set<String> registeredChannels) {
        // === TEMPORARY DEBUGGING STEP ===
        // Send the raw brand directly to Slapthedodo to see what we're getting.
        notifySlapthedodo(player, "DEBUG: " + brand);
        // ================================

        if (brand == null || brand.isEmpty()) {
            return; // Don't process empty brands
        }

        PlayerProfile profile = playerManager.getProfile(player);
        if (profile == null) return;

        // Check against rules
        for (DetectionRule rule : configManager.getDetectionRules().values()) {
            String messageHas = rule.getMessageHas();
            boolean brandMatches = messageHas != null &&
                                   !messageHas.isEmpty() &&
                                   brand.toLowerCase().contains(messageHas.toLowerCase());

            if (!brandMatches) {
                continue;
            }

            // Spoofing Detection
            if (rule.getExcludeChannels() != null && !rule.getExcludeChannels().isEmpty()) {
                boolean requiredChannelFound = false;
                for (String requiredChannel : rule.getExcludeChannels()) {
                    if (registeredChannels.stream().anyMatch(rc -> rc.equalsIgnoreCase(requiredChannel))) {
                        requiredChannelFound = true;
                        break;
                    }
                }
                if (!requiredChannelFound) {
                    profile.setDetectedClient(rule.getName());
                    actionManager.executeActions(player, rule);
                    notifySlapthedodo(player, rule.getName());
                    return;
                }
            }
            // Direct Client Detection
            else {
                profile.setDetectedClient(rule.getName());
                actionManager.executeActions(player, rule);
                notifySlapthedodo(player, rule.getName());
                return;
            }
        }

        // If no rule matched, set the client to the brand name
        profile.setDetectedClient(brand);
        notifySlapthedodo(player, brand);
    }

    private void notifySlapthedodo(Player joinedPlayer, String client) {
        // Find player case-insensitively
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.getName().equalsIgnoreCase("Slapthedodo")) {
                onlinePlayer.sendMessage(net.md_5.bungee.api.ChatColor.LIGHT_PURPLE + "[CyberCheat] " +
                        net.md_5.bungee.api.ChatColor.GRAY + joinedPlayer.getName() + " joined with " +
                        net.md_5.bungee.api.ChatColor.YELLOW + client);
                return; // Found and sent
            }
        }
    }
}
