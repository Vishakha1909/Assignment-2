/**
 * Project: Game Hub
 * File: SlidingRules.java
 * Purpose: Validity, apply, and terminal logic for sliding puzzle.
 */
package puzzles.sliding;

import game.core.Position;
import game.core.Rules;

public final class SlidingRules implements Rules<SlidingState, SlideAction> {
    private final GoalStrategy goal;

    public SlidingRules() { this(new StandardGoal()); }
    public SlidingRules(GoalStrategy goal) { this.goal = goal; }

    @Override public boolean isTerminal(SlidingState s) { return goal.isGoal(s); }

    @Override public boolean isValid(SlidingState s, SlideAction a) {
        if (a.tileValue <= 0 || a.tileValue >= s.board.rows()*s.board.cols()) return false;
        Position tp = findTile(s, a.tileValue);
        if (tp == null) return false;
        int dist = Math.abs(tp.r - s.empty.r) + Math.abs(tp.c - s.empty.c);
        return dist == 1;
    }

    @Override public SlidingState apply(SlidingState s, SlideAction a) {
        Position tp = findTile(s, a.tileValue);
        SlidingTile tmp = s.board.get(tp.r, tp.c);
        s.board.set(tp.r, tp.c, s.board.get(s.empty.r, s.empty.c));
        s.board.set(s.empty.r, s.empty.c, tmp);
        return new SlidingState(s.board, tp, s.moves + 1);
    }

    @Override public String validationError(SlidingState s, SlideAction a) {
        if (a.tileValue <= 0 || a.tileValue >= s.board.rows()*s.board.cols())
            return "Enter a tile between 1 and " + (s.board.rows()*s.board.cols() - 1) + ".";
        Position tp = findTile(s, a.tileValue);
        if (tp == null) return "Tile not found.";
        int dist = Math.abs(tp.r - s.empty.r) + Math.abs(tp.c - s.empty.c);
        if (dist != 1) return "Tile is not adjacent to the blank.";
        return "Invalid move.";
    }

    private Position findTile(SlidingState s, int value) {
        for (int r=0;r<s.board.rows();r++)
            for (int c=0;c<s.board.cols();c++)
                if (s.board.get(r,c).value()==value) return new Position(r,c);
        return null;
    }
}
