package de.jules.cheatdetector;

import com.moandjiezana.toml.Toml;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import java.util.logging.Logger;

public class ConfigManager {

    private final Map<String, DetectionRule> detectionRules = new HashMap<>();
    private final File dataFolder;
    private final Logger logger;
    private final InputStream defaultConfig;

    public ConfigManager(File dataFolder, Logger logger, InputStream defaultConfig) {
        this.dataFolder = dataFolder;
        this.logger = logger;
        this.defaultConfig = defaultConfig;
    }

    public void loadConfig() {
        File configFile = new File(dataFolder, "config.toml");
        if (!configFile.exists()) {
            dataFolder.mkdirs();
            try {
                if (defaultConfig != null) {
                    Files.copy(defaultConfig, configFile.toPath());
                } else {
                    logger.warning("Could not find default config.toml in JAR.");
                }
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Could not save default config.toml.", e);
            }
        }

        Toml toml = new Toml().read(configFile);
        detectionRules.clear();

        for (Map.Entry<String, Object> entry : toml.entrySet()) {
            String key = entry.getKey();
            if (toml.getTable(key) != null) {
                Map<String, Object> clientData = toml.getTable(key).toMap();
                DetectionRule rule = new DetectionRule(key, clientData);
                detectionRules.put(key, rule);
            }
        }
        logger.info("Loaded " + detectionRules.size() + " cheat detection rules.");
    }

    public Map<String, DetectionRule> getDetectionRules() {
        return detectionRules;
    }
}
