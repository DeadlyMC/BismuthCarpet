package si.bismuth.scoreboard.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalLongRef;
import net.minecraft.scoreboard.ScoreboardScore;
import net.minecraft.server.command.TriggerCommand;
import net.minecraft.server.command.exception.CommandException;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import si.bismuth.scoreboard.IScoreboardScore;

import static net.minecraft.server.command.AbstractCommand.parseLong;

@Mixin(TriggerCommand.class)
public class TriggerCommandMixin {
    @WrapOperation(method = "run", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/command/TriggerCommand;parseInt(Ljava/lang/String;)I"))
    private int parse(String string, Operation<Integer> original, @Share("long") LocalLongRef localLongRef) throws CommandException {
        long value = parseLong(string);
        localLongRef.set(value);

        // Calling original leads to not valid int exception if we want to parse a long so we cast to an int
        return (int) value;
    }

    @Redirect(method = "run", at = @At(value = "INVOKE", target = "Lnet/minecraft/scoreboard/ScoreboardScore;set(I)V"))
    private void run(ScoreboardScore instance, int score, @Share("long") LocalLongRef localLongRef) {
        ((IScoreboardScore) instance).bismuthServer$setLongScore(localLongRef.get());
    }

    @Redirect(method = "run", at = @At(value = "INVOKE", target = "Lnet/minecraft/scoreboard/ScoreboardScore;increase(I)V"))
    private void increase(ScoreboardScore instance, int amount, @Share("long") LocalLongRef localLongRef) {
        ((IScoreboardScore) instance).bismuthServer$longIncrease(localLongRef.get());
    }
}
