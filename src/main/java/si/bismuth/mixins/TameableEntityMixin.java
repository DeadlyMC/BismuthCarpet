package si.bismuth.mixins;

import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.mob.passive.animal.tamable.TameableEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import si.bismuth.BismuthServer;

@Mixin(TameableEntity.class)
public class TameableEntityMixin {
	@Redirect(method = "onKilled", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/living/LivingEntity;sendMessage(Lnet/minecraft/text/Text;)V"))
	private void sendMessage(LivingEntity entity, Text message) {
		BismuthServer.server.getPlayerManager().sendSystemMessage(message);
		BismuthServer.bot.sendDeathMessage(message);
	}
}
