/**
 * Project: Game Hub
 * File: Position.java
 * Purpose: Immutable (row, col) coordinate.
 */
package game.core;

public final class Position {
    public final int r, c;
    public Position(int r, int c) { this.r = r; this.c = c; }
    public Position add(int dr, int dc) { return new Position(r + dr, c + dc); }
    @Override public String toString() { return "(" + r + "," + c + ")"; }
    @Override public boolean equals(Object o) {
        if (!(o instanceof Position)) return false;
        Position p = (Position)o; return p.r == r && p.c == c;
    }
    @Override public int hashCode() { return (r * 31) ^ c; }
}
