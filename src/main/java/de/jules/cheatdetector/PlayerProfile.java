package de.jules.cheatdetector;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerProfile {

    private final UUID playerUUID;
    private final Set<String> registeredChannels = new HashSet<>();
    private String detectedClient = "Vanilla"; // Default to Vanilla

    public PlayerProfile(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public Set<String> getRegisteredChannels() {
        return registeredChannels;
    }

    public void addChannel(String channel) {
        this.registeredChannels.add(channel);
    }

    public String getDetectedClient() {
        return detectedClient;
    }

    public void setDetectedClient(String detectedClient) {
        this.detectedClient = detectedClient;
    }
}
