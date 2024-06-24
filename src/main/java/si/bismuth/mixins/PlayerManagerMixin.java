package si.bismuth.mixins;

import net.minecraft.entity.Entity;
import net.minecraft.network.Connection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.server.network.handler.ServerPlayNetworkHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import si.bismuth.BismuthServer;
import si.bismuth.patches.FakeServerPlayerEntity;
import si.bismuth.patches.FakeServerPlayNetworkHandler;
import si.bismuth.utils.ScoreboardHelper;

import java.util.Arrays;
import java.util.List;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
	private ServerPlayerEntity mycopy;

	@Inject(method = "onLogin", at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/server/PlayerManager;load(Lnet/minecraft/server/entity/living/player/ServerPlayerEntity;)Lnet/minecraft/nbt/NbtCompound;"))
	private void onLogin(Connection connection, ServerPlayerEntity player, CallbackInfo ci) {
		if (player instanceof FakeServerPlayerEntity) {
			((FakeServerPlayerEntity) player).resetToSetPosition();
		}
	}

	@Redirect(method = "onLogin", at = @At(value = "NEW", target = "(Lnet/minecraft/server/MinecraftServer;Lnet/minecraft/network/Connection;Lnet/minecraft/server/entity/living/player/ServerPlayerEntity;)Lnet/minecraft/server/network/handler/ServerPlayNetworkHandler;"))
	private ServerPlayNetworkHandler replaceNetworkHandlerForFakePlayers(MinecraftServer server, Connection connection, ServerPlayerEntity player) {
		return player instanceof FakeServerPlayerEntity ? new FakeServerPlayNetworkHandler(server, connection, player) : new ServerPlayNetworkHandler(server, connection, player);
	}

	@Redirect(method = "createForLogin", at = @At(value = "FIELD", target = "Lnet/minecraft/server/entity/living/player/ServerPlayerEntity;networkHandler:Lnet/minecraft/server/network/handler/ServerPlayNetworkHandler;"))
	private ServerPlayNetworkHandler copyVariable(ServerPlayerEntity player) {
		this.mycopy = player;
		return player.networkHandler;
	}

	@Redirect(method = "createForLogin", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/handler/ServerPlayNetworkHandler;sendDisconnect(Lnet/minecraft/text/Text;)V"))
	private void handleFakePlayerJoin(ServerPlayNetworkHandler networkHandler, Text message) {
		if (this.mycopy instanceof FakeServerPlayerEntity) {
			this.mycopy.discard();
		} else {
			this.mycopy.networkHandler.sendDisconnect(new TranslatableText("multiplayer.disconnect.duplicate_login"));
		}
	}

	@Inject(method = "teleportEntityToDimension", at = @At(value = "INVOKE", shift = At.Shift.BEFORE, ordinal = 0, target = "Lnet/minecraft/util/profiler/Profiler;pop()V"))
	private void onTeleportEntityToDimension(Entity entity, int lastDimension, ServerWorld oldWorld, ServerWorld newWorld, CallbackInfo ci) {
		if (entity.isLoaded && ((IServerWorld) oldWorld).callIsChunkLoadedAt(entity.chunkX, entity.chunkZ, true)) {
			oldWorld.getChunkAt(entity.chunkX, entity.chunkZ).removeEntity(entity, entity.chunkY);
		}
	}

	@Inject(method = "sendMessage(Lnet/minecraft/text/Text;Z)V", at = @At("HEAD"))
	private void onSendMessage(Text component, boolean isSystem, CallbackInfo ci) {
		if (!isSystem) {
			final String text = component.getString().replaceFirst("^<(\\S*?)>", "\u02F9`$1`\u02FC");
			BismuthServer.bot.sendToDiscord(text);
			final List<String> args = Arrays.asList(text.split(" "));
			if (args.size() > 1 && args.get(1).equals(";s")) {
				ScoreboardHelper.setScoreboard(args, 1);
			} else if (args.size() > 1 && args.get(1).equals(";t")) {
				ScoreboardHelper.setScoreboard(args, 0);
			}
		}
	}

	@Inject(method = "save", at = @At("HEAD"), cancellable = true)
	private void cancelSaveForFakePlayers(ServerPlayerEntity player, CallbackInfo ci){
		if (player instanceof FakeServerPlayerEntity){
			ci.cancel();
		}
	}
}
