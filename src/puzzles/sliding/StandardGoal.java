/**
 * Project: Game Hub
 * File: StandardGoal.java
 * Purpose: 1..N in order with blank at bottom-right.
 */
package puzzles.sliding;

public final class StandardGoal implements GoalStrategy {
    @Override public boolean isGoal(SlidingState s) {
        int k = 1;
        for (int r = 0; r < s.board.rows(); r++) {
            for (int c = 0; c < s.board.cols(); c++) {
                int v = s.board.get(r,c).value();
                if (r == s.board.rows()-1 && c == s.board.cols()-1) return v == 0;
                if (v != k++) return false;
            }
        }
        return true;
    }
}
