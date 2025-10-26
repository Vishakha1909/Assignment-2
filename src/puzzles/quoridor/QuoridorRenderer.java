package puzzles.quoridor;

import game.core.Renderer;

/**
 * Boxed Quoridor renderer for the simple grid model:
 *  - cells: rows x cols
 *  - h[r][c] is the horizontal segment between (r,c) and (r+1,c)
 *  - v[r][c] is the vertical   segment between (r,c) and (r,c+1)
 *
 * Always draws a full lattice (+---+ / |   |). Placed walls overlay in bold yellow.
 * Pawns: A (cyan), B (magenta). ASCII fallback on Windows to avoid glyph issues.
 */
public final class QuoridorRenderer implements Renderer<QuoridorState> {

    // style
    private final boolean useUnicode;
    private final boolean useColor;
    private final boolean tintWalls;

    // glyphs
    private final String H, V, X;
    private static final String SP3 = "   ";

    // ANSI
    private static final String RESET = "\u001B[0m";
    private static final String BOLD  = "\u001B[1m";
    private static final String DIM   = "\u001B[2m";
    private static final String FG_CYAN    = "\u001B[36m";
    private static final String FG_MAGENTA = "\u001B[35m";
    private static final String FG_YELLOW  = "\u001B[33m";
    private static final String FG_GRAY    = "\u001B[90m";

    public QuoridorRenderer(boolean preferUnicode, boolean useColor, boolean tintWalls) {
        boolean isWindows = System.getProperty("os.name","").toLowerCase().contains("win");
        this.useUnicode = preferUnicode && !isWindows; // ASCII on Windows
        this.useColor   = useColor;
        this.tintWalls  = useColor && tintWalls;

        if (this.useUnicode) { H = "───"; V = "│"; X = "┼"; }
        else                 { H = "---"; V = "|"; X = "+"; }
    }

    private String c(String s, String color) { return useColor ? color + s + RESET : s; }
    private String bold(String s)            { return useColor ? BOLD + s + RESET : s; }
    private String dim(String s)             { return useColor ? DIM + s + RESET  : s; }

    @Override
    public String render(QuoridorState s) {
        StringBuilder sb = new StringBuilder(8192);

        // HUD (logical coordinates)
        sb.append(c("P1@" + s.p1.r + "," + s.p1.c, FG_CYAN)).append("   ")
          .append(c("P2@" + s.p2.r + "," + s.p2.c, FG_MAGENTA)).append("   ")
          .append("Walls ").append(c("P1:" + s.walls1, FG_CYAN)).append(" ")
          .append(c("P2:" + s.walls2, FG_MAGENTA)).append("\n");
        sb.append(c("Commands: ", FG_GRAY))
          .append("move r c  |  wall H r c  |  wall V r c  |  size n m  |  q\n\n");

        // column indices
        sb.append("    ");
        for (int cIdx = 0; cIdx < s.cols; cIdx++) sb.append(String.format("%-4d", cIdx));
        sb.append("\n");

        // top border
        sb.append("    ").append(border(s.cols)).append("\n");

        for (int r = 0; r < s.rows; r++) {
            // cells row
            sb.append(String.format("%-3d", r)).append(" ");
            sb.append(dim(V)); // left border
            for (int cIdx = 0; cIdx < s.cols; cIdx++) {
                // pawn / empty
                String cell = SP3;
                if (s.p1.r == r && s.p1.c == cIdx)      cell = " " + c("A", FG_CYAN)    + " ";
                else if (s.p2.r == r && s.p2.c == cIdx) cell = " " + c("B", FG_MAGENTA) + " ";
                sb.append(cell);

                // interior vertical wall between (r,cIdx) and (r,cIdx+1)
                if (cIdx < s.cols - 1) {
                    boolean wall = s.v[r][cIdx];
                    sb.append(wall ? c(bold(V), FG_YELLOW) : dim(V));
                }
            }
            sb.append(dim(V)).append("\n");

            // separator below row r (unless last row)
            if (r < s.rows - 1) {
                sb.append("    ").append(separatorRow(s, r)).append("\n");
            }
        }

        // >>> bottom border (fix): close the grid after the last row
        sb.append("    ").append(border(s.cols)).append("\n");

        return sb.toString();
    }

    private String border(int cols) {
        StringBuilder line = new StringBuilder();
        line.append(dim(X));
        for (int c = 0; c < cols; c++) line.append(dim(H)).append(dim(X));
        return line.toString();
    }

    /** Separator between logical rows r and r+1. Overlays H walls; keeps V continuity through junctions. */
    private String separatorRow(QuoridorState s, int r) {
        StringBuilder line = new StringBuilder();
        line.append(dim(X));
        for (int c = 0; c < s.cols; c++) {
            boolean hSeg = s.h[r][c];
            line.append(hSeg ? c(bold(H), FG_YELLOW) : dim(H));

            if (c < s.cols - 1) {
                // Is there a vertical wall continuing through this junction?
                boolean vTop = s.v[r][c];
                boolean vBot = s.v[r+1][c];
                if (vTop && vBot) {
                    line.append(c(bold(V), FG_YELLOW));   // continuous V wall
                } else {
                    boolean meets = hSeg || s.h[r][c+1] || vTop;
                    line.append(meets ? c(dim(X), FG_YELLOW) : dim(X));
                }
            }
        }
        line.append(dim(X));
        return line.toString();
    }
}
