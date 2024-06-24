package si.bismuth.network.client;

import net.minecraft.util.math.BlockPos;
import si.bismuth.BismuthClient;
import si.bismuth.client.ParticleShowItems;

import java.util.Random;

public class ClientNetworkHandler {
    private static final Random rng = new Random();
    public static void handleItemLocation(ItemLocationsPacket packet) {
        BismuthClient.minecraft.player.closeMenu();
        for (BlockPos pos : packet.getPositions()) {
            for (int i = 0; i < 20; i++) {
                BismuthClient.minecraft.particleManager.addParticle(new ParticleShowItems(BismuthClient.minecraft.player.world, pos.getX() + rng.nextDouble(), pos.getY() + rng.nextDouble(), pos.getZ() + rng.nextDouble(), 0D, 0D, 0D, 2F));
            }
        }
    }

    public static void handleInventoryContents(InventoryContentsPacket packet) {
        // TODO use data for something ig
    }
}
