package de.jules.cheatdetector.bungee;

import com.github.retrooper.packetevents.PacketEvents;
import de.jules.cheatdetector.ConfigManager;
import io.github.retrooper.packetevents.bungee.factory.BungeePacketEventsBuilder;
import net.md_5.bungee.api.plugin.Plugin;

public class CheatDetectorBungee extends Plugin {

    private static CheatDetectorBungee instance;
    // We can reuse the Spigot/Paper ConfigManager as it has no platform-specific code.
    // However, for a clean separation, let's imagine a shared 'core' module in a real project.
    // For this implementation, we will need to create Bungee-specific versions of the other classes.
    // Let's assume for now we will create them.

    @Override
    public void onLoad() {
        instance = this;
        PacketEvents.setAPI(BungeePacketEventsBuilder.build(this));
        PacketEvents.getAPI().getSettings().checkForUpdates(false).bStats(false);
        PacketEvents.getAPI().load();
    }

    private ConfigManager configManager;
    private BungeeActionManager actionManager;

    @Override
    public void onEnable() {
        // Initialize managers and load the configuration
        configManager = new ConfigManager(getDataFolder(), getLogger(), getResourceAsStream("config.toml"));
        actionManager = new BungeeActionManager(this);
        configManager.loadConfig();

        PacketEvents.getAPI().init();

        // Register Bungee packet listener
        PacketEvents.getAPI().getEventManager().registerListener(
                new BungeePacketListener(configManager, actionManager)
        );

        getLogger().info("CheatDetector (Bungee) has been enabled.");
    }

    @Override
    public void onDisable() {
        PacketEvents.getAPI().terminate();
    }

    public static CheatDetectorBungee getInstance() {
        return instance;
    }
}
