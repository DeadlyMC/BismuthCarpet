package si.bismuth.mixins;

import net.minecraft.entity.Entity;
import net.minecraft.entity.living.mob.passive.VillagerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.TitlesS2CPacket;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.server.network.handler.ServerPlayNetworkHandler;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
	@Shadow
	public ServerPlayerEntity player;

	@Redirect(method = "handlePlayerMove", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/entity/living/player/ServerPlayerEntity;isInTeleportationState()Z"))
	private boolean preventPlayerMovedWronglyOrTooQuickly(ServerPlayerEntity player) {
		return true;
	}

	@Redirect(method = "handlePlayerMove", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/handler/ServerPlayNetworkHandler;sendDisconnect(Lnet/minecraft/text/Text;)V"))
	private void debugPlayerBeingKicked(ServerPlayNetworkHandler networkHandler, Text reason) {
		networkHandler.player.addMessage(new LiteralText("If you're using OptiFine F4 or later, disable Fast Math!"), true);
		this.player.teleport(this.player.x, this.player.y, this.player.z);
	}

	@Redirect(method = "handlePlayerMovementAction", at = @At(value = "FIELD", target = "Lnet/minecraft/server/entity/living/player/ServerPlayerEntity;velocityY:D", opcode = Opcodes.GETFIELD))
	private double mc111444(ServerPlayerEntity player) {
		return -1D;
	}

	@Redirect(method = "handleInteractEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/entity/living/player/ServerPlayerEntity;canSee(Lnet/minecraft/entity/Entity;)Z"))
	private boolean mc107103(ServerPlayerEntity player, Entity entity) {
		return true;
	}

	@Redirect(method = "handleInteractEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/entity/living/player/ServerPlayerEntity;interact(Lnet/minecraft/entity/Entity;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;"))
	private InteractionResult onInteractVillager(ServerPlayerEntity player, Entity entity, InteractionHand hand) {
		if (this.player.isSneaking() && entity instanceof VillagerEntity) {
			final VillagerEntity villager = (VillagerEntity) entity;
			final SimpleInventory inventory = villager.getVillagerInventory();
			final StringBuilder builder = new StringBuilder();
			for (int i = 0; i < inventory.getSize(); i++) {
				final ItemStack stack = inventory.getStack(i);
				if (!stack.isEmpty()) {
					builder.append(stack.getSize()).append(" ").append(stack.getHoverName()).append(" ");
				}
			}

			final String output = builder.toString();
			if (!output.isEmpty()) {
				final Text message = new LiteralText(builder.toString());
				final TitlesS2CPacket packetOut = new TitlesS2CPacket(TitlesS2CPacket.Type.ACTIONBAR, message);
				this.player.networkHandler.sendPacket(packetOut);
				return InteractionResult.PASS;
			}
		}

		return player.interact(entity, hand);
	}
}
