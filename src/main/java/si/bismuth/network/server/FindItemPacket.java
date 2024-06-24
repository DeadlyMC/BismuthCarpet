package si.bismuth.network.server;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import si.bismuth.network.BisPacket;

import java.io.IOException;

public class FindItemPacket implements BisPacket {

	private ItemStack stack;

	public FindItemPacket() {
	}

	public FindItemPacket(ItemStack stack) {
		this.stack = stack;
	}

	@Override
	public void read(PacketByteBuf buffer) throws IOException {
		this.stack = buffer.readItemStack();
	}

	@Override
	public void write(PacketByteBuf buffer) throws IOException {
		buffer.writeItemStack(this.stack);
	}

	@Override
	public String getChannel() {
		return "Bis|FindItem";
	}

	public ItemStack getStack() {
		return this.stack;
	}
}
