/**
 * Project: Game Hub
 * File: SolverUtils.java
 * Purpose: Build a random solvable board; parity test for general m×n.
 */
package puzzles.sliding;

import game.core.Position;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public final class SolverUtils {
    private SolverUtils() {}

    /**
     * Create a random solvable starting state using parity rules.
     * @param rows rows
     * @param cols cols
     * @param seed RNG seed (use System.currentTimeMillis() for time-based)
     */
    public static SlidingState randomSolvable(int rows, int cols, long seed) {
        Random rnd = new Random(seed);
        List<Integer> vals = new ArrayList<>(rows*cols);
        for (int i = 0; i < rows*cols; i++) vals.add(i); // 0..N-1 (0 = blank)

        while (true) {
            Collections.shuffle(vals, rnd);
            if (isSolvable(vals, rows, cols)) {
                GridBoard b = new GridBoard(rows, cols);
                Position empty = null;
                int k = 0;
                for (int r=0;r<rows;r++){
                    for (int c=0;c<cols;c++){
                        int v = vals.get(k++);
                        b.set(r,c,new SlidingTile(v));
                        if (v==0) empty = new Position(r,c);
                    }
                }
                return new SlidingState(b, empty, 0);
            }
        }
    }

    /** Standard n-puzzle parity test for m×n. */
    public static boolean isSolvable(List<Integer> perm, int rows, int cols) {
        int N = rows*cols;
        int[] a = new int[N-1];
        int idx = 0, blankIndex = -1;
        for (int i=0;i<N;i++){
            int v = perm.get(i);
            if (v == 0) blankIndex = i;
            else a[idx++] = v;
        }
        int inv = 0;
        for (int i=0;i<a.length;i++)
            for (int j=i+1;j<a.length;j++)
                if (a[i] > a[j]) inv++;

        if ((cols % 2) == 1) {
            return (inv % 2) == 0; // odd width: even inversions
        } else {
            int blankRowFromBottom = rows - (blankIndex / cols);
            if ((blankRowFromBottom % 2) == 0) {
                return (inv % 2) == 1; // even row from bottom -> odd inversions
            } else {
                return (inv % 2) == 0; // odd row from bottom -> even inversions
            }
        }
    }
}
