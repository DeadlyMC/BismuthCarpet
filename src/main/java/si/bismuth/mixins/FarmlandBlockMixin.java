package si.bismuth.mixins;

import net.minecraft.block.Blocks;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.block.state.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Objects;

@Mixin(FarmlandBlock.class)
public class FarmlandBlockMixin {
	/**
	 * @author nessie
	 * @reason Unfixes MC-120444
	 */
	@Overwrite
	public static void setDirt(World world, BlockPos pos) {
		final BlockState state = Blocks.DIRT.defaultState();
		world.setBlockState(pos, state);
		final Box shape = Objects.requireNonNull(state.getCollisionShape(world, pos)).move(pos);

		for (final Entity entity : world.getEntities((Entity) null, shape)) {
			entity.setPosition(entity.x, shape.maxY, entity.z);
		}
	}
}
