package si.bismuth.network.client;

import net.ornithemc.osl.networking.api.client.ClientPlayNetworking;
import si.bismuth.BismuthServer;
import si.bismuth.network.BisPacket;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ClientNetworking {
	private final Set<String> allChannels = new HashSet<>();

	public ClientNetworking() {
		this.registerListener(InventoryContentsPacket::new, ClientNetworkHandler::handleInventoryContents);
		this.registerListener(ItemLocationsPacket::new, ClientNetworkHandler::handleItemLocation);
	}

	private <T extends BisPacket> void registerListener(Supplier<T> initializer, Consumer<T> packetHandler) {
		BisPacket p = initializer.get();
		String channel = p.getChannel();

		if (this.allChannels.contains(channel)) {
			BismuthServer.log.error("Attempted to register packet '{}' on channel '{}' but it already exists!", p.getClass().getSimpleName(), channel);
		} else {
			this.allChannels.add(channel);

			ClientPlayNetworking.registerListener(channel, initializer, (minecraft, handler, packet) -> {
				packetHandler.accept(packet);
				return true;
			});
		}
	}

	public void sendPacket(BisPacket packet) {
		ClientPlayNetworking.send(packet.getChannel(), packet);
	}
}
