package si.bismuth.utils;

import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.server.scoreboard.ServerScoreboard;
import org.apache.commons.lang3.StringUtils;
import si.bismuth.BismuthServer;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ScoreboardHelper {
	public static final String upperScoreboardScorePrefix = "$";
	private static ServerScoreboard board;

	public static void init() {
		board = (ServerScoreboard) BismuthServer.server.getWorld(0).getScoreboard();
	}

	public static void setScoreboard(List<String> args, int displaySlot) {
		final ScoreboardObjective objective = ScoreboardHelper.getObjective(args, board);
		board.setDisplayObjective(displaySlot, objective);
	}

	public static ScoreboardObjective getObjective(List<String> args, Scoreboard board) {
		final Collection<String> objectives = board.getObjectives().stream().map(ScoreboardObjective::getName).collect(Collectors.toList());
		return args.size() > 2 ? board.getObjective(getClosestMatch(objectives, args.get(2))) : null;
	}

	public static String getUpperScoreboardScoreName(String name) {
		return upperScoreboardScorePrefix + name;
	}

	private static String getClosestMatch(Collection<String> collection, String target) {
		int distance = Integer.MAX_VALUE;
		String closest = "";
		for (final String s : collection) {
			if (target.equalsIgnoreCase(s)) {
				return s;
			}

			int currentDistance = StringUtils.getLevenshteinDistance(s, target);
			if (currentDistance < distance) {
				distance = currentDistance;
				closest = s;
			}
		}

		return closest;
	}
}
