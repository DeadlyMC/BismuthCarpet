package si.bismuth.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.crafting.CraftingManager;

import si.bismuth.utils.BismuthRecipeManager;

@Mixin(CraftingManager.class)
public class CraftingManagerMixin {
	@Inject(
		method = "init",
		cancellable = true,
		at = @At(
			value = "HEAD"
		)
	)
	private static void bismuthServer$registerRecipes(CallbackInfoReturnable<Boolean> cir) {
		try {
			BismuthRecipeManager.init();
		} catch (Throwable t) {
			cir.setReturnValue(false);
		}
	}
}
