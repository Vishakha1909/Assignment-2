package puzzles.quoridor;

import game.core.Position;
import game.core.Rules;

import java.util.ArrayDeque;

public final class QuoridorRules implements Rules<QuoridorState, QuoridorAction> {

    // ----- helpers -----
    private int wallsLeft(QuoridorState s) { return s.turn == 1 ? s.walls1 : s.walls2; }

    @Override
    public boolean isTerminal(QuoridorState s) {
        return s.p1.r == s.rows - 1 || s.p2.r == 0;
    }

    @Override
    public boolean isValid(QuoridorState s, QuoridorAction a) {
        return validationError(s, a) == null;
    }

    @Override
    public QuoridorState apply(QuoridorState s, QuoridorAction a) {
        QuoridorState next = s.copy();

        switch (a.type) {
            case MOVE: {
                if (next.turn == 1) next.p1 = a.to; else next.p2 = a.to;

                // refresh visible cells
                for (int r = 0; r < next.rows; r++)
                    for (int c = 0; c < next.cols; c++)
                        next.set(r, c, null);

                next.set(next.p1.r, next.p1.c, new PawnPiece(1, "1"));
                next.set(next.p2.r, next.p2.c, new PawnPiece(2, "2"));

                next.turn = 3 - next.turn;
                break;
            }

            case WALL_H: {
                // validator guarantees bounds & legality, so place BOTH segments
                next.h[a.r][a.c]     = true;
                next.h[a.r][a.c + 1] = true;
                if (next.turn == 1) next.walls1--; else next.walls2--;
                next.rebuildGraphNeighbors();
                next.turn = 3 - next.turn;
                break;
            }

            case WALL_V: {
                // validator guarantees bounds & legality, so place BOTH segments
                next.v[a.r][a.c]     = true;
                next.v[a.r + 1][a.c] = true;
                if (next.turn == 1) next.walls1--; else next.walls2--;
                next.rebuildGraphNeighbors();
                next.turn = 3 - next.turn;
                break;
            }

            default: break;
        }
        return next;
    }

    @Override
    public String validationError(QuoridorState s, QuoridorAction a) {
        Position my  = s.currentPawn();
        Position opp = s.otherPawn();

        switch (a.type) {
            case MOVE: {
                if (!s.inBounds(a.to.r, a.to.c)) return "Move out of bounds";
                if (a.to.equals(opp))             return "Cannot move onto opponent";
                if (!isReachableStep(s, my, opp, a.to))
                    return "Illegal move (blocked or not adjacent/jump)";
                return null;
            }

            case WALL_H: {
                // needs two horizontal segments: h[r][c] and h[r][c+1]
                if (wallsLeft(s) <= 0) return "No walls left";
                if (a.r < 0 || a.r >= s.rows - 1 || a.c < 0 || a.c >= s.cols - 1)
                    return "Wall anchor out of bounds";
                if (s.h[a.r][a.c] || s.h[a.r][a.c + 1])
                    return "Wall overlaps existing horizontal wall";

                // forbid crossing a vertical wall at the midpoint (v[r][c] AND v[r+1][c])
                if (s.v[a.r][a.c] && s.v[a.r + 1][a.c])
                    return "Wall crosses an existing vertical wall";

                // simulate and ensure both players still have a path
                QuoridorState sim = s.copy();
                sim.h[a.r][a.c] = true;
                sim.h[a.r][a.c + 1] = true;
                sim.rebuildGraphNeighbors();
                if (!hasPath(sim, sim.p1, sim.rows - 1)) return "Wall blocks P1 path";
                if (!hasPath(sim, sim.p2, 0))            return "Wall blocks P2 path";
                return null;
            }

            case WALL_V: {
                // needs two vertical segments: v[r][c] and v[r+1][c]
                if (wallsLeft(s) <= 0) return "No walls left";
                if (a.r < 0 || a.r >= s.rows - 1 || a.c < 0 || a.c >= s.cols - 1)
                    return "Wall anchor out of bounds";
                if (s.v[a.r][a.c] || s.v[a.r + 1][a.c])
                    return "Wall overlaps existing vertical wall";

                // forbid crossing a horizontal wall at the midpoint (h[r][c] AND h[r][c+1])
                if (s.h[a.r][a.c] && s.h[a.r][a.c + 1])
                    return "Wall crosses an existing horizontal wall";

                // simulate and ensure both players still have a path
                QuoridorState sim = s.copy();
                sim.v[a.r][a.c] = true;
                sim.v[a.r + 1][a.c] = true;
                sim.rebuildGraphNeighbors();
                if (!hasPath(sim, sim.p1, sim.rows - 1)) return "Wall blocks P1 path";
                if (!hasPath(sim, sim.p2, 0))            return "Wall blocks P2 path";
                return null;
            }

            default: return "Unknown action";
        }
    }

