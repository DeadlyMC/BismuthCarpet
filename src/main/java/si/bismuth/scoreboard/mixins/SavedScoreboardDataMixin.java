package si.bismuth.scoreboard.mixins;

import com.google.common.collect.Maps;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalLongRef;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.scoreboard.SavedScoreboardData;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardScore;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import si.bismuth.scoreboard.IScoreboard;
import si.bismuth.scoreboard.IScoreboardScore;

import java.util.Collection;
import java.util.Map;

@Mixin(SavedScoreboardData.class)
public abstract class SavedScoreboardDataMixin {
    @Shadow
    private Scoreboard scoreboard;

    @Inject(method = "setScoreboard", at = @At("TAIL"))
    private void updateTotals(CallbackInfo ci) {
        final Map<ScoreboardObjective, Long> totalsMap = Maps.newHashMap();

        for (ScoreboardScore score : ((IScoreboard) scoreboard).bismuthServer$getScores()) {
            if (!"Total".equals(score.getOwner())) {
                totalsMap.put(score.getObjective(), totalsMap.getOrDefault(score.getObjective(), (long) 0) + ((IScoreboardScore) score).bismuthServer$getLongScore());
            }
        }

        for (ScoreboardObjective objective : totalsMap.keySet()){
            long total = totalsMap.get(objective);

            ((IScoreboardScore) scoreboard.getScore("Total", objective)).bismuthServer$setLongScore(total);
        }
    }

    @Redirect(method = "scoresToNbt", at = @At(value = "INVOKE", target = "Lnet/minecraft/scoreboard/Scoreboard;getScores()Ljava/util/Collection;"))
    private Collection<ScoreboardScore> getScores(Scoreboard instance) {
        return ((IScoreboard) instance).bismuthServer$getScores();
    }

    @Redirect(method = "scoresToNbt", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NbtCompound;putInt(Ljava/lang/String;I)V"))
    private void get(NbtCompound instance, String key, int value, @Local ScoreboardScore scoreboardScore) {
        instance.putLong(key, ((IScoreboardScore) scoreboardScore).bismuthServer$getLongScore());
    }

    @Inject(method = "readScoresFromNbt", at = @At(value = "INVOKE", target = "Lnet/minecraft/scoreboard/Scoreboard;getScore(Ljava/lang/String;Lnet/minecraft/scoreboard/ScoreboardObjective;)Lnet/minecraft/scoreboard/ScoreboardScore;"))
    private void LongNBT(NbtList nbt, CallbackInfo ci, @Local NbtCompound nbtCompound, @Share("scoreValue") LocalLongRef argRef) {
        long score = nbtCompound.getLong("Score");
        argRef.set(score);
    }

    @WrapOperation(method = "readScoresFromNbt", at = @At(value = "INVOKE", target = "Lnet/minecraft/scoreboard/ScoreboardScore;set(I)V"))
    private void setScoreboard(ScoreboardScore instance, int score, Operation<Void> original, @Share("scoreValue") LocalLongRef argRef) {
        ((IScoreboardScore) instance).bismuthServer$setLongScore(argRef.get());
    }
}
