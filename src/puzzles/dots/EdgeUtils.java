package puzzles.dots;

import java.util.ArrayList;
import java.util.List;

/** Utilities to enumerate and pretty-print available edges (H/V only). */
final class EdgeUtils {
    private EdgeUtils() {}

    static final class EdgeRow {
        final Orientation o; final int r; final int c;
        final String hv;        // e.g., "H 1 0" or "V 2 3"
        final boolean critical; // completes at least one box if played
        EdgeRow(Orientation o, int r, int c, String hv, boolean critical) {
            this.o=o; this.r=r; this.c=c; this.hv=hv; this.critical=critical;
        }
    }

    /** List unclaimed edges; if criticalOnly, include only edges that would close a box. */
    static List<EdgeRow> listAvailable(DotsState s, boolean criticalOnly) {
        List<EdgeRow> out = new ArrayList<EdgeRow>();
        // H edges: r in [0..rows], c in [0..cols-1]
        for (int r=0; r<=s.rows; r++) {
            for (int c=0; c<s.cols; c++) {
                if (s.H[r][c]) continue;
                boolean crit = wouldCloseBoxH(s, r, c);
                if (criticalOnly && !crit) continue;
                out.add(new EdgeRow(Orientation.H, r, c, "H " + r + " " + c, crit));
            }
        }
        // V edges: r in [0..rows-1], c in [0..cols]
        for (int r=0; r<s.rows; r++) {
            for (int c=0; c<=s.cols; c++) {
                if (s.V[r][c]) continue;
                boolean crit = wouldCloseBoxV(s, r, c);
                if (criticalOnly && !crit) continue;
                out.add(new EdgeRow(Orientation.V, r, c, "V " + r + " " + c, crit));
            }
        }
        return out;
    }

    /** Optional small filter by row/col index for convenience. */
    static List<EdgeRow> filterByRowCol(List<EdgeRow> rows, String which, int k) {
        List<EdgeRow> out = new ArrayList<EdgeRow>();
        boolean rowFilter = "row".equalsIgnoreCase(which);
        boolean colFilter = "col".equalsIgnoreCase(which);
        for (EdgeRow e : rows) {
            if (rowFilter && e.r == k) out.add(e);
            else if (colFilter && e.c == k) out.add(e);
            else if (!rowFilter && !colFilter) out.add(e);
        }
        return out;
    }

    // ---------- internals: does this edge complete a box? ----------

    private static boolean wouldCloseBoxH(DotsState s, int r, int c) {
        if (r-1 >= 0) { if (s.H[r-1][c] && s.V[r-1][c] && s.V[r-1][c+1]) return true; }
        if (r < s.rows) { if (s.H[r+1][c] && s.V[r][c] && s.V[r][c+1]) return true; }
        return false;
    }
    private static boolean wouldCloseBoxV(DotsState s, int r, int c) {
        if (c-1 >= 0) { if (s.V[r][c-1] && s.H[r][c-1] && s.H[r+1][c-1]) return true; }
        if (c < s.cols) { if (s.V[r][c+1] && s.H[r][c] && s.H[r+1][c]) return true; }
        return false;
    }
}
