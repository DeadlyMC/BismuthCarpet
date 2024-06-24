package si.bismuth.scoreboard.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalLongRef;
import net.minecraft.scoreboard.ScoreboardScore;
import net.minecraft.server.command.ScoreboardCommand;
import net.minecraft.server.command.exception.CommandException;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import si.bismuth.scoreboard.IScoreboardScore;

import static net.minecraft.server.command.AbstractCommand.parseLong;

@Mixin(ScoreboardCommand.class)
public class ScoreboardCommandMixin {
    @WrapOperation(method = "setScore", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/command/ScoreboardCommand;parseInt(Ljava/lang/String;)I"))
    private int parse1(String string, Operation<Integer> original, @Share("long") LocalLongRef localLongRef) throws CommandException {
        long value = parseLong(string);
        localLongRef.set(value);

        // Calling original leads to not valid int exception if we want to parse a long so we cast to an int
        return (int) value;
    }

    @WrapOperation(method = "setScore", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/command/ScoreboardCommand;parseInt(Ljava/lang/String;I)I"))
    private int parse2(String string, int i, Operation<Integer> original, @Share("long") LocalLongRef localLongRef) throws CommandException {
        long value = parseLong(string, i, Long.MAX_VALUE);
        localLongRef.set(value);

        // Calling original leads to not valid int exception if we want to parse a long so we cast to an int
        return (int) value;
    }

    @Redirect(method = "setScore", at = @At(value = "INVOKE", target = "Lnet/minecraft/scoreboard/ScoreboardScore;set(I)V"))
    private void setScore(ScoreboardScore instance, int score, @Share("long") LocalLongRef localLongRef) {
        ((IScoreboardScore) instance).bismuthServer$setLongScore(localLongRef.get());
    }

    @Redirect(method = "setScore", at = @At(value = "INVOKE", target = "Lnet/minecraft/scoreboard/ScoreboardScore;increase(I)V"))
    private void increase(ScoreboardScore instance, int amount, @Share("long") LocalLongRef localLongRef) {
        ((IScoreboardScore) instance).bismuthServer$longIncrease(localLongRef.get());
    }

    @Redirect(method = "setScore", at = @At(value = "INVOKE", target = "Lnet/minecraft/scoreboard/ScoreboardScore;decrease(I)V"))
    private void decrease(ScoreboardScore instance, int amount, @Share("long") LocalLongRef localLongRef) {
        ((IScoreboardScore) instance).bismuthServer$longDecrease(localLongRef.get());
    }

    @ModifyArg(method = "setScore", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/command/ScoreboardCommand;sendSuccess(Lnet/minecraft/server/command/source/CommandSource;Lnet/minecraft/server/command/Command;Ljava/lang/String;[Ljava/lang/Object;)V"), index = 3)
    private Object[] get(Object[] par4, @Local ScoreboardScore scoreboardScore) {
        par4[2] = ((IScoreboardScore) scoreboardScore).bismuthServer$getLongScore();
        return par4;
    }

    @Redirect(method = "modifyScore", at = @At(value = "INVOKE", target = "Lnet/minecraft/scoreboard/ScoreboardScore;set(I)V", ordinal = 0))
    private void modifyScorePlus(ScoreboardScore instance, int score, @Local(ordinal = 0) ScoreboardScore scoreboardScore, @Local(ordinal = 1) ScoreboardScore scoreboardScore2) {
        long score1 = ((IScoreboardScore) scoreboardScore).bismuthServer$getLongScore();
        long score2 = ((IScoreboardScore) scoreboardScore2).bismuthServer$getLongScore();
        ((IScoreboardScore) scoreboardScore).bismuthServer$setLongScore(score1 + score2);
    }

    @Redirect(method = "modifyScore", at = @At(value = "INVOKE", target = "Lnet/minecraft/scoreboard/ScoreboardScore;set(I)V", ordinal = 1))
    private void modifyScoreMin(ScoreboardScore instance, int score, @Local(ordinal = 0) ScoreboardScore scoreboardScore, @Local(ordinal = 1) ScoreboardScore scoreboardScore2) {
        long score1 = ((IScoreboardScore) scoreboardScore).bismuthServer$getLongScore();
        long score2 = ((IScoreboardScore) scoreboardScore2).bismuthServer$getLongScore();
        ((IScoreboardScore) scoreboardScore).bismuthServer$setLongScore(score1 - score2);
    }

    @Redirect(method = "modifyScore", at = @At(value = "INVOKE", target = "Lnet/minecraft/scoreboard/ScoreboardScore;set(I)V", ordinal = 2))
    private void modifyScoreMult(ScoreboardScore instance, int score, @Local(ordinal = 0) ScoreboardScore scoreboardScore, @Local(ordinal = 1) ScoreboardScore scoreboardScore2) {
        long score1 = ((IScoreboardScore) scoreboardScore).bismuthServer$getLongScore();
        long score2 = ((IScoreboardScore) scoreboardScore2).bismuthServer$getLongScore();
        ((IScoreboardScore) scoreboardScore).bismuthServer$setLongScore(score1 * score2);
    }

    @Redirect(method = "modifyScore", at = @At(value = "INVOKE", target = "Lnet/minecraft/scoreboard/ScoreboardScore;set(I)V", ordinal = 3))
    private void modifyScoreDiv(ScoreboardScore instance, int score, @Local(ordinal = 0) ScoreboardScore scoreboardScore, @Local(ordinal = 1) ScoreboardScore scoreboardScore2) {
        long score1 = ((IScoreboardScore) scoreboardScore).bismuthServer$getLongScore();
        long score2 = ((IScoreboardScore) scoreboardScore2).bismuthServer$getLongScore();
        ((IScoreboardScore) scoreboardScore).bismuthServer$setLongScore(score1 / score2);
    }

    @Redirect(method = "modifyScore", at = @At(value = "INVOKE", target = "Lnet/minecraft/scoreboard/ScoreboardScore;set(I)V", ordinal = 4))
    private void modifyScoreMod(ScoreboardScore instance, int score, @Local(ordinal = 0) ScoreboardScore scoreboardScore, @Local(ordinal = 1) ScoreboardScore scoreboardScore2) {
        long score1 = ((IScoreboardScore) scoreboardScore).bismuthServer$getLongScore();
        long score2 = ((IScoreboardScore) scoreboardScore2).bismuthServer$getLongScore();
        ((IScoreboardScore) scoreboardScore).bismuthServer$setLongScore(score1 % score2);
    }

    @Redirect(method = "modifyScore", at = @At(value = "INVOKE", target = "Lnet/minecraft/scoreboard/ScoreboardScore;set(I)V", ordinal = 5))
    private void modifyScoreEq(ScoreboardScore instance, int score, @Local(ordinal = 0) ScoreboardScore scoreboardScore, @Local(ordinal = 1) ScoreboardScore scoreboardScore2) {
        long score2 = ((IScoreboardScore) scoreboardScore2).bismuthServer$getLongScore();
        ((IScoreboardScore) scoreboardScore).bismuthServer$setLongScore(score2);
    }

    @Redirect(method = "modifyScore", at = @At(value = "INVOKE", target = "Lnet/minecraft/scoreboard/ScoreboardScore;set(I)V", ordinal = 6))
    private void modifyScoreLess(ScoreboardScore instance, int score, @Local(ordinal = 0) ScoreboardScore scoreboardScore, @Local(ordinal = 1) ScoreboardScore scoreboardScore2) {
        long score1 = ((IScoreboardScore) scoreboardScore).bismuthServer$getLongScore();
        long score2 = ((IScoreboardScore) scoreboardScore2).bismuthServer$getLongScore();
        ((IScoreboardScore) scoreboardScore).bismuthServer$setLongScore(Math.min(score1, score2));
    }

    @Redirect(method = "modifyScore", at = @At(value = "INVOKE", target = "Lnet/minecraft/scoreboard/ScoreboardScore;set(I)V", ordinal = 7))
    private void modifyScoreGreater(ScoreboardScore instance, int score, @Local(ordinal = 0) ScoreboardScore scoreboardScore, @Local(ordinal = 1) ScoreboardScore scoreboardScore2) {
        long score1 = ((IScoreboardScore) scoreboardScore).bismuthServer$getLongScore();
        long score2 = ((IScoreboardScore) scoreboardScore2).bismuthServer$getLongScore();
        ((IScoreboardScore) scoreboardScore).bismuthServer$setLongScore(Math.max(score1, score2));
    }

    @Redirect(method = "modifyScore", at = @At(value = "INVOKE", target = "Lnet/minecraft/scoreboard/ScoreboardScore;set(I)V", ordinal = 8))
    private void modifyScoreWeirdOp(ScoreboardScore instance, int score, @Local(ordinal = 0) ScoreboardScore scoreboardScore, @Local(ordinal = 1) ScoreboardScore scoreboardScore2) {
        long score1 = ((IScoreboardScore) scoreboardScore).bismuthServer$getLongScore();
        long score2 = ((IScoreboardScore) scoreboardScore2).bismuthServer$getLongScore();
        ((IScoreboardScore) scoreboardScore).bismuthServer$setLongScore(score2);
        ((IScoreboardScore) scoreboardScore2).bismuthServer$setLongScore(score1);
    }
}
