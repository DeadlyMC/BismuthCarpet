package si.bismuth.mixins;

import net.minecraft.block.PumpkinBlock;
import net.minecraft.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PumpkinBlock.class)
public abstract class MixinBlockPumpkin {
	@Redirect(method = "canSurvive", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/state/BlockState;isFullBlock()Z"))
	private boolean onPlaceFenceGate(BlockState state) {
		return true;
	}
}
