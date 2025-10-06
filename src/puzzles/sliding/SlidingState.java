/**
 * Project: Game Hub
 * File: SlidingState.java
 * Purpose: Current board + blank position + move count.
 */
package puzzles.sliding;

import game.core.Position;

public final class SlidingState {
    public final GridBoard board;
    public final Position empty;
    public final int moves;

    public SlidingState(GridBoard board, Position empty, int moves) {
        this.board = board; this.empty = empty; this.moves = moves;
    }
}