   /** Return true if target is reachable in one move
 *  (adjacent step OR jump over adjacent opponent OR side-diagonal if blocked).
 *  We check walls directly in s.h/s.v so the logic isn’t sensitive to lattice quirks.
 */
private boolean isReachableStep(QuoridorState s, Position me, Position opp, Position target) {
    // --- small helpers for wall tests between adjacent cells ---
    // Is there a horizontal wall between (ra,ca) and (rb,cb) ? (same column, rows differ by 1)
    java.util.function.BiPredicate<Position, Position> blockedVert =
        (a, b) -> {
            if (a.c != b.c) return false;
            if (a.r + 1 == b.r) return s.h[a.r][a.c];       // between a (above) and b (below) -> h[a.r][c]
            if (b.r + 1 == a.r) return s.h[b.r][a.c];       // between b (above) and a (below) -> h[b.r][c]
            return false;
        };

    // Is there a vertical wall between (ra,ca) and (rb,cb) ? (same row, cols differ by 1)
    java.util.function.BiPredicate<Position, Position> blockedHoriz =
        (a, b) -> {
            if (a.r != b.r) return false;
            if (a.c + 1 == b.c) return s.v[a.r][a.c];       // between a (left) and b (right) -> v[r][a.c]
            if (b.c + 1 == a.c) return s.v[a.r][b.c];       // between b (left) and a (right) -> v[r][b.c]
            return false;
        };

    // Helper: in bounds & not on opponent
    java.util.function.Predicate<Position> free =
        p -> s.inBounds(p.r, p.c) && !(p.r == opp.r && p.c == opp.c);

    // 1) Adjacent orthogonal step (no wall, not onto opponent)
    Position up    = new Position(me.r - 1, me.c);
    Position down  = new Position(me.r + 1, me.c);
    Position left  = new Position(me.r, me.c - 1);
    Position right = new Position(me.r, me.c + 1);

    if (free.test(up)    && !blockedVert.test(me, up)    && up.equals(target))    return true;
    if (free.test(down)  && !blockedVert.test(me, down)  && down.equals(target))  return true;
    if (free.test(left)  && !blockedHoriz.test(me, left) && left.equals(target))  return true;
    if (free.test(right) && !blockedHoriz.test(me, right)&& right.equals(target)) return true;

    // 2) JUMP / SIDE-STEP logic: if opponent is adjacent with no wall between me and opponent
    boolean oppUp    = (opp.r == me.r - 1 && opp.c == me.c) && !blockedVert.test(me, opp);
    boolean oppDown  = (opp.r == me.r + 1 && opp.c == me.c) && !blockedVert.test(me, opp);
    boolean oppLeft  = (opp.r == me.r && opp.c == me.c - 1) && !blockedHoriz.test(me, opp);
    boolean oppRight = (opp.r == me.r && opp.c == me.c + 1) && !blockedHoriz.test(me, opp);

    if (oppUp) {
        // straight cell behind opponent
        Position behind = new Position(opp.r - 1, opp.c);
        if (s.inBounds(behind.r, behind.c) && !blockedVert.test(opp, behind)) {
            // straight jump available
            return behind.equals(target);
        } else {
            // straight blocked -> two diagonals (up-left / up-right) if passable from opp
            Position ul = new Position(opp.r, opp.c - 1);
            Position ur = new Position(opp.r, opp.c + 1);
            boolean leftFree  = s.inBounds(ul.r, ul.c) && !blockedHoriz.test(opp, ul);
            boolean rightFree = s.inBounds(ur.r, ur.c) && !blockedHoriz.test(opp, ur);
            return (leftFree  && ul.equals(target)) || (rightFree && ur.equals(target));
        }
    }

    if (oppDown) {
        Position behind = new Position(opp.r + 1, opp.c);
        if (s.inBounds(behind.r, behind.c) && !blockedVert.test(opp, behind)) {
            return behind.equals(target);
        } else {
            Position dl = new Position(opp.r, opp.c - 1);
            Position dr = new Position(opp.r, opp.c + 1);
            boolean leftFree  = s.inBounds(dl.r, dl.c) && !blockedHoriz.test(opp, dl);
            boolean rightFree = s.inBounds(dr.r, dr.c) && !blockedHoriz.test(opp, dr);
            return (leftFree  && dl.equals(target)) || (rightFree && dr.equals(target));
        }
    }

    if (oppLeft) {
        Position behind = new Position(opp.r, opp.c - 1);
        if (s.inBounds(behind.r, behind.c) && !blockedHoriz.test(opp, behind)) {
            return behind.equals(target);
        } else {
            Position ul = new Position(opp.r - 1, opp.c);
            Position dl = new Position(opp.r + 1, opp.c);
            boolean upFree   = s.inBounds(ul.r, ul.c) && !blockedVert.test(opp, ul);
            boolean downFree = s.inBounds(dl.r, dl.c) && !blockedVert.test(opp, dl);
            return (upFree && ul.equals(target)) || (downFree && dl.equals(target));
        }
    }

    if (oppRight) {
        Position behind = new Position(opp.r, opp.c + 1);
        if (s.inBounds(behind.r, behind.c) && !blockedHoriz.test(opp, behind)) {
            return behind.equals(target);
        } else {
            Position ur = new Position(opp.r - 1, opp.c);
            Position dr = new Position(opp.r + 1, opp.c);
            boolean upFree   = s.inBounds(ur.r, ur.c) && !blockedVert.test(opp, ur);
            boolean downFree = s.inBounds(dr.r, dr.c) && !blockedVert.test(opp, dr);
            return (upFree && ur.equals(target)) || (downFree && dr.equals(target));
        }
    }

    // not adjacent to opponent and not a normal adjacent step
    return false;
}


    /** BFS to check that a pawn has at least one path to its goal row. */
    private boolean hasPath(QuoridorState s, Position start, int goalRow) {
        boolean[][] vis = new boolean[s.rows][s.cols];
        ArrayDeque<Position> dq = new ArrayDeque<Position>();
        dq.add(start); vis[start.r][start.c] = true;
        while (!dq.isEmpty()) {
            Position p = dq.poll();
            if (p.r == goalRow) return true;
            for (Position n : s.lattice[p.r][p.c].neighbors()) {
                if (!vis[n.r][n.c]) { vis[n.r][n.c] = true; dq.add(n); }
            }
        }
        return false;
    }
}
