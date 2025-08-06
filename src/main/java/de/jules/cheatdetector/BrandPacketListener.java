package de.jules.cheatdetector;

import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPluginMessage;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

public class BrandPacketListener extends PacketListenerAbstract {

    private final PlayerManager playerManager = CheatDetectorPlugin.getInstance().getPlayerManager();
    private final DetectionEngine detectionEngine = new DetectionEngine();
    private final Logger logger = CheatDetectorPlugin.getInstance().getLogger();

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() != PacketType.Play.Client.PLUGIN_MESSAGE) {
            return;
        }

        Player player = (Player) event.getPlayer();
        if (player == null) return;

        PlayerProfile profile = playerManager.getProfile(player);
        if (profile == null) return;

        WrapperPlayClientPluginMessage packet = new WrapperPlayClientPluginMessage(event);
        String channelName = packet.getChannelName();

        if (channelName.equalsIgnoreCase("minecraft:register") || channelName.equalsIgnoreCase("REGISTER")) {
            for (String channel : new String(packet.getData(), StandardCharsets.UTF_8).split("\0")) {
                profile.addChannel(channel);
            }
            return;
        }

        if (channelName.equalsIgnoreCase("minecraft:brand") || channelName.equalsIgnoreCase("MC|Brand")) {
            String brand = readString(packet.getData());
            logger.info("Detected brand for player " + player.getName() + ": '" + brand + "'");
            detectionEngine.checkPlayer(player, brand, profile.getRegisteredChannels());
        }
    }

    private String readString(byte[] data) {
        try {
            int varIntLength = 0;
            int bytesRead = 0;
            int value = 0;
            byte currentByte;

            do {
                currentByte = data[bytesRead];
                value |= (currentByte & 0x7F) << (varIntLength++ * 7);
                if (varIntLength > 5) {
                    throw new IOException("VarInt too big");
                }
            } while ((currentByte & 0x80) == 0x80 && ++bytesRead < data.length);

            bytesRead++; // Move past the last byte of the VarInt

            int stringLength = value;
            if (bytesRead + stringLength > data.length) {
                 // Fallback if the parsing is wrong or packet is malformed
                return new String(data, StandardCharsets.UTF_8).trim().replaceAll("\\x00", "");
            }

            return new String(data, bytesRead, stringLength, StandardCharsets.UTF_8);
        } catch (Exception e) {
            // Fallback for any parsing error
            return new String(data, StandardCharsets.UTF_8).trim().replaceAll("\\x00", "");
        }
    }
}
