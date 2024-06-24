package si.bismuth.commands;

import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.exception.CommandException;
import net.minecraft.server.command.source.CommandSource;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class DisplayItemCommand extends BismuthCommand {
	@Override
	public String getName() {
		return "displayitem";
	}

	@Override
	public String getUsage(CommandSource source) {
		return this.getName();
	}

	@Override
	public void run(MinecraftServer server, CommandSource source, String[] args) throws CommandException {
		if (!(source instanceof ServerPlayerEntity)) {
			throw new CommandException("Unknown " + source.getName() + " tried to run " + this.getName() + "!");
		}

		final ItemStack stack = ((ServerPlayerEntity) source).getMainHandStack();
		if (!stack.isEmpty()) {
			final Text component = new TranslatableText("chat.type.text", source.getDisplayName(), stack.getDisplayName());
			server.getPlayerManager().sendMessage(component, false);
		}
	}
}
