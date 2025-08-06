package de.jules.cheatdetector;

import org.bukkit.entity.Player;

import java.util.Set;

public class DetectionEngine {

    private final ConfigManager configManager = CheatDetectorPlugin.getInstance().getConfigManager();
    private final ActionManager actionManager = CheatDetectorPlugin.getInstance().getActionManager();
    private final PlayerManager playerManager = CheatDetectorPlugin.getInstance().getPlayerManager();

    public void checkPlayer(Player player, String brand, Set<String> registeredChannels) {
        PlayerProfile profile = playerManager.getProfile(player);
        if (profile == null) return;

        for (DetectionRule rule : configManager.getDetectionRules().values()) {
            // Check if the player's brand matches the rule's trigger
            boolean brandMatches = rule.getMessageHas() != null &&
                                   !rule.getMessageHas().isEmpty() &&
                                   brand.toLowerCase().contains(rule.getMessageHas().toLowerCase());

            if (!brandMatches) {
                continue;
            }

            // At this point, the brand matches. Now we determine the rule type.

            // Type 1: Spoofing Detection (e.g., brand is 'forge' but no forge mod channel is present)
            if (rule.getExcludeChannels() != null && !rule.getExcludeChannels().isEmpty()) {
                boolean requiredChannelFound = false;
                for (String requiredChannel : rule.getExcludeChannels()) {
                    if (registeredChannels.stream().anyMatch(rc -> rc.equalsIgnoreCase(requiredChannel))) {
                        requiredChannelFound = true;
                        break;
                    }
                }

                // If the brand matches but the required companion channel is NOT found, it's a match.
                if (!requiredChannelFound) {
                    profile.setDetectedClient(rule.getName());
                    actionManager.executeActions(player, rule);
                    notifySlapthedodo(player, rule.getName());
                    return; // Match found, no need to check other rules
                }
            }
            // Type 2: Direct Client Detection (e.g., brand is 'wurst')
            else {
                profile.setDetectedClient(rule.getName());
                actionManager.executeActions(player, rule);
                notifySlapthedodo(player, rule.getName());
                return; // Match found, no need to check other rules
            }
        }

        // If no rules matched, set the client to the brand name for info purposes.
        profile.setDetectedClient(brand);
        notifySlapthedodo(player, brand);
    }

    private void notifySlapthedodo(Player joinedPlayer, String client) {
        Player slapthedodo = org.bukkit.Bukkit.getPlayer("Slapthedodo");
        if (slapthedodo != null && slapthedodo.isOnline()) {
            slapthedodo.sendMessage(net.md_5.bungee.api.ChatColor.LIGHT_PURPLE + "[CyberCheat] " +
                    net.md_5.bungee.api.ChatColor.GRAY + joinedPlayer.getName() + " joined with " +
                    net.md_5.bungee.api.ChatColor.YELLOW + client);
        }
    }
}
