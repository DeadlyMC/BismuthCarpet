package si.bismuth.network.server;

import net.minecraft.network.PacketByteBuf;
import si.bismuth.network.BisPacket;

import java.io.IOException;

public class SortPacket implements BisPacket {

	private boolean isPlayerInv;

	public SortPacket() {
	}

	public SortPacket(boolean isPlayerInv) {
		this.isPlayerInv = isPlayerInv;
	}

	@Override
	public void read(PacketByteBuf buffer) throws IOException {
		this.isPlayerInv = buffer.readBoolean();
	}

	@Override
	public void write(PacketByteBuf buffer) throws IOException {
		buffer.writeBoolean(this.isPlayerInv);
	}

	@Override
	public String getChannel() {
		return "Bis|Sort";
	}

	public boolean isPlayerInv() {
		return isPlayerInv;
	}
}
