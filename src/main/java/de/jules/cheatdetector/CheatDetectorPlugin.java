package de.jules.cheatdetector;

import com.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import org.bukkit.plugin.java.JavaPlugin;

public final class CheatDetectorPlugin extends JavaPlugin {

    private static CheatDetectorPlugin instance;
    private ConfigManager configManager;
    private ActionManager actionManager;

    @Override
    public void onLoad() {
        instance = this;
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().getSettings().checkForUpdates(false).bStats(false);
        PacketEvents.getAPI().load();
    }

    @Override
    public void onEnable() {
        // Initialize managers and load the configuration
        configManager = new ConfigManager(getDataFolder(), getLogger(), getResource("config.toml"));
        actionManager = new ActionManager(this);
        configManager.loadConfig();

        PacketEvents.getAPI().init();

        // Register packet listener
        PacketEvents.getAPI().getEventManager().registerListener(new BrandPacketListener());
    }

    @Override
    public void onDisable() {
        PacketEvents.getAPI().terminate();
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public ActionManager getActionManager() {
        return actionManager;
    }

    public static CheatDetectorPlugin getInstance() {
        return instance;
    }
}
