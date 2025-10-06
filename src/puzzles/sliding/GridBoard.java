/**
 * Project: Game Hub
 * File: GridBoard.java
 * Purpose: Concrete Board<SlidingTile> backed by 2D array.
 */
package puzzles.sliding;

import game.core.Board;

public final class GridBoard implements Board<SlidingTile> {
    private final int rows, cols;
    private final SlidingTile[][] grid;

    public GridBoard(int rows, int cols) {
        if (rows < 2 || cols < 2) throw new IllegalArgumentException("min size 2x2");
        this.rows = rows; this.cols = cols;
        this.grid = new SlidingTile[rows][cols];
    }

    @Override public int rows() { return rows; }
    @Override public int cols() { return cols; }
    @Override public SlidingTile get(int r, int c) { return grid[r][c]; }
    @Override public void set(int r, int c, SlidingTile v) { grid[r][c] = v; }
}
