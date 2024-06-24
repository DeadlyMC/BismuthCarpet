package si.bismuth.network.client;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.math.BlockPos;
import si.bismuth.network.BisPacket;

import java.io.IOException;

public class ItemLocationsPacket implements BisPacket {
	private DefaultedList<BlockPos> positions;

	public ItemLocationsPacket() {
	}

	public ItemLocationsPacket(DefaultedList<BlockPos> positions) {
		this.positions = positions;
	}

	@Override
	public void read(PacketByteBuf buffer) throws IOException {
		final int size = buffer.readVarInt();
		this.positions = DefaultedList.of(size, BlockPos.ORIGIN);
		for (int i = 0; i < size; i++) {
			this.positions.set(i, buffer.readBlockPos());
		}
	}

	@Override
	public void write(PacketByteBuf buffer) throws IOException {
		buffer.writeVarInt(this.positions.size());
		for (BlockPos pos : this.positions) {
			buffer.writeBlockPos(pos);
		}
	}

	@Override
	public String getChannel() {
		return "Bis|ItemLoc";
	}

	public DefaultedList<BlockPos> getPositions() {
		return this.positions;
	}
}
