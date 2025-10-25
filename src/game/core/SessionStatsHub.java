package game.core;

import java.util.*;

/** Shared per-session stats for all games without changing factory signatures. */
public final class SessionStatsHub {
    private static final SessionStatsHub I = new SessionStatsHub();
    public static SessionStatsHub get() { return I; }

    public static final class Stats {
        public int games, winsA, winsB, ties;
        public int totalMoves, fastestWinMoves = Integer.MAX_VALUE;

        public void onFinish(char winnerMark, int moves) {
            games++; totalMoves += moves;
            if (winnerMark == 'A') winsA++;
            else if (winnerMark == 'B') winsB++;
            else ties++;
            if (winnerMark != 0) fastestWinMoves = Math.min(fastestWinMoves, moves);
        }
        public String summary(String name) {
            double avg = games == 0 ? 0 : ((double) totalMoves / games);
            String fw = (fastestWinMoves == Integer.MAX_VALUE) ? "—" : String.valueOf(fastestWinMoves);
            return String.format("%s → games=%d, A=%d, B=%d, ties=%d, avgMoves=%.1f, fastestWin=%s",
                    name, games, winsA, winsB, ties, avg, fw);
        }
    }

    private final Map<String, Stats> map = new LinkedHashMap<>();
    public Stats forGame(String name) {
        return map.computeIfAbsent(name, k -> new Stats());
    }
    public String summaryAll() {
        StringBuilder sb = new StringBuilder("== Session Stats ==\n");
        for (Map.Entry<String, Stats> e : map.entrySet()) {
            sb.append("  ").append(e.getValue().summary(e.getKey())).append("\n");
        }
        return sb.toString();
    }
}
