package puzzles.quoridor;

import game.core.Position;
import game.core.Rules;

import java.util.ArrayDeque;

public final class QuoridorRules implements Rules<QuoridorState, QuoridorAction> {

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
        QuoridorState n = s.copy();
        switch (a.type) {
            case MOVE: {
                boolean wasJump = isJump(s, s.currentPawn(), s.otherPawn(), a.to);
                if (n.turn == 1) {
                    n.p1 = a.to;
                    if (wasJump) n.jumps1++;
                } else {
                    n.p2 = a.to;
                    if (wasJump) n.jumps2++;
                }
                n.placePawnPieces();
                n.turn = 3 - n.turn;
                break;
            }
            case WALL_H: {
                n.h[a.r][a.c] = true;
                n.h[a.r][a.c + 1] = true;
                if (n.turn == 1) { n.walls1--; n.wallsPlaced1++; } else { n.walls2--; n.wallsPlaced2++; }
                n.rebuildGraphNeighbors();
                n.turn = 3 - n.turn;
                break;
            }
            case WALL_V: {
                n.v[a.r][a.c] = true;
                n.v[a.r + 1][a.c] = true;
                if (n.turn == 1) { n.walls1--; n.wallsPlaced1++; } else { n.walls2--; n.wallsPlaced2++; }
                n.rebuildGraphNeighbors();
                n.turn = 3 - n.turn;
                break;
            }
        }
        n.moveCount++;
        return n;
    }

    @Override
    public String validationError(QuoridorState s, QuoridorAction a) {
        switch (a.type) {
            case MOVE:
                if (!s.inBounds(a.to.r, a.to.c)) return "Out of bounds";
                if (a.to.equals(s.otherPawn()))  return "Square occupied";
                return isReachableStep(s, s.currentPawn(), s.otherPawn(), a.to)
                        ? null : "Illegal move (blocked/not adjacent/jump)";

            case WALL_H: {
                if ((s.turn==1 && s.walls1<=0) || (s.turn==2 && s.walls2<=0)) return "No walls left";
                if (a.r<0 || a.r>=s.rows-1 || a.c<0 || a.c>=s.cols-1) return "Anchor OOB";
                if (s.h[a.r][a.c] || s.h[a.r][a.c+1]) return "Overlap H";
                // disallow crossing: needs both v segments occupied to be crossing at the center
                if (s.v[a.r][a.c] && s.v[a.r+1][a.c]) return "Crossing vertical";
                QuoridorState sim = s.copy();
                sim.h[a.r][a.c]=true; sim.h[a.r][a.c+1]=true; sim.rebuildGraphNeighbors();
                if (!hasPath(sim, sim.p1, sim.rows-1)) return "Blocks P1 path";
                if (!hasPath(sim, sim.p2, 0))          return "Blocks P2 path";
                return null;
            }

            case WALL_V: {
                if ((s.turn==1 && s.walls1<=0) || (s.turn==2 && s.walls2<=0)) return "No walls left";
                if (a.r<0 || a.r>=s.rows-1 || a.c<0 || a.c>=s.cols-1) return "Anchor OOB";
                if (s.v[a.r][a.c] || s.v[a.r+1][a.c]) return "Overlap V";
                if (s.h[a.r][a.c] && s.h[a.r][a.c+1]) return "Crossing horizontal";
                QuoridorState sim = s.copy();
                sim.v[a.r][a.c]=true; sim.v[a.r+1][a.c]=true; sim.rebuildGraphNeighbors();
                if (!hasPath(sim, sim.p1, sim.rows-1)) return "Blocks P1 path";
                if (!hasPath(sim, sim.p2, 0))          return "Blocks P2 path";
                return null;
            }
        }
        return "Unknown";
    }

    /** True if the move is a jump (used for stats). */
    private boolean isJump(QuoridorState s, Position me, Position opp, Position target) {
        int dr = Math.abs(target.r - me.r), dc = Math.abs(target.c - me.c);
        return (dr==2 && dc==0) || (dr==0 && dc==2) || (dr==1 && dc==1 && isAdjacent(me, opp));
    }
    private boolean isAdjacent(Position a, Position b) {
        return Math.abs(a.r-b.r) + Math.abs(a.c-b.c) == 1;
    }

    /** Wall-aware one-move reachability: step, jump, diagonal side-step if straight is blocked. */
    private boolean isReachableStep(QuoridorState s, Position me, Position opp, Position target) {
        // Adjacent orthogonal
        Position up    = new Position(me.r-1, me.c);
        Position down  = new Position(me.r+1, me.c);
        Position left  = new Position(me.r, me.c-1);
        Position right = new Position(me.r, me.c+1);

        if (free(s, up, opp)    && !blockedVert(s, me, up)    && up.equals(target))    return true;
        if (free(s, down, opp)  && !blockedVert(s, me, down)  && down.equals(target))  return true;
        if (free(s, left, opp)  && !blockedHoriz(s, me, left) && left.equals(target))  return true;
        if (free(s, right, opp) && !blockedHoriz(s, me, right)&& right.equals(target)) return true;

        // Jump/side-step if opponent is orthogonally adjacent and reachable
        if (isAdjacent(me, opp)) {
            if (me.r == opp.r) {
                // left/right
                if (!blockedHoriz(s, me, opp)) {
                    int dc = opp.c - me.c;
                    Position behind = new Position(opp.r, opp.c + dc);
                    if (s.inBounds(behind.r, behind.c) && !blockedHoriz(s, opp, behind) && free(s, behind, opp)) {
                        if (behind.equals(target)) return true; // straight jump
                    } else {
                        Position upFromOpp   = new Position(opp.r-1, opp.c);
                        Position downFromOpp = new Position(opp.r+1, opp.c);
                        if (s.inBounds(upFromOpp.r, upFromOpp.c) && !blockedVert(s, opp, upFromOpp) && free(s, upFromOpp, opp) && upFromOpp.equals(target)) return true;
                        if (s.inBounds(downFromOpp.r, downFromOpp.c) && !blockedVert(s, opp, downFromOpp) && free(s, downFromOpp, opp) && downFromOpp.equals(target)) return true;
                    }
                }
            } else if (me.c == opp.c) {
                // up/down
                if (!blockedVert(s, me, opp)) {
                    int dr = opp.r - me.r;
                    Position behind = new Position(opp.r + dr, opp.c);
                    if (s.inBounds(behind.r, behind.c) && !blockedVert(s, opp, behind) && free(s, behind, opp)) {
                        if (behind.equals(target)) return true; // straight jump
                    } else {
                        Position leftFromOpp  = new Position(opp.r, opp.c-1);
                        Position rightFromOpp = new Position(opp.r, opp.c+1);
                        if (s.inBounds(leftFromOpp.r, leftFromOpp.c) && !blockedHoriz(s, opp, leftFromOpp) && free(s, leftFromOpp, opp) && leftFromOpp.equals(target)) return true;
                        if (s.inBounds(rightFromOpp.r, rightFromOpp.c) && !blockedHoriz(s, opp, rightFromOpp) && free(s, rightFromOpp, opp) && rightFromOpp.equals(target)) return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean free(QuoridorState s, Position p, Position opp) {
        return s.inBounds(p.r,p.c) && !(p.equals(opp));
    }
    private boolean blockedVert(QuoridorState s, Position a, Position b) {
        if (a.c != b.c) return false;
        if (a.r + 1 == b.r) return s.h[a.r][a.c];
        if (b.r + 1 == a.r) return s.h[b.r][a.c];
        return false;
    }
    private boolean blockedHoriz(QuoridorState s, Position a, Position b) {
        if (a.r != b.r) return false;
        if (a.c + 1 == b.c) return s.v[a.r][a.c];
        if (b.c + 1 == a.c) return s.v[a.r][b.c];
        return false;
    }

    /** BFS reachability from start to a goal row (top/bottom). */
    private boolean hasPath(QuoridorState s, Position start, int goalRow) {
        boolean[][] vis = new boolean[s.rows][s.cols];
        ArrayDeque<Position> dq = new ArrayDeque<Position>();
        dq.add(start); vis[start.r][start.c]=true;
        while (!dq.isEmpty()) {
            Position p = dq.poll();
            if (p.r == goalRow) return true;
            for (Position n : s.lattice[p.r][p.c].neighbors()) {
                if (!vis[n.r][n.c]) { vis[n.r][n.c]=true; dq.add(n); }
            }
        }
        return false;
    }
}
