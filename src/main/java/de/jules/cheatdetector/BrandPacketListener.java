package de.jules.cheatdetector;

import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPluginMessage;
import org.bukkit.entity.Player;

import java.nio.charset.StandardCharsets;

public class BrandPacketListener extends PacketListenerAbstract {

    private final PlayerManager playerManager = CheatDetectorPlugin.getInstance().getPlayerManager();
    private final DetectionEngine detectionEngine = new DetectionEngine();

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() != PacketType.Play.Client.PLUGIN_MESSAGE) {
            return;
        }

        Player player = (Player) event.getPlayer();
        if (player == null) return;

        PlayerProfile profile = playerManager.getProfile(player);
        if (profile == null) return; // Should not happen if PlayerConnectionListener is working

        WrapperPlayClientPluginMessage packet = new WrapperPlayClientPluginMessage(event);
        String channelName = packet.getChannelName();

        if (channelName.equalsIgnoreCase("minecraft:register")) {
            for (String channel : new String(packet.getData(), StandardCharsets.UTF_8).split("\0")) {
                profile.addChannel(channel);
            }
        } else if (channelName.equalsIgnoreCase("minecraft:brand")) {
            // The brand is sent after registration, so we can now check.
            String brand = readBrand(packet.getData());
            detectionEngine.checkPlayer(player, brand, profile.getRegisteredChannels());
        }
    }

    private String readBrand(byte[] data) {
        // A simple utility to read a VarInt prefixed string.
        // This is a simplified implementation. A proper one would be more robust.
        try {
            int i = 0;
            int j = 0;
            int k = 0;
            while (true) {
                int b = data[i++];
                j |= (b & 0x7F) << k++ * 7;
                if (k > 5) {
                    throw new RuntimeException("VarInt too big");
                }
                if ((b & 0x80) != 128) {
                    break;
                }
            }
            return new String(data, i, j, StandardCharsets.UTF_8);
        } catch (Exception e) {
            // Fallback for older clients or unexpected formats
            return new String(data, StandardCharsets.UTF_8).trim();
        }
    }
}
