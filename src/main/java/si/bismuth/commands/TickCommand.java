package si.bismuth.commands;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.exception.CommandException;
import net.minecraft.server.command.exception.IncorrectUsageException;
import net.minecraft.server.command.source.CommandSource;
import net.minecraft.util.math.BlockPos;
import si.bismuth.utils.Profiler;

import java.util.Collections;
import java.util.List;

public class TickCommand extends BismuthCommand {
	@Override
	public String getName() {
		return "tick";
	}

	@Override
	public String getUsage(CommandSource source) {
		return "Usage: tick <health|entities> [duration]";
	}

	@Override
	public void run(final MinecraftServer server, final CommandSource source, String[] args) throws CommandException {
		if (args.length == 0) {
			throw new IncorrectUsageException(getUsage(source));
		}

		if ("health".equalsIgnoreCase(args[0])) {
			int step = 100;
			if (args.length > 1) {
				step = parseInt(args[1], 20, 72000);
			}

			Profiler.prepare_tick_report(step);
			return;
		} else if ("entity".equalsIgnoreCase(args[0]) || "entities".equalsIgnoreCase(args[0])) {
			int step = 100;
			if (args.length > 1) {
				step = parseInt(args[1], 20, 72000);
			}

			Profiler.prepare_entity_report(step);
			return;
		}

		throw new IncorrectUsageException(getUsage(source));
	}

	@Override
	public List<String> getSuggestions(MinecraftServer server, CommandSource source, String[] args, BlockPos pos) {
		if (args.length == 1) {
			return suggestMatching(args, "health", "entities");
		}

		if (args.length == 2) {
			if ("health".equalsIgnoreCase(args[0])) {
				return suggestMatching(args, "100", "200", "1000");
			}

			if ("entities".equalsIgnoreCase(args[0])) {
				return suggestMatching(args, "100", "200", "1000");
			}
		}
		return Collections.emptyList();
	}
}
