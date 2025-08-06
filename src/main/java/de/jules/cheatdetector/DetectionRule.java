package de.jules.cheatdetector;

import java.util.List;
import java.util.Map;

public class DetectionRule {
    private final String clientIdentifier;
    private final String name;
    private final List<String> actions;
    private final List<String> channels;
    private final String messageHas;
    private final List<String> excludeChannels;

    @SuppressWarnings("unchecked")
    public DetectionRule(String clientIdentifier, Map<String, Object> data) {
        this.clientIdentifier = clientIdentifier;
        this.name = (String) data.getOrDefault("name", clientIdentifier);
        this.actions = (List<String>) data.getOrDefault("actions", java.util.Collections.emptyList());
        this.channels = (List<String>) data.getOrDefault("channels", java.util.Collections.emptyList());
        this.messageHas = (String) data.get("message_has");
        this.excludeChannels = (List<String>) data.getOrDefault("exclude_channels", java.util.Collections.emptyList());
    }

    public String getClientIdentifier() {
        return clientIdentifier;
    }

    public String getName() {
        return name;
    }

    public List<String> getActions() {
        return actions;
    }

    public List<String> getChannels() {
        return channels;
    }

    public String getMessageHas() {
        return messageHas;
    }

    public List<String> getExcludeChannels() {
        return excludeChannels;
    }
}
