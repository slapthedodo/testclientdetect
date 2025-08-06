package de.jules.cheatdetector;

import java.util.List;
import java.util.Map;

public class DetectionRule {
    private final String clientIdentifier;
    private final String name;
    private final List<String> actions;
    private final List<String> channels;
    private final String messageHas;

    public DetectionRule(String clientIdentifier, Map<String, Object> data) {
        this.clientIdentifier = clientIdentifier;
        this.name = (String) data.get("name");
        this.actions = (List<String>) data.get("actions");
        this.channels = (List<String>) data.get("channels");
        this.messageHas = (String) data.get("message_has");
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
}
