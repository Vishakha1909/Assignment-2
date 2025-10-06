/**
 * Project: Game Hub
 * File: DotsRenderer.java
 * Purpose: ASCII renderer for Dots & Boxes.
 */
package puzzles.dots;

import game.core.Renderer;

public final class DotsRenderer implements Renderer<DotsState> {
    @Override public String render(DotsState s) {
        StringBuilder sb = new StringBuilder();
        // header: show valid ranges
        sb.append("H r∈[0..").append(s.rows).append("] c∈[0..").append(s.cols-1)
          .append("]   V r∈[0..").append(s.rows-1).append("] c∈[0..").append(s.cols).append("]\n");

        for (int r = 0; r < s.rows; r++) {
            // top edges with owner mark in the middle, e.g. -A-
            for (int c = 0; c < s.cols; c++) {
                sb.append(".");
                if (s.H[r][c]) sb.append("-").append(mark(s.Howner[r][c])).append("-");
                else           sb.append("   ");
            }
            sb.append(".\n");
            // verticals with boxes
            for (int c = 0; c < s.cols; c++) {
                if (s.V[r][c]) sb.append(mark(s.Vowner[r][c]));
                else           sb.append(" ");
                char m = s.boxOwner[r][c] == 0 ? ' ' : s.boxOwner[r][c];
                sb.append(" ").append(m).append(" ");
            }
            sb.append(s.V[r][s.cols] ? String.valueOf(mark(s.Vowner[r][s.cols]))+"\n" : " \n");
        }
        // bottom edges
        for (int c = 0; c < s.cols; c++) {
            sb.append(".");
            if (s.H[s.rows][c]) sb.append("-").append(mark(s.Howner[s.rows][c])).append("-");
            else                sb.append("   ");
        }
        sb.append(".\n");
        return sb.toString();
    }

    private char mark(char owner){ return owner==0 ? '-' : owner; }
}
