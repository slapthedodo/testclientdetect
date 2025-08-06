package de.jules.cheatdetector;

import org.bukkit.entity.Player;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerManager {

    private final Map<UUID, PlayerProfile> playerProfiles = new ConcurrentHashMap<>();

    public void addPlayer(Player player) {
        playerProfiles.put(player.getUniqueId(), new PlayerProfile(player.getUniqueId()));
    }

    public void removePlayer(Player player) {
        playerProfiles.remove(player.getUniqueId());
    }

    public PlayerProfile getProfile(Player player) {
        return playerProfiles.get(player.getUniqueId());
    }

    public PlayerProfile getProfile(UUID uuid) {
        return playerProfiles.get(uuid);
    }
}
