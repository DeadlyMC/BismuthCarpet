package si.bismuth.commands;

import com.google.common.collect.ImmutableList;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtException;
import net.minecraft.nbt.SnbtParser;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.exception.CommandException;
import net.minecraft.server.command.source.CommandSource;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import si.bismuth.utils.InventoryHelper;

import java.util.Collections;
import java.util.List;

public class SearchForItemCommand extends BismuthCommand {
	@Override
	public String getName() {
		return "searchforitem";
	}

	@Override
	public String getUsage(CommandSource source) {
		return "/searchforitem [item] [damage] [nbt]";
	}

	public List<String> getAliases() {
		return ImmutableList.of("find", "finditem", "searchforitem");
	}

	@Override
	public void run(MinecraftServer server, CommandSource source, String[] args) throws CommandException {
		if (!(source instanceof ServerPlayerEntity)) {
			throw new CommandException("Nonplayer " + source + " tried to run /searchforitem!");
		}

		if (args.length == 0) {
			InventoryHelper.processFindItem((ServerPlayerEntity) source, ((ServerPlayerEntity) source).getMainHandStack());
			return;
		}

		final Item item = parseItem(source, args[0]);
		final int damage = args.length >= 2 ? parseInt(args[1]) : 0;
		final ItemStack stack = new ItemStack(item, 1, damage);

		if (args.length >= 3) {
			final String s = parseString(args, 2);

			try {
				stack.setNbt(SnbtParser.parse(s));
			} catch (NbtException exception) {
				throw new CommandException("Data tag parsing failed: " + exception.getMessage());
			}
		}

		InventoryHelper.processFindItem((ServerPlayerEntity) source, stack);
	}

	public List<String> getSuggestions(MinecraftServer server, CommandSource source, String[] args, BlockPos pos) {
		return args.length == 1 ? suggestMatching(args, Item.REGISTRY.keySet()) : Collections.emptyList();
	}
}
