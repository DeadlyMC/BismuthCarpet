package si.bismuth.mixins;

import net.minecraft.client.entity.living.player.LocalClientPlayerEntity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LocalClientPlayerEntity.class)
public class LocalClientPlayerEntityMixin {
    @Redirect(method = "tickAi", at = @At(value = "FIELD", target = "Lnet/minecraft/client/entity/living/player/LocalClientPlayerEntity;velocityY:D", opcode = Opcodes.GETFIELD, ordinal = 0))
    private double mc111444(LocalClientPlayerEntity player) {
        return -1D;
    }
}
