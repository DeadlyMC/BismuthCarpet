package si.bismuth.scoreboard.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardScore;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import si.bismuth.scoreboard.IScoreboardScore;

import static si.bismuth.utils.ScoreboardHelper.getUpperScoreboardScoreName;

@Mixin(ScoreboardScore.class)
public abstract class ScoreboardScoreMixin implements IScoreboardScore {
    @Shadow @Final
    private Scoreboard scoreboard;
    @Shadow @Final
    private ScoreboardObjective objective;
    @Shadow @Final
    private String owner;

    @Unique
    private ScoreboardScore upperScore = null;

    @Shadow
    public abstract int get();

    @Shadow
    public abstract void set(int score);

    @Inject(method = "increase", at = @At("RETURN"))
    private void increaseTotal(int amount, CallbackInfo ci){
        if (!"Total".equals(owner)) {
            ScoreboardScore totalScore = scoreboard.getScore("Total", objective);
            totalScore.increase(amount);
        }
    }

    @Inject(method = "decrease", at = @At("RETURN"))
    private void decreaseTotal(int amount, CallbackInfo ci){
        if (!"Total".equals(owner)) {
            ScoreboardScore totalScore = scoreboard.getScore("Total", objective);
            totalScore.decrease(amount);
        }
    }

    @Redirect(method = "increase", at = @At(value = "INVOKE", target = "Lnet/minecraft/scoreboard/ScoreboardScore;set(I)V"))
    private void increase(ScoreboardScore instance, int score, @Local(argsOnly = true) int amount) {
        this.bismuthServer$setLongScore(this.bismuthServer$getLongScore() + (long) amount);
    }

    @Redirect(method = "decrease", at = @At(value = "INVOKE", target = "Lnet/minecraft/scoreboard/ScoreboardScore;set(I)V"))
    private void decrease(ScoreboardScore instance, int score, @Local(argsOnly = true) int amount) {
        this.bismuthServer$setLongScore(this.bismuthServer$getLongScore() - (long) amount);
    }

    @Inject(method = "setLocked", at = @At("TAIL"))
    private void setLocked(boolean locked, CallbackInfo ci) {
        if (upperScore != null) {
            upperScore.setLocked(locked);
        }
    }

    public Long bismuthServer$getLongScore() {
        int lowerBits = this.get();
        if (upperScore == null) {
            bismuthServer$createUpperScore();
        }
        int higher_bits = upperScore.get();

        return (((long) higher_bits) << 32) | (lowerBits & 0xffffffffL);
    }

    public void bismuthServer$setLongScore(Long value) {
        this.set(value.intValue());

        if (upperScore == null) {
            bismuthServer$createUpperScore();
        }
        upperScore.set((int)(value >> 32));
    }

    public void bismuthServer$createUpperScore() {
        upperScore = this.scoreboard.getScore(getUpperScoreboardScoreName(owner), objective);
    }

    public void bismuthServer$longIncrease(long amount) {
        if (this.objective.getCriterion().isReadOnly()) {
            throw new IllegalStateException("Cannot modify read-only score");
        } else {
            this.bismuthServer$setLongScore(this.bismuthServer$getLongScore() + amount);
            if (!"Total".equals(owner)) {
                ScoreboardScore totalScore = scoreboard.getScore("Total", objective);
                ((IScoreboardScore) totalScore).bismuthServer$longIncrease(amount);
            }
        }
    }

    public void bismuthServer$longDecrease(long amount) {
        if (this.objective.getCriterion().isReadOnly()) {
            throw new IllegalStateException("Cannot modify read-only score");
        } else {
            this.bismuthServer$setLongScore(this.bismuthServer$getLongScore() - amount);
            if (!"Total".equals(owner)) {
                ScoreboardScore totalScore = scoreboard.getScore("Total", objective);
                ((IScoreboardScore) totalScore).bismuthServer$longDecrease(amount);
            }
        }
    }
}
