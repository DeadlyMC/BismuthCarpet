package si.bismuth.mixins;

import net.minecraft.entity.XpOrbEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(XpOrbEntity.class)
public interface IEntityXPOrb {
	@Accessor
	void setOrbAge(int age);
}
