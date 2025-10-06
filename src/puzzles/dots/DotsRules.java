/**
 * Project: Game Hub
 * File: DotsRules.java
 * Purpose: Validity, apply, and terminal logic for Dots & Boxes.
 */
package puzzles.dots;

import game.core.Rules;

public final class DotsRules implements Rules<DotsState, ClaimEdge> {
    @Override public boolean isTerminal(DotsState s) {
        return s.claimedBoxes == s.rows * s.cols;
    }

    @Override public boolean isValid(DotsState s, ClaimEdge a) {
        EdgePos e = a.edge;
        if (e.o == Orientation.H) {
            if (e.r < 0 || e.r > s.rows || e.c < 0 || e.c >= s.cols) return false;
            return !s.H[e.r][e.c];
        } else {
            if (e.r < 0 || e.r >= s.rows || e.c < 0 || e.c > s.cols) return false;
            return !s.V[e.r][e.c];
        }
    }

    @Override public DotsState apply(DotsState s, ClaimEdge a) {
        boolean[][] H = copy(s.H), V = copy(s.V);
        char[][] Hown = copy(s.Howner), Vown = copy(s.Vowner);
        char[][] box = copy(s.boxOwner);
        int[] sc = new int[]{s.score[0], s.score[1]};
        int claimed = s.claimedBoxes;

        EdgePos e = a.edge;
        char mark = s.players[s.current].mark;
        if (e.o == Orientation.H) { H[e.r][e.c] = true; Hown[e.r][e.c] = mark; }
        else                       { V[e.r][e.c] = true; Vown[e.r][e.c] = mark; }

        int gained = 0;
        if (e.o == Orientation.H) {
            if (e.r-1 >= 0 && closes(H,V,e.r-1,e.c)) { box[e.r-1][e.c] = mark; gained++; }
            if (e.r < s.rows && closes(H,V,e.r,e.c)) { box[e.r][e.c]   = mark; gained++; }
        } else {
            if (e.c-1 >= 0 && closes(H,V,e.r,e.c-1)) { box[e.r][e.c-1] = mark; gained++; }
            if (e.c < s.cols && closes(H,V,e.r,e.c)) { box[e.r][e.c]   = mark; gained++; }
        }

        if (gained > 0) sc[s.current] += gained;
        int next = (gained > 0) ? s.current : 1 - s.current;
        return s.with(H, V, Hown, Vown, box, next, sc, claimed + gained);
    }

    @Override public String validationError(DotsState s, ClaimEdge a) {
        return "Edge out of bounds or already claimed.";
    }

    private static boolean closes(boolean[][] H, boolean[][] V, int r, int c) {
        return H[r][c] && H[r+1][c] && V[r][c] && V[r][c+1];
    }
    private static boolean[][] copy(boolean[][] x){ boolean[][] y=new boolean[x.length][]; for (int i=0;i<x.length;i++) y[i]=x[i].clone(); return y; }
    private static char[][] copy(char[][] x){ char[][] y=new char[x.length][]; for (int i=0;i<x.length;i++) y[i]=x[i].clone(); return y; }
}

