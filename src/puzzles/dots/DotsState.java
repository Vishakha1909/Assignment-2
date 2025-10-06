/**
 * Project: Game Hub
 * File: DotsState.java
 * Purpose: Game state for Dots & Boxes.
 */
package puzzles.dots;

public final class DotsState {
    public final int rows, cols;
    // edge claimed flags
    public final boolean[][] H;        // (rows+1) x cols
    public final boolean[][] V;        // rows x (cols+1)
    // NEW: who claimed each edge (0 if unclaimed, else 'A' / 'B')
    public final char[][] Howner;      // (rows+1) x cols
    public final char[][] Vowner;      // rows x (cols+1)

    public final char[][] boxOwner;    // 0 if none; else player's mark
    public final PlayerInfo[] players;
    public final int current;          // 0 or 1
    public final int[] score;          // length=2
    public final int claimedBoxes;

    public DotsState(int rows, int cols, PlayerInfo p1, PlayerInfo p2) {
        if (rows < 1 || cols < 1) throw new IllegalArgumentException("min 1x1 boxes");
        this.rows=rows; this.cols=cols;
        this.H = new boolean[rows+1][cols];
        this.V = new boolean[rows][cols+1];
        this.Howner = new char[rows+1][cols];
        this.Vowner = new char[rows][cols+1];
        this.boxOwner = new char[rows][cols];
        this.players = new PlayerInfo[]{p1, p2};
        this.current = 0;
        this.score = new int[]{0,0};
        this.claimedBoxes = 0;
    }

    private DotsState(int rows,int cols,boolean[][]H,boolean[][]V,
                      char[][]Howner,char[][]Vowner,char[][]boxOwner,
                      PlayerInfo[] players,int current,int[] score,int claimed) {
        this.rows=rows; this.cols=cols; this.H=H; this.V=V;
        this.Howner=Howner; this.Vowner=Vowner; this.boxOwner=boxOwner;
        this.players=players; this.current=current; this.score=score; this.claimedBoxes=claimed;
    }

    public DotsState with(boolean[][] H, boolean[][] V, char[][] Howner, char[][] Vowner,
                          char[][] boxOwner, int current, int[] score, int claimed) {
        return new DotsState(rows, cols, H, V, Howner, Vowner, boxOwner, players, current, score, claimed);
    }
}

