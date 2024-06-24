package si.bismuth.network.server;

import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.ornithemc.osl.networking.api.server.ServerPlayNetworking;
import si.bismuth.BismuthServer;
import si.bismuth.network.BisPacket;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class ServerNetworking {
	private final Set<String> allChannels = new HashSet<>();

	public ServerNetworking() {
		this.registerListener(QueryInventoryPacket::new, ServerNetworkHandler::handleQueryInventory);
		this.registerListener(FindItemPacket::new, ServerNetworkHandler::handleFindItem);
		this.registerListener(SortPacket::new, ServerNetworkHandler::handleSort);
		this.registerListener(FakeCarpetClientSupport::new, ServerNetworkHandler::handleFakeCarpetClientSupport);
	}

	private <T extends BisPacket> void registerListener(Supplier<T> initializer, BiConsumer<T, ServerPlayerEntity> packetHandler) {
		BisPacket p = initializer.get();
		String channel = p.getChannel();

		if (this.allChannels.contains(channel)) {
			BismuthServer.log.error("Attempted to register packet '{}' on channel '{}' but it already exists!", p.getClass().getSimpleName(), channel);
		} else {
			this.allChannels.add(channel);

			ServerPlayNetworking.registerListener(channel, initializer, (server, handler, player, packet) -> {
				packetHandler.accept(packet, player);
				return true;
			});
		}
	}

	public void sendPacket(ServerPlayerEntity player, BisPacket packet) {
		ServerPlayNetworking.send(player, packet.getChannel(), packet);
	}

	public void sendPacket(Iterable<ServerPlayerEntity> players, BisPacket packet) {
		ServerPlayNetworking.send(players, packet.getChannel(), packet);
	}

	public void sendPacket(int dimension, BisPacket packet) {
		ServerPlayNetworking.send(dimension, packet.getChannel(), packet);
	}

	public void sendPacket(BisPacket packet) {
		ServerPlayNetworking.send(packet.getChannel(), packet);
	}
}
