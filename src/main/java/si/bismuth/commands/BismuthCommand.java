package si.bismuth.commands;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.AbstractCommand;
import net.minecraft.server.command.source.CommandSource;

abstract class BismuthCommand extends AbstractCommand {
	@Override
	public boolean canUse(MinecraftServer server, CommandSource source) {
		return true;
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 0;
	}
}
