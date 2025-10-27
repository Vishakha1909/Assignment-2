package puzzles.quoridor;

import game.core.Renderer;

/** Pretty boxed renderer with ANSI colors and ASCII fallback. */
public final class QuoridorRenderer implements Renderer<QuoridorState> {

    private final boolean useUnicode;
    private final boolean useColor;
    private final boolean tintWalls;

    private final String H, V, X;
    private static final String SP3 = "   ";

    private static final String RESET = "\u001B[0m";
    private static final String BOLD  = "\u001B[1m";
    private static final String DIM   = "\u001B[2m";
    private static final String FG_CYAN    = "\u001B[36m";
    private static final String FG_MAGENTA = "\u001B[35m";
    private static final String FG_YELLOW  = "\u001B[33m";
    private static final String FG_GRAY    = "\u001B[90m";

    public QuoridorRenderer(boolean preferUnicode, boolean useColor, boolean tintWalls) {
        boolean isWindows = System.getProperty("os.name","").toLowerCase().contains("win");
        this.useUnicode = preferUnicode && !isWindows;
        this.useColor   = useColor;
        this.tintWalls  = useColor && tintWalls;
        if (this.useUnicode) { H="───"; V="│"; X="┼"; } else { H="---"; V="|"; X="+"; }
    }

    private String c(String s, String color) { return useColor ? color + s + RESET : s; }
    private String bold(String s)            { return useColor ? BOLD + s + RESET : s; }
    private String dim(String s)             { return useColor ? DIM + s + RESET  : s; }

    private static String initialOf(String name, String fallback) {
        if (name == null || name.trim().isEmpty()) return fallback;
        return String.valueOf(Character.toUpperCase(name.trim().charAt(0)));
    }

    @Override
    public String render(QuoridorState s) {
        StringBuilder sb = new StringBuilder(8192);
        String i1 = initialOf(s.name1, "A");
        String i2 = initialOf(s.name2, "B");

        // HUD
        sb.append(c(s.name1, FG_CYAN)).append(" @(").append(s.p1.r).append(",").append(s.p1.c).append(")")
          .append("  Walls: ").append(s.walls1).append("    ");
        sb.append(c(s.name2, FG_MAGENTA)).append(" @(").append(s.p2.r).append(",").append(s.p2.c).append(")")
          .append("  Walls: ").append(s.walls2).append("\n");
        sb.append(c("Move #", FG_GRAY)).append(s.moveCount).append("   ");
        sb.append("Turn: ").append(s.turn==1? c(s.name1, FG_CYAN) : c(s.name2, FG_MAGENTA)).append("\n");
        sb.append(c("Commands: ", FG_GRAY))
          .append("move r c  |  wall H r c  |  wall V r c  |  help | quit\n\n");

        // column indices
        sb.append("    ");
        for (int cIdx=0;cIdx<s.cols;cIdx++) sb.append(String.format("%-4d", cIdx));
        sb.append("\n");

        // top border
        sb.append("    ").append(border(s.cols)).append("\n");

        for (int r=0;r<s.rows;r++) {
            // cells row
            sb.append(String.format("%-3d", r)).append(" ");
            sb.append(dim(V));
            for (int c=0;c<s.cols;c++) {
                String cell = SP3;
                if (s.p1.r==r && s.p1.c==c)      cell = " " + c(i1, FG_CYAN)    + " ";
                else if (s.p2.r==r && s.p2.c==c) cell = " " + c(i2, FG_MAGENTA) + " ";
                sb.append(cell);
                if (c < s.cols-1) {
                    boolean wall = s.v[r][c];
                    sb.append(wall ? c(bold(V), FG_YELLOW) : dim(V));
                }
            }
            sb.append(dim(V)).append("\n");

            if (r < s.rows-1) {
                sb.append("    ").append(separatorRow(s, r)).append("\n");
            }
        }
        // bottom border
        sb.append("    ").append(border(s.cols)).append("\n");
        return sb.toString();
    }

    private String border(int cols) {
        StringBuilder line = new StringBuilder();
        line.append(dim(X));
        for (int c=0;c<cols;c++) line.append(dim(H)).append(dim(X));
        return line.toString();
    }
    private String separatorRow(QuoridorState s, int r) {
        StringBuilder line = new StringBuilder();
        line.append(dim(X));
        for (int c=0;c<s.cols;c++) {
            boolean hSeg = s.h[r][c];
            line.append(hSeg ? c(bold(H), FG_YELLOW) : dim(H));
            if (c < s.cols-1) {
                boolean vTop = s.v[r][c];
                boolean vBot = s.v[r+1][c];
                if (vTop && vBot) line.append(c(bold(V), FG_YELLOW));
                else {
                    boolean meets = hSeg || s.h[r][c+1] || vTop || vBot;
                    line.append(meets ? c(dim(X), FG_YELLOW) : dim(X));
                }
            }
        }
        line.append(dim(X));
        return line.toString();
    }
}
