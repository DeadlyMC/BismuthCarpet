package si.bismuth.mixins;

import net.minecraft.advancement.PlayerAdvancements;
import net.minecraft.server.PlayerManager;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import si.bismuth.BismuthServer;

@Mixin(PlayerAdvancements.class)
public class PlayerAdvancementsMixin {
	@Redirect(method = "award", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;sendSystemMessage(Lnet/minecraft/text/Text;)V"))
	private void onSendAdvancement(PlayerManager manager, Text message) {
		manager.sendSystemMessage(message);
		BismuthServer.bot.sendAdvancementMessage(message);
	}
}
