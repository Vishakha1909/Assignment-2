package puzzles.quoridor;

import game.core.Renderer;

/**
 * Renderer that understands the doubled-grid model used by QuoridorState:
 *   - Cells at even,even (2r,2c)
 *   - Horizontal walls at odd,even
 *   - Vertical walls at even,odd
 *
 * It prints a logical n x m board (NOT 2n x 2m), with ASCII fallback on Windows
 * and optional ANSI colors.
 *
 * Expected state fields (as in your QuoridorState):
 *   int n, m;                 // logical size (cells)
 *   int rows, cols;           // doubled grid size (2n, 2m)
 *   game.core.Position p1,p2; // doubled coords: (even,even)
 *   boolean[][] h, v;         // wall segments on doubled grid
 *   int wallsA, wallsB;       // counts to display
 */
public final class QuoridorRenderer implements Renderer<QuoridorState> {

    // style
    private final boolean useUnicode;
    private final boolean useColor;
    private final boolean tintWalls;

    // glyphs
    private final String DOT, H, V, X, SP3;

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
        this.useUnicode = preferUnicode && !isWindows;       // ASCII on Windows (avoid ?)
        this.useColor   = useColor;
        this.tintWalls  = useColor && tintWalls;

        if (this.useUnicode) {
            DOT = "•"; H = "───"; V = "│"; X = "┼"; SP3 = "   ";
        } else {
            DOT = "."; H = "---"; V = "|"; X = "+";  SP3 = "   ";
        }
    }

    private String c(String s, String color) { return useColor ? color + s + RESET : s; }
    private String bold(String s)            { return useColor ? BOLD + s + RESET : s; }
    private String dim(String s)             { return useColor ? DIM + s + RESET  : s; }

    @Override public String render(QuoridorState s) {
        StringBuilder sb = new StringBuilder(4096);

        // ===== HUD =====
        int aR = s.p1.r / 2, aC = s.p1.c / 2; // logical cell coords
        int bR = s.p2.r / 2, bC = s.p2.c / 2;

        sb.append(bold("=== Quoridor ===")).append("\n");
        sb.append(c("A@" + aR + "," + aC, FG_CYAN)).append("   ")
          .append(c("B@" + bR + "," + bC, FG_MAGENTA)).append("   ")
          .append("Walls ").append(c("A:" + s.wallsA, FG_CYAN)).append(" ")
          .append(c("B:" + s.wallsB, FG_MAGENTA)).append("\n");
        sb.append(c("Commands: ", FG_GRAY))
          .append("move r c  |  wall H r c  |  wall V r c  |  size n m  |  q")
          .append("\n\n");

        // ===== column indices (logical) =====
        sb.append("    ");
        for (int c = 0; c < s.n /* columns */; c++) sb.append(String.format("%-4d", c));
        sb.append("\n");

        // ===== rows =====
        for (int r = 0; r < s.n; r++) {
            // row label + cells with vertical walls
            sb.append(String.format("%-3d", r)).append(" ");
            for (int c = 0; c < s.m; c++) {
                int rr = 2 * r;     // doubled row of the cell
                int cc = 2 * c;     // doubled col of the cell

                String cell = DOT;
                if (rr == s.p1.r && cc == s.p1.c) cell = c("A", FG_CYAN);
                else if (rr == s.p2.r && cc == s.p2.c) cell = c("B", FG_MAGENTA);
                sb.append(cell);

                // vertical wall between (r,c) and (r,c+1) sits at (2r, 2c+1)
                if (c < s.m - 1) {
                    boolean w = in(s, 2*r, 2*c + 1) && s.v[2*r][2*c + 1];
                    if (w) {
                        String vv = V;
                        if (tintWalls) vv = c(vv, DIM + FG_YELLOW);
                        sb.append(" ").append(vv).append(" ");
                    } else {
                        sb.append(SP3);
                    }
                }
            }
            sb.append("\n");

            // horizontal wall row between r and r+1 → (2r+1, 2c)
            if (r < s.n - 1) {
                sb.append("    ");
                for (int c = 0; c < s.m; c++) {
                    boolean w = in(s, 2*r + 1, 2*c) && s.h[2*r + 1][2*c];
                    if (w) {
                        String hh = H;
                        if (tintWalls) hh = c(hh, DIM + FG_YELLOW);
                        sb.append(hh);
                    } else {
                        sb.append(SP3);
                    }
                    if (c < s.m - 1) {
                        // a tiny cross helper; tint if any adjacent wall around this join
                        boolean adj = w
                                   || (in(s, 2*r + 1, 2*(c + 1)) && s.h[2*r + 1][2*(c + 1)])
                                   || (in(s, 2*r, 2*c + 1) && s.v[2*r][2*c + 1]);
                        String xx = adj && tintWalls ? c(X, DIM + FG_YELLOW) : X;
                        sb.append(xx);
                    }
                }
                sb.append("\n");
            }
        }

        // footer note
        sb.append("\n")
          .append(c("Note:", FG_GRAY))
          .append(" walls use top-left indices in logical coords r,c -> [0..")
          .append(Math.max(0, s.n - 2)).append("] for a ")
          .append(s.n).append("×").append(s.m).append(" board.\n");

        return sb.toString();
    }

    private static boolean in(QuoridorState s, int r, int c) {
        return r >= 0 && r < s.rows && c >= 0 && c < s.cols;
    }
}
