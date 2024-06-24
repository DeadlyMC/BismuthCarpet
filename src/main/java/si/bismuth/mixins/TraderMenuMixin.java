package si.bismuth.mixins;

import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.inventory.menu.TraderMenu;
import net.minecraft.world.village.trade.TradeOffer;
import net.minecraft.world.village.trade.TradeOffers;
import net.minecraft.world.village.trade.Trader;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(TraderMenu.class)
public class TraderMenuMixin {
	@Unique
	private final Random rand = new Random();

	@Shadow
	@Final
	private Trader trader;

	@Inject(method = "close", at = @At("HEAD"))
	private void unlockOffers(PlayerEntity player, CallbackInfo ci) {
		final TradeOffers offers = this.trader.getOffers(player);
		if (offers == null) {
			return;
		}

		boolean shouldUnlock = true;
		for (TradeOffer offer : offers) {
			if (!offer.isDisabled()) {
				shouldUnlock = false;
			}
		}

		if (shouldUnlock) {
			for (TradeOffer offer : offers) {
				offer.increaseMaxUses(this.rand.nextInt(6) + this.rand.nextInt(6) + 2);
			}
		}
	}
}
