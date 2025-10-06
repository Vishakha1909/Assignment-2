package puzzles.dots;

import game.core.Renderer;

/**
 * ANSI-colored renderer for Dots & Boxes.
 * - Colors each claimed box with a background color block (3 chars wide).
 * - Optionally tints claimed edges by their owner.
 * - If color is disabled, uses Unicode shading as a fallback.
 *
 * NOTE: Works great in VS Code terminal and modern Windows 10+ terminals.
 * If your terminal shows weird characters, run with color=false.
 */
public final class DotsRendererAnsi implements Renderer<DotsState> {
    private final boolean color;        // true = ANSI colors, false = monochrome shading
    private final boolean colorEdges;   // also tint edges if true

    public DotsRendererAnsi(boolean color, boolean colorEdges) {
        this.color = color;
        this.colorEdges = colorEdges;
    }

    // ANSI codes
    private static final String RESET = "\u001B[0m";
    private static final String BG_RED = "\u001B[41m";
    private static final String BG_BLUE = "\u001B[44m";
    private static final String FG_RED = "\u001B[31m";
    private static final String FG_BLUE = "\u001B[34m";

    @Override public String render(DotsState s) {
        StringBuilder sb = new StringBuilder();
        sb.append("Box coords: rows 0..").append(s.rows-1)
          .append(", cols 0..").append(s.cols-1).append("\n");

        for (int r = 0; r < s.rows; r++) {
            // top edges
            for (int c = 0; c < s.cols; c++) {
                sb.append(".");
                if (s.H[r][c]) sb.append(tinted("---", s.Howner[r][c]));
                else           sb.append("   ");
            }
            sb.append(".\n");

            // verticals + boxes
            for (int c = 0; c < s.cols; c++) {
                // vertical edge
                if (s.V[r][c]) sb.append(tinted("|", s.Vowner[r][c]));
                else           sb.append(" ");

                // box content
                char owner = s.boxOwner[r][c];
                sb.append(boxFill(owner));

                // no character between boxes; right boundary handled below
            }
            // rightmost vertical boundary
            if (s.V[r][s.cols]) sb.append(tinted("|", s.Vowner[r][s.cols]));
            else                sb.append(" ");
            sb.append("\n");
        }

        // bottom edges
        for (int c = 0; c < s.cols; c++) {
            sb.append(".");
            if (s.H[s.rows][c]) sb.append(tinted("---", s.Howner[s.rows][c]));
            else                sb.append("   ");
        }
        sb.append(".\n");

        return sb.toString();
    }

    // Paint a box with color or shading; keep width=3 to match cell spacing
    private String boxFill(char owner) {
        if (!color) {
            // Monochrome shading: A = ▓▓▓, B = ░░░, empty = spaces
            if (owner == 'A') return "▓▓▓";
            if (owner == 'B') return "░░░";
            return "   ";
        }
        if (owner == 'A') return BG_RED + "   " + RESET;
        if (owner == 'B') return BG_BLUE + "   " + RESET;
        return "   ";
    }

    // Tint edges with the owner's color (foreground); else return raw
    private String tinted(String raw, char owner) {
        if (!color || !colorEdges) return raw;
        if (owner == 'A') return FG_RED + raw + RESET;
        if (owner == 'B') return FG_BLUE + raw + RESET;
        return raw; // unclaimed
    }
}
