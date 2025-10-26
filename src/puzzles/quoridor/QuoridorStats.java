package puzzles.quoridor;

import java.util.concurrent.atomic.AtomicInteger;

final class QuoridorStats {
    private static final QuoridorStats INSTANCE = new QuoridorStats();
    static QuoridorStats get() { return INSTANCE; }

    private final AtomicInteger games = new AtomicInteger();
    private final AtomicInteger winsA = new AtomicInteger();
    private final AtomicInteger winsB = new AtomicInteger();
    private final AtomicInteger totalMoves = new AtomicInteger();
    private final AtomicInteger totalWalls = new AtomicInteger();
    private volatile int bestFewestMoves = Integer.MAX_VALUE;
    private volatile long totalDurationMs = 0L;

    void onGameStart() { games.incrementAndGet(); }
    void onWallPlaced() { totalWalls.incrementAndGet(); }
    void onGameEnd(boolean aWon, int moves, long durMs) {
        (aWon ? winsA : winsB).incrementAndGet();
        totalMoves.addAndGet(moves);
        totalDurationMs += durMs;
        if (moves < bestFewestMoves) bestFewestMoves = moves;
    }
    String summary() {
        int g = games.get(), a = winsA.get(), b = winsB.get();
        double avgMoves = g > 0 ? (double) totalMoves.get()/g : 0;
        double avgSecs  = g > 0 ? (totalDurationMs/1000.0)/g : 0;
        String best = bestFewestMoves == Integer.MAX_VALUE ? "—" : String.valueOf(bestFewestMoves);
        return "Games: " + g + "\nWins  : A=" + a + "  B=" + b +
               "\nMoves : total=" + totalMoves.get() + ", avg/game=" + String.format("%.2f", avgMoves) +
               "\nWalls : total=" + totalWalls.get() +
               "\nBest  : fewest moves to win = " + best +
               "\nTime  : avg duration = " + String.format("%.2fs", avgSecs);
    }
}
