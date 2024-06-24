package si.bismuth.scoreboard;

public interface IScoreboardScore {
    Long bismuthServer$getLongScore();
    void bismuthServer$setLongScore(Long value);

    void bismuthServer$longIncrease(long amount);

    void bismuthServer$longDecrease(long amount);
    void bismuthServer$createUpperScore();
}
