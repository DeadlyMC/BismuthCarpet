package si.bismuth.scoreboard.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.overlay.PlayerTabOverlay;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.text.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import si.bismuth.scoreboard.IScoreboard;

@Mixin(PlayerTabOverlay.class)
public class PlayerTabOverlayMixin {
    @ModifyVariable(method = "renderDisplayScore", at = @At("STORE"), ordinal = 1)
    private String injected(String original, @Local(argsOnly = true) ScoreboardObjective displayObjective, @Local(ordinal = 0, argsOnly = true) String owner) {
        IScoreboard scoreboard = (IScoreboard) displayObjective.getScoreboard();
        return Formatting.YELLOW + "" + scoreboard.bismuthServer$getLongScore(owner, displayObjective);
    }
}
