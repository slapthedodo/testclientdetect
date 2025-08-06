package de.jules.cheatdetector;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerConnectionListener implements Listener {

    private final CheatDetectorPlugin plugin;
    private final PlayerManager playerManager;

    public PlayerConnectionListener(CheatDetectorPlugin plugin) {
        this.plugin = plugin;
        this.playerManager = plugin.getPlayerManager();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        playerManager.addPlayer(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        playerManager.removePlayer(event.getPlayer());
    }
}
