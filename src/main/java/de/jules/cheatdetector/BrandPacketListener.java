package de.jules.cheatdetector;

import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPluginMessage;
import org.bukkit.entity.Player;

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
            String brand = "unknown";
            try {
                // Try reading the string directly from the buffer. This is a common Netty pattern.
                // The buffer's reader index will be advanced automatically.
                brand = packet.getBuffer().readString();
            } catch (Exception e) {
                logger.warning("Could not read brand for player " + player.getName() + " using buffer.readString(). Error: " + e.getMessage());
                // Fallback to a simple UTF-8 conversion if the buffer read fails.
                brand = new String(packet.getData(), StandardCharsets.UTF_8).trim();
            }

            logger.info("Detected brand for player " + player.getName() + ": '" + brand + "'");
            detectionEngine.checkPlayer(player, brand, profile.getRegisteredChannels());
        }
    }
}
