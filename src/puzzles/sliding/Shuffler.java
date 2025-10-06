/**
 * Project: Game Hub
 * File: Shuffler.java
 * Purpose: Strategy for scrambling a solvable board.
 */
package puzzles.sliding;

public interface Shuffler {
    /**
     * Produce a scrambled (still solvable) state from a starting state.
     * @param start a valid state
     * @param steps how many random legal moves to perform
     * @return a new state (moves reset to 0)
     */
    SlidingState shuffle(SlidingState start, int steps);
}
