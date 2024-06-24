package si.bismuth.scoreboard.mixins;

import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardScore;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import si.bismuth.scoreboard.IScoreboard;
import si.bismuth.scoreboard.IScoreboardScore;

import java.util.Collection;

import static si.bismuth.utils.ScoreboardHelper.upperScoreboardScorePrefix;

@Mixin(Scoreboard.class)
public abstract class ScoreboardMixin implements IScoreboard {
    @Shadow
    public abstract ScoreboardScore getScore(String owner, ScoreboardObjective objective);

    @Shadow
    public abstract Collection<ScoreboardScore> getScores(ScoreboardObjective objective);

    @Shadow
    public abstract Collection<ScoreboardScore> getScores();

    @Override
    public Long bismuthServer$getLongScore(String owner, ScoreboardObjective objective) {
        IScoreboardScore lower_score = (IScoreboardScore) this.getScore(owner, objective);

        return lower_score.bismuthServer$getLongScore();
    }

    @Override
    public Collection<ScoreboardScore> bismuthServer$getScores(ScoreboardObjective objective) {
        Collection<ScoreboardScore> collection = this.getScores(objective);

        collection.removeIf(scoreboardScore -> scoreboardScore.getOwner().startsWith(upperScoreboardScorePrefix));
        return collection;
    }

    @Override
    public Collection<ScoreboardScore> bismuthServer$getScores() {
        Collection<ScoreboardScore> collection = this.getScores();

        collection.removeIf(scoreboardScore -> scoreboardScore.getOwner().startsWith(upperScoreboardScorePrefix));
        return collection;
    }
}
