package puzzles.dots;

import java.util.ArrayList;
import java.util.List;

/** Utilities to enumerate and pretty-print available edges. */
final class EdgeUtils {
    private EdgeUtils() {}

    static final class EdgeRow {
        final Orientation o; final int r; final int c;
        final String boxSidePrimary;  // e.g., "1 0 T"
        final String hv;              // e.g., "H 1 0"
        final boolean critical;       // completes at least one box
        EdgeRow(Orientation o, int r, int c, String boxSidePrimary, String hv, boolean critical) {
            this.o=o; this.r=r; this.c=c;
            this.boxSidePrimary=boxSidePrimary; this.hv=hv; this.critical=critical;
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
                out.add(rowForH(s, r, c, crit));
            }
        }
        // V edges: r in [0..rows-1], c in [0..cols]
        for (int r=0; r<s.rows; r++) {
            for (int c=0; c<=s.cols; c++) {
                if (s.V[r][c]) continue;
                boolean crit = wouldCloseBoxV(s, r, c);
                if (criticalOnly && !crit) continue;
                out.add(rowForV(s, r, c, crit));
            }
        }
        return out;
    }

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

    private static EdgeRow rowForH(DotsState s, int r, int c, boolean crit) {
        String side = (r == s.rows) ? "B" : "T";
        int br = (r == s.rows) ? (r-1) : r;
        int bc = c;
        String boxSide = br + " " + bc + " " + side;
        String hv = "H " + r + " " + c;
        return new EdgeRow(Orientation.H, r, c, boxSide, hv, crit);
    }
    private static EdgeRow rowForV(DotsState s, int r, int c, boolean crit) {
        String side = (c == s.cols) ? "R" : "L";
        int br = r;
        int bc = (c == s.cols) ? (c-1) : c;
        String boxSide = br + " " + bc + " " + side;
        String hv = "V " + r + " " + c;
        return new EdgeRow(Orientation.V, r, c, boxSide, hv, crit);
    }

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
