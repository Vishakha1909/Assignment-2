/**
 * Project: Game Hub
 * File: Direction.java
 * Purpose: 2D grid directions (row/col deltas).
 */
package game.core;

public enum Direction {
    UP(-1,0), DOWN(1,0), LEFT(0,-1), RIGHT(0,1);
    public final int dr, dc;
    Direction(int dr, int dc) { this.dr = dr; this.dc = dc; }
}
