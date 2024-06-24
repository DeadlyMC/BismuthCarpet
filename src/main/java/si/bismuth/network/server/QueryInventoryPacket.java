package si.bismuth.network.server;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import si.bismuth.network.BisPacket;

import java.io.IOException;

public class QueryInventoryPacket implements BisPacket {

	private BlockPos pos;

	public QueryInventoryPacket() {
	}

	public QueryInventoryPacket(BlockPos pos) {
		this.pos = pos;
	}

	@Override
	public void read(PacketByteBuf buffer) throws IOException {
		this.pos = buffer.readBlockPos();
	}

	@Override
	public void write(PacketByteBuf buffer) throws IOException {
		buffer.writeBlockPos(this.pos);
	}

	@Override
	public String getChannel() {
		return "Bis|InvQuery";
	}

	public BlockPos getPos() {
		return this.pos;
	}
}
