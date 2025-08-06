package de.jules.cheatdetector;

import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPluginMessage;
import org.bukkit.entity.Player;

import java.nio.charset.StandardCharsets;

public class BrandPacketListener extends PacketListenerAbstract {

    private final CheatDetectorPlugin plugin = CheatDetectorPlugin.getInstance();

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() != PacketType.Play.Client.PLUGIN_MESSAGE) {
            return;
        }

        Player player = (Player) event.getPlayer();
        if (player == null) {
            return;
        }

        WrapperPlayClientPluginMessage packet = new WrapperPlayClientPluginMessage(event);
        String channelName = packet.getChannelName();
        byte[] data = packet.getData();
        String message = new String(data, StandardCharsets.UTF_8);

        for (DetectionRule rule : plugin.getConfigManager().getDetectionRules().values()) {
            // Check if the channel matches one of the rule's channels
            boolean channelMatch = rule.getChannels().stream()
                    .anyMatch(ch -> ch.equalsIgnoreCase(channelName));

            if (channelMatch) {
                // Check if the message contains the specified string
                if (message.toLowerCase().contains(rule.getMessageHas().toLowerCase())) {
                    // Execute the configured actions for this rule
                    plugin.getActionManager().executeActions(player, rule);
                }
            }
        }
    }
}
