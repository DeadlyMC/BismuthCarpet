package si.bismuth.mixins;

import com.mojang.authlib.GameProfile;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EndGatewayBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import si.bismuth.BismuthServer;
import si.bismuth.utils.IRecipeBookItemDuper;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity implements IRecipeBookItemDuper {
	@Shadow
	public abstract boolean isSpectator();

	@Shadow
	public abstract BlockPos getSourceBlockPos();

	@Shadow
	public abstract Entity getCamera();

	public ServerPlayerEntityMixin(World world, GameProfile profile) {
		super(world, profile);
	}

	@Unique
	private int dupe;
	@Unique
	private boolean scanForDuping;

	@Shadow
	public abstract Entity teleportToDimension(int dim);

	@Override
	public void bismuthServer$clearDupeItem() {
		this.dupe = Integer.MIN_VALUE;
	}

	@Override
	public void bismuthServer$dupeItem(int slot) {
		if (this.scanForDuping) {
			this.dupe = slot;
		}
	}

	@Override
	public int bismuthServer$getDupeItem() {
		return this.dupe;
	}

	@Override
	public void bismuthServer$dupeItemScan(boolean s) {
		this.scanForDuping = s;
	}

	@Inject(method = "tick", at = @At("RETURN"))
	private void postTick(CallbackInfo ci) {
		this.bismuthServer$clearDupeItem();
		if (this.isSpectator() && this.getCamera() == this) {
			final BlockPos pos = this.getSourceBlockPos();
			final Block block = this.world.getBlockState(pos).getBlock();
			if (block == Blocks.NETHER_PORTAL) {
				this.onPortalCollision(pos);
			} else if (block == Blocks.END_PORTAL) {
				this.teleportToDimension(1);
			} else if (block == Blocks.END_GATEWAY) {
				final BlockEntity te = this.world.getBlockEntity(pos);
				if (te instanceof EndGatewayBlockEntity) {
					((EndGatewayBlockEntity) te).teleport(this);
				}
			}
		}
	}

	@Redirect(method = "onKilled", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;sendSystemMessage(Lnet/minecraft/text/Text;)V"))
	private void sendMessage(PlayerManager manager, Text message) {
		manager.sendSystemMessage(message);
		BismuthServer.bot.sendDeathMessage(message);
		BismuthServer.log.info("Player {} died at {} {} {} in {}", this.getName(), this.x, this.y, this.z, this.world.dimension.getType().getKey());
	}
}