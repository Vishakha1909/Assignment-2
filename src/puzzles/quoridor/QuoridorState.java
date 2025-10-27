package puzzles.quoridor;

import game.core.Board;
import game.core.Piece;
import game.core.Position;
import game.core.Tile;

import java.util.Arrays;

/**
 * Simple Quoridor board: cells (rows x cols), horizontal walls h[r][c] between (r,c) and (r+1,c),
 * vertical walls v[r][c] between (r,c) and (r,c+1).
 */
public final class QuoridorState implements Board<Piece> {
    public final int rows, cols;

    private final Piece[][] cells;    // for pawns only
    public final boolean[][] h;       // (rows-1) x cols
    public final boolean[][] v;       // rows x (cols-1)

    // Pawns and walls
    public Position p1, p2;
    public int walls1, walls2;
    /** 1 or 2 */
    public int turn = 1;

    // Names (initials shown on board)
    public String name1 = "A";
    public String name2 = "B";

    // Session/in-game stats (lightweight)
    public int moveCount = 0;
    public int wallsPlaced1 = 0;
    public int wallsPlaced2 = 0;
    public int jumps1 = 0;
    public int jumps2 = 0;

    // Graph for path checks & hints
    public final Tile[][] lattice;

    public QuoridorState() { this(9, 9); }
    public QuoridorState(int rows, int cols) {
        if (rows < 3 || cols < 3) throw new IllegalArgumentException("Minimum 3x3");
        this.rows = rows; this.cols = cols;
        this.cells = new Piece[rows][cols];
        this.h = new boolean[Math.max(0, rows-1)][cols];
        this.v = new boolean[rows][Math.max(0, cols-1)];
        this.lattice = new Tile[rows][cols];

        // start in the center of their first/last rows
        this.p1 = new Position(0, cols/2);
        this.p2 = new Position(rows-1, cols/2);

        // walls per player (9x9 → 10), scale by board
        int base = Math.min(rows, cols) + 1;
        this.walls1 = base;
        this.walls2 = base;

        initLatticeFresh();
        placePawnPieces();
        rebuildGraphNeighbors();
    }

    private void initLatticeFresh() {
        for (int r=0;r<rows;r++) for (int c=0;c<cols;c++)
            lattice[r][c] = new Tile(0, new Position(r,c));
    }
    public void rebuildGraphNeighbors() {
        initLatticeFresh();
        for (int r=0;r<rows;r++) for (int c=0;c<cols;c++) {
            if (r>0       && !h[r-1][c]) lattice[r][c].addEdge(new Position(r-1,c));
            if (r<rows-1 && !h[r][c])   lattice[r][c].addEdge(new Position(r+1,c));
            if (c>0       && !v[r][c-1]) lattice[r][c].addEdge(new Position(r,c-1));
            if (c<cols-1 && !v[r][c])   lattice[r][c].addEdge(new Position(r,c+1));
        }
    }

    public void placePawnPieces() {
        for (int r=0;r<rows;r++) Arrays.fill(cells[r], null);
        set(p1.r, p1.c, new PawnPiece(1, initialOf(name1)));
        set(p2.r, p2.c, new PawnPiece(2, initialOf(name2)));
    }
    private static String initialOf(String s) {
        if (s == null || s.trim().isEmpty()) return "?";
        return String.valueOf(Character.toUpperCase(s.trim().charAt(0)));
    }

    public QuoridorState copy() {
        QuoridorState s = new QuoridorState(rows, cols);
        for (int r=0;r<Math.max(0, rows-1);r++) s.h[r] = Arrays.copyOf(h[r], cols);
        for (int r=0;r<rows;r++)                s.v[r] = Arrays.copyOf(v[r], Math.max(0, cols-1));
        s.p1 = new Position(p1.r, p1.c);
        s.p2 = new Position(p2.r, p2.c);
        s.walls1 = walls1; s.walls2 = walls2;
        s.turn = turn;
        s.name1 = name1; s.name2 = name2;
        s.moveCount = moveCount;
        s.wallsPlaced1 = wallsPlaced1; s.wallsPlaced2 = wallsPlaced2;
        s.jumps1 = jumps1; s.jumps2 = jumps2;
        s.rebuildGraphNeighbors();
        s.placePawnPieces();
        return s;
    }

    // Board<Piece>
    @Override public int rows() { return rows; }
    @Override public int cols() { return cols; }
    @Override public Piece get(int r, int c) { return cells[r][c]; }
    @Override public void set(int r, int c, Piece value) { cells[r][c] = value; }

    public boolean inBounds(int r, int c) { return r>=0 && r<rows && c>=0 && c<cols; }
    public Position currentPawn() { return (turn==1) ? p1 : p2; }
    public Position otherPawn()   { return (turn==1) ? p2 : p1; }
}
