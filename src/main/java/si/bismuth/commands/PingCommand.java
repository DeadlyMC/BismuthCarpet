package si.bismuth.commands;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.exception.CommandException;
import net.minecraft.server.command.source.CommandSource;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.BlockPos;
import java.util.Collections;
import java.util.List;

public class PingCommand extends BismuthCommand {
	@Override
	public String getName() {
		return "ping";
	}

	@Override
	public String getUsage(CommandSource source) {
		return "ping [player]";
	}

	@Override
	public void run(MinecraftServer server, CommandSource source, String[] args) throws CommandException {
		if (!(source instanceof ServerPlayerEntity)) {
			return;
		}

		ServerPlayerEntity player = asPlayer(source);
		if (args.length > 0) {
			final ServerPlayerEntity name = server.getPlayerManager().get(args[0]);
			if (name != null) {
				player = name;
			}
		}

		source.sendMessage(new LiteralText("Ping of " + player.getName() + " is " + player.ping + "ms."));
	}

	@Override
	public List<String> getSuggestions(MinecraftServer server, CommandSource source, String[] args, BlockPos targetPos) {
		return args.length == 1 ? suggestMatching(args, server.getPlayerNames()) : Collections.emptyList();
	}
}
