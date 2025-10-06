/**
 * Project: Game Hub
 * File: BoxPiece.java
 * Purpose: Optional piece wrapper for a claimed box (useful for tests/UIs).
 */
package puzzles.dots;

import game.core.Piece;

public final class BoxPiece implements Piece {
    private final int id;     // r*cols + c
    private final char owner; // 'A','B', or 0 if unclaimed
    public BoxPiece(int id, char owner){ this.id=id; this.owner=owner; }
    @Override public int id(){ return id; }
    @Override public String label(){ return owner==0 ? " " : String.valueOf(owner); }
}
