package de.jules.cheatdetector.bungee;

import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPluginMessage;
import de.jules.cheatdetector.ConfigManager;
import de.jules.cheatdetector.DetectionRule;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.nio.charset.StandardCharsets;

public class BungeePacketListener extends PacketListenerAbstract {

    private final ConfigManager configManager;
    private final BungeeActionManager actionManager;

    public BungeePacketListener(ConfigManager configManager, BungeeActionManager actionManager) {
        this.configManager = configManager;
        this.actionManager = actionManager;
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() != PacketType.Play.Client.PLUGIN_MESSAGE) {
            return;
        }

        User user = event.getUser();
        ProxiedPlayer player = (ProxiedPlayer) event.getPlayer();
        if (player == null) {
            return;
        }

        WrapperPlayClientPluginMessage packet = new WrapperPlayClientPluginMessage(event);
        String channelName = packet.getChannelName();
        byte[] data = packet.getData();
        String message = new String(data, StandardCharsets.UTF_8);

        for (DetectionRule rule : configManager.getDetectionRules().values()) {
            boolean channelMatch = rule.getChannels().stream()
                    .anyMatch(ch -> ch.equalsIgnoreCase(channelName));

            if (channelMatch) {
                if (message.toLowerCase().contains(rule.getMessageHas().toLowerCase())) {
                    actionManager.executeActions(player, rule);
                }
            }
        }
    }
}
