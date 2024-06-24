package si.bismuth.network.server;

import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.math.BlockPos;
import si.bismuth.BismuthServer;
import si.bismuth.network.client.InventoryContentsPacket;
import si.bismuth.utils.InventoryHelper;

public class ServerNetworkHandler {
    public static void handleFakeCarpetClientSupport(FakeCarpetClientSupport packet, ServerPlayerEntity player) {
        BismuthServer.networking.sendPacket(player, new FakeCarpetClientSupport());
    }

    public static void handleFindItem(FindItemPacket packet, ServerPlayerEntity player) {
        InventoryHelper.processFindItem(player, packet.getStack());
    }

    public static void handleQueryInventory(QueryInventoryPacket packet, ServerPlayerEntity player) {
        BlockPos pos = packet.getPos();
        final Inventory inventory = HopperBlockEntity.getInventoryAt(player.world, pos.getX(), pos.getY(), pos.getZ());
        // silence inspection since it falsely claims that container cannot be null. :(
        // noinspection ConstantConditions
        if (inventory == null) {
            return;
        }

        final DefaultedList<ItemStack> contents = DefaultedList.of(inventory.getSize(), ItemStack.EMPTY);
        for (int i = 0; i < inventory.getSize(); i++) {
            contents.set(i, inventory.getStack(i));
        }

        BismuthServer.networking.sendPacket(player, new InventoryContentsPacket(contents));
    }

    public static void handleSort(SortPacket packet, ServerPlayerEntity player) {
        if (player.isSpectator()) {
            return;
        }

        if (packet.isPlayerInv()) {
            InventoryHelper.sortInv(player.inventory, 9, 27);
        } else {
            final Inventory inv = player.menu.getSlot(0).inventory;
            InventoryHelper.sortInv(inv, 0, inv.getSize());
        }
    }
}
