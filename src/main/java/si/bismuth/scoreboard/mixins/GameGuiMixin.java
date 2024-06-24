package si.bismuth.scoreboard.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.GameGui;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardScore;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import si.bismuth.scoreboard.IScoreboard;
import si.bismuth.scoreboard.IScoreboardScore;

import java.util.*;

@Mixin(GameGui.class)
public class GameGuiMixin {
    @Redirect(method = "renderScoreboardObjective", at = @At(value = "INVOKE", target = "Lnet/minecraft/scoreboard/Scoreboard;getScores(Lnet/minecraft/scoreboard/ScoreboardObjective;)Ljava/util/Collection;"))
    Collection<ScoreboardScore> renderScoreboardObjective(Scoreboard instance, ScoreboardObjective objective) {
        ArrayList<ScoreboardScore> collection = (ArrayList<ScoreboardScore>) ((IScoreboard) instance).bismuthServer$getScores(objective);
        collection.sort(Comparator.comparing(s -> ((IScoreboardScore) s).bismuthServer$getLongScore()));
        return collection;
    }

    @Redirect(method = "renderScoreboardObjective", at = @At(value = "INVOKE", target = "Ljava/lang/StringBuilder;append(I)Ljava/lang/StringBuilder;", ordinal = 0))
    private StringBuilder append1(StringBuilder instance, int i, @Local ScoreboardScore scoreboardScore) {
        long score = ((IScoreboardScore) scoreboardScore).bismuthServer$getLongScore();
        return instance.append(score);
    }

    @Redirect(method = "renderScoreboardObjective", at = @At(value = "INVOKE", target = "Ljava/lang/StringBuilder;append(I)Ljava/lang/StringBuilder;", ordinal = 1))
    private StringBuilder append2(StringBuilder instance, int i, @Local ScoreboardScore scoreboardScore2) {
        long score = ((IScoreboardScore) scoreboardScore2).bismuthServer$getLongScore();
        return instance.append(score);
    }
}
