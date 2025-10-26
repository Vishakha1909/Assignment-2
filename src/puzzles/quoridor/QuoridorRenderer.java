package puzzles.quoridor;

import game.core.Renderer;

/**
 * QuoridorRenderer — boxed grid with clear walls.
 *
 * Works with the doubled-grid QuoridorState:
 *   cells at (2r,2c); horizontal wall segments at (2r+1,2c); vertical at (2r,2c+1).
 * Draws a full lattice like the sliding puzzle so walls are obvious:
 *   - baseline grid lines are dim (ASCII on Windows, Unicode elsewhere),
 *   - placed walls are bold & colored (yellow),
 *   - pawns A/B are cyan/magenta.
 *
 * Commands displayed: move r c | wall H r c | wall V r c | size n m | q
 */
public final class QuoridorRenderer implements Renderer<QuoridorState> {

    // style
    private final boolean useUnicode;
    private final boolean useColor;
    private final boolean tintWalls;

    // glyphs (3-char horizontal cells)
    private final String H, V, X, SP3;

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
        this.useUnicode = preferUnicode && !isWindows; // ASCII on Windows → no '?'
        this.useColor   = useColor;
        this.tintWalls  = useColor && tintWalls;

        if (this.useUnicode) {
            H = "───"; V = "│"; X = "┼";  SP3 = "   ";
        } else {
            H = "---"; V = "|"; X = "+";  SP3 = "   ";
        }
    }

    private String c(String s, String color) { return useColor ? color + s + RESET : s; }
    private String bold(String s)            { return useColor ? BOLD + s + RESET : s; }
    private String dim(String s)             { return useColor ? DIM + s + RESET  : s; }

    @Override
    public String render(QuoridorState s) {
        StringBuilder sb = new StringBuilder(8192);

        // HUD: positions in logical coords
        int aR = s.p1.r / 2, aC = s.p1.c / 2;
        int bR = s.p2.r / 2, bC = s.p2.c / 2;
        int wallsA = (getOr(s, true));
        int wallsB = (getOr(s, false));

        sb.append(bold("=== Quoridor ===")).append("\n");
        sb.append(c("A@" + aR + "," + aC, FG_CYAN)).append("   ")
          .append(c("B@" + bR + "," + bC, FG_MAGENTA)).append("   ")
          .append("Walls ").append(c("A:" + wallsA, FG_CYAN)).append(" ")
          .append(c("B:" + wallsB, FG_MAGENTA)).append("\n");
        sb.append(c("Commands: ", FG_GRAY))
          .append("move r c  |  wall H r c  |  wall V r c  |  size n m  |  q")
          .append("\n\n");

        // Column indices (logical)
        sb.append("    ");
        for (int c = 0; c < s.m; c++) sb.append(String.format("%-4d", c));
        sb.append("\n");

        // Top border (sep=0)
        sb.append("    ");
        sb.append(borderRow(s, /*sep=*/0));
        sb.append("\n");

        // For each logical row r, print a cell row then the separator beneath it.
        for (int r = 0; r < s.n; r++) {
            // Cells with vertical edges
            sb.append(String.format("%-3d", r)).append(" ");

            // Left border
            sb.append(dim(V));

            for (int c = 0; c < s.m; c++) {
                // cell body: A/B or spaces
                String cell = "   ";
                if (2*r == s.p1.r && 2*c == s.p1.c) cell = " " + c("A", FG_CYAN) + " ";
                else if (2*r == s.p2.r && 2*c == s.p2.c) cell = " " + c("B", FG_MAGENTA) + " ";
                sb.append(cell);

                // interior vertical edge between (r,c) and (r,c+1): at doubled (2r, 2c+1)
                if (c < s.m - 1) {
                    boolean wall = in(s, 2*r, 2*c + 1) && s.v[2*r][2*c + 1];
                    if (wall) sb.append(c(bold(V), FG_YELLOW));
                    else      sb.append(dim(V));
                }
            }

            // Right border
            sb.append(dim(V)).append("\n");

            // Separator under row r (sep=r+1)
            sb.append("    ");
            sb.append(separatorRow(s, r));
            sb.append("\n");
        }

        return sb.toString();
    }

    /** Top/bottom border or any separator line: prints +---+---+ with walls overlaid on interior seps. */
    private String borderRow(QuoridorState s, int sep /* 0..n */) {
        // sep=0 top border (no walls), sep=n bottom border (no walls)
        StringBuilder line = new StringBuilder();
        for (int c = 0; c < s.m; c++) {
            line.append(dim(X));
            line.append(dim(H));
        }
        line.append(dim(X));
        return line.toString();
    }

    /** Separator between logical row r and r+1: show baseline grid AND highlight horizontal walls. */
    private String separatorRow(QuoridorState s, int r /* 0..n-1 */) {
        StringBuilder line = new StringBuilder();

        // Left border '+'
        line.append(dim(X));

        for (int c = 0; c < s.m; c++) {
            // horizontal wall segment at doubled (2r+1, 2c)
            boolean seg = in(s, 2*r + 1, 2*c) && s.h[2*r + 1][2*c];

            if (seg) line.append(c(bold(H), FG_YELLOW));
            else     line.append(dim(H));

            // junction between segments: draw vertical continuity if a vertical wall passes through here
            if (c < s.m - 1) {
                boolean vTop =  in(s, 2*r,   2*c + 1) && s.v[2*r][2*c + 1];
                boolean vBot =  in(s, 2*r+2, 2*c + 1) && s.v[2*r+2][2*c + 1];

                if (vTop && vBot) {
                    line.append(c(bold(V), FG_YELLOW));  // continuous vertical wall
                } else {
                    // baseline '+'; tint if any wall meets here
                    boolean meet = seg
                        || (in(s, 2*r + 1, 2*(c + 1)) && s.h[2*r + 1][2*(c + 1)])
                        || vTop;
                    String cross = meet ? c(dim(X), FG_YELLOW) : dim(X);
                    line.append(cross);
                }
            }
        }
        // Right border '+'
        line.append(dim(X));
        return line.toString();
    }

    private static boolean in(QuoridorState s, int r, int c) {
        return r >= 0 && r < s.rows && c >= 0 && c < s.cols;
    }

    // read either wallsA/wallsB (if present) or fall back to p1Walls/p2Walls
    private int getOr(QuoridorState s, boolean forA) {
        try {
            // if fields exist and non-negative, prefer them
            int wA = s.wallsA, wB = s.wallsB;
            return forA ? wA : wB;
        } catch (Throwable t) {
            // fall back to finals from earlier version
            try {
                java.lang.reflect.Field f = s.getClass().getDeclaredField(forA ? "p1Walls" : "p2Walls");
                f.setAccessible(true);
                return (int) f.get(s);
            } catch (Exception e) {
                return 0;
            }
        }
    }
}
