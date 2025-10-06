/**
 * Project: Game Hub
 * File: Piece.java
 * Purpose: Minimal piece abstraction to satisfy rubric and enable reuse.
 */
package game.core;

public interface Piece {
    /** Stable id for equality/tests (e.g., tile number, box index). */
    int id();

    /** Short UI label, e.g., " ", "1", "A". */
    String label();
}
