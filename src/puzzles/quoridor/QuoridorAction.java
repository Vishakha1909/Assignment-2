package puzzles.quoridor;

import game.core.Position;

public final class QuoridorAction {
    public enum Type { MOVE, WALL_H, WALL_V }

    public final Type type;
    public final Position from; // for MOVE (current pawn pos), null for walls
    public final Position to;   // for MOVE target cell
    public final int r, c;      // wall anchor (top-left of 2-segment wall)

    private QuoridorAction(Type t, Position from, Position to, int r, int c) {
        this.type = t; this.from = from; this.to = to; this.r = r; this.c = c;
    }
    public static QuoridorAction move(Position from, Position to) {
        return new QuoridorAction(Type.MOVE, from, to, -1, -1);
    }
    public static QuoridorAction wallH(int r, int c) {
        return new QuoridorAction(Type.WALL_H, null, null, r, c);
    }
    public static QuoridorAction wallV(int r, int c) {
        return new QuoridorAction(Type.WALL_V, null, null, r, c);
    }
}
