package puzzles.quoridor;

import java.util.LinkedHashMap;
import java.util.Map;

public final class QuoridorStats {
    private static final QuoridorStats SINGLETON = new QuoridorStats();
    public static QuoridorStats get() { return SINGLETON; }

    private int games = 0;
    private long totalMs = 0;
    private long totalMoves = 0;
    private int totalWalls = 0;
    private int totalJumps = 0;

    private final Map<String,Integer> wins = new LinkedHashMap<String,Integer>();

    private QuoridorStats() {}

    public synchronized void onGameEnd(
            String name1, String name2, String winnerName,
            int moveCount, int wallsP1, int wallsP2,
            int jumps1, int jumps2, long elapsedMs
    ) {
        games++;
        totalMs += elapsedMs;
        totalMoves += moveCount;
        totalWalls += (wallsP1 + wallsP2);
        totalJumps += (jumps1 + jumps2);
        wins.put(winnerName, wins.getOrDefault(winnerName, 0) + 1);
    }

    public synchronized String summary() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n=== Quoridor Session Stats ===\n");
        sb.append("Games: ").append(games).append("\n");
        if (games > 0) {
            sb.append("Avg moves/game: ").append(String.format("%.1f", (double)totalMoves / games)).append("\n");
            sb.append("Avg walls/game: ").append(String.format("%.1f", (double)totalWalls / games)).append("\n");
            sb.append("Avg jumps/game: ").append(String.format("%.1f", (double)totalJumps / games)).append("\n");
            sb.append("Avg duration: ").append(String.format("%.1fs", totalMs / 1000.0 / games)).append("\n");
        }
        sb.append("Wins by player:\n");
        for (Map.Entry<String,Integer> e : wins.entrySet()) {
            sb.append("  ").append(e.getKey()).append(": ").append(e.getValue()).append("\n");
        }
        return sb.toString();
    }

    public synchronized String quickLine() {
    if (games == 0) return "No games played.";
    return "Games: " + games +
           " | Avg moves: " + String.format("%.1f", (double) totalMoves / games) +
           " | Avg walls: " + String.format("%.1f", (double) totalWalls / games) +
           " | Avg jumps: " + String.format("%.1f", (double) totalJumps / games);
}

}
