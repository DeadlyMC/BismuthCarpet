package si.bismuth.mixins;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.SayCommand;
import net.minecraft.server.command.source.CommandSource;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import si.bismuth.BismuthServer;

@Mixin(SayCommand.class)
public abstract class SayCommandMixin {
	@Inject(method = "run", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;sendSystemMessage(Lnet/minecraft/text/Text;)V"), locals = LocalCapture.CAPTURE_FAILHARD)
	private void onSay(MinecraftServer server, CommandSource source, String[] args, CallbackInfo ci, Text message) {
		final Text text = new TranslatableText("chat.type.announcement", source.getDisplayName(), message);
		BismuthServer.bot.sendToDiscord(text.getString());
	}
}
