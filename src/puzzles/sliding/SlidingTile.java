/**
 * Project: Game Hub
 * File: SlidingTile.java
 * Purpose: Puzzle tile (0 = blank). Implements Piece for rubric.
 */
package puzzles.sliding;

import game.core.Piece;

public final class SlidingTile implements Piece {
    private final int value; // 0 => blank

    public SlidingTile(int value) { this.value = value; }

    /** @return numeric value (0 => blank) */
    public int value() { return value; }

    /** @return true if blank */
    public boolean isEmpty() { return value == 0; }

    @Override public int id() { return value; }
    @Override public String label() { return isEmpty() ? " " : Integer.toString(value); }
    @Override public String toString() { return label(); }
}
