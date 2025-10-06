package puzzles.dots;

import game.core.ConsoleIO;
import game.core.Game;
import game.core.Renderer;

/**
 * Dots & Boxes (no dot-ID input).
 * Supported inputs during play:
 *   • Box + side:   r c side     (side -> T,B,L,R or top/bottom/left/right) recommended
 *   • Legacy:       H r c   or   V r c
 *
 * Commands:
 *   • avail [options]   → list unclaimed edges (see showRules for variants)
 *   • edges             → list claimed edges
 *   • rules             → show rules
 *   • q                 → end round
 */
public final class DotsGame implements Game {
    private final ConsoleIO io;
    private final DotsRules rules = new DotsRules();

    // Choose renderer:
    // private final Renderer<DotsState> renderer = new DotsRenderer();              // plain ASCII
    private final Renderer<DotsState> renderer = new DotsRendererAnsi(true, true);  // color boxes + tint edges

    private final SessionStats stats;

    public DotsGame(ConsoleIO io, SessionStats stats) {
        this.io = io; this.stats = stats;
    }

    @Override public void run() {
        while (true) {
            io.println("\n=== Dots & Boxes ===");
            io.println("1) Play");
            io.println("2) Rules");
            io.println("3) High Scores");
            io.println("4) Back");
            io.print("Choice: ");
            String s = io.nextLine().trim();
            if (s.equals("4")) return;
            if (s.equals("2")) { showRules(); continue; }
            if (s.equals("3")) { showHighScores(); continue; }
            if (s.equals("1")) { playOnce(); continue; }
        }
    }

    // -------------------- Round --------------------

    private void playOnce() {
        // Ask size & players at start
        int rows = askInt("Boxes rows (>=1) [3]: ", 3, 1, 50);
        int cols = askInt("Boxes cols (>=1) [3]: ", 3, 1, 50);
        io.print("Player 1 name [Player1]: ");
        String n1 = orDefault(io.nextLine().trim(), "Player1");
        io.print("Player 2 name [Player2]: ");
        String n2 = orDefault(io.nextLine().trim(), "Player2");
        PlayerInfo p1 = new PlayerInfo(n1,'A'), p2 = new PlayerInfo(n2,'B');

        DotsState state = new DotsState(rows, cols, p1, p2);

        io.println("\nINPUT:");
        io.println("- Easiest:  r c side  (side of the box -> T,B,L,R or top/bottom/left/right)");
        io.println("- Matrix wise:   H r c   or   V r c");
        io.println("- Commands: avail | edges | rules | q");

        while (true) {
            io.println(renderer.render(state));
            if (rules.isTerminal(state)) {
                io.println("Score: " + p1.name + "=" + state.score[0] + ", " + p2.name + "=" + state.score[1]);
                if (state.score[0] == state.score[1]) { io.println("It's a tie!"); stats.ties++; }
                else if (state.score[0] > state.score[1]) { io.println("Winner: " + p1.name); stats.winsA++; }
                else { io.println("Winner: " + p2.name); stats.winsB++; }
                break;
            }

            PlayerInfo cur = state.players[state.current];
            io.print(cur.name + " (" + cur.mark + ") move [r c side | H r c | V r c | avail | edges | rules | q]: ");
            String line = io.nextLine().trim();

            // Commands
            String lower = line.toLowerCase();
            if (lower.equals("q")) break;
            if (lower.equals("edges")) { listEdges(state); continue; }
            if (lower.equals("rules")) { showRules(); continue; }
            if (lower.equals("avail") || lower.startsWith("avail ")) { handleAvail(line, state); continue; }

            // Parse move (no dot IDs)
            ClaimEdge a = parseMoveNoDotIds(line, state);
            if (a == null) {
                io.println("Invalid input. Try:  r c side  (e.g., 0 1 R)  or  H r c / V r c.");
                continue;
            }

            if (!rules.isValid(state, a)) {
                io.println("Invalid: " + rules.validationError(state, a));
                continue;
            }

            state = rules.apply(state, a);
        }
    }

    // -------------------- Parsing (no dot IDs) --------------------

    /** Accepts: "r c side" (T/B/L/R or words), or "H r c"/"V r c". */
    private ClaimEdge parseMoveNoDotIds(String s, DotsState state) {
        String[] t = s.split("\\s+");
        // 1) Box + side: r c side
        if (t.length == 3 && isInt(t[0]) && isInt(t[1])) {
            Side side = Side.parse(t[2]);
            if (side != null) {
                int r = Integer.parseInt(t[0]);
                int c = Integer.parseInt(t[1]);
                return edgeFromBoxSide(r, c, side, state.rows, state.cols);
            }
        }
        // 2) Legacy: H r c / V r c
        if (t.length == 3 && (t[0].equalsIgnoreCase("H") || t[0].equalsIgnoreCase("V")) && isInt(t[1]) && isInt(t[2])) {
            Orientation o = t[0].equalsIgnoreCase("H") ? Orientation.H : Orientation.V;
            int r = Integer.parseInt(t[1]), c = Integer.parseInt(t[2]);
            return new ClaimEdge(new EdgePos(o, r, c));
        }
        return null;
    }

    /** Convert (box r,c) + side → edge (with bounds). */
    private ClaimEdge edgeFromBoxSide(int r, int c, Side side, int boxRows, int boxCols) {
        if (r < 0 || r >= boxRows || c < 0 || c >= boxCols) return null;
        switch (side) {
            case TOP:    return new ClaimEdge(new EdgePos(Orientation.H, r,     c));
            case BOTTOM: return new ClaimEdge(new EdgePos(Orientation.H, r + 1, c));
            case LEFT:   return new ClaimEdge(new EdgePos(Orientation.V, r,     c));
            case RIGHT:  return new ClaimEdge(new EdgePos(Orientation.V, r,     c + 1));
            default:     return null;
        }
    }

    // -------------------- 'avail' listing (no dot-ID column) --------------------

    private void handleAvail(String line, DotsState state) {
        // Parse options: avail [all|critical|N] [row K|col K]
        String[] t = line.trim().split("\\s+");
        boolean criticalOnly = false;
        int limit = 20;  // default
        String filterKind = null; int filterVal = 0;

        for (int i = 1; i < t.length; i++) {
            String x = t[i].toLowerCase();
            if (x.equals("all")) { limit = Integer.MAX_VALUE; continue; }
            if (x.equals("critical")) { criticalOnly = true; continue; }
            if (x.equals("row") && i+1 < t.length && isInt(t[i+1])) { filterKind="row"; filterVal=Integer.parseInt(t[++i]); continue; }
            if (x.equals("col") && i+1 < t.length && isInt(t[i+1])) { filterKind="col"; filterVal=Integer.parseInt(t[++i]); continue; }
            if (isInt(x)) { limit = Integer.parseInt(x); continue; }
        }

        java.util.List<EdgeUtils.EdgeRow> rows = EdgeUtils.listAvailable(state, criticalOnly);
        if (filterKind != null) rows = EdgeUtils.filterByRowCol(rows, filterKind, filterVal);

        io.println("\nAvailable edges " + (criticalOnly ? "(critical only) " : "") +
                "(showing " + Math.min(limit, rows.size()) + " of " + rows.size() + "):");
        io.println("  Box+Side     | H/V      | Note");
        io.println("  ------------ | -------- | ----");
        int shown = 0;
        for (EdgeUtils.EdgeRow e : rows) {
            if (shown++ >= limit) break;
            String note = e.critical ? "closes a box" : "";
            io.println(String.format("  %-12s | %-8s | %s", e.boxSidePrimary, e.hv, note));
        }
        io.println("");
    }

    // -------------------- UI helpers --------------------

    private void listEdges(DotsState s) {
        io.println("Claimed H edges (r,c,by):");
        for (int r=0;r<=s.rows;r++) for (int c=0;c<s.cols;c++)
            if (s.H[r][c]) io.println("  H "+r+" "+c+" by "+(s.Howner[r][c]==0?'-':s.Howner[r][c]));
        io.println("Claimed V edges (r,c,by):");
        for (int r=0;r<s.rows;r++) for (int c=0;c<=s.cols;c++)
            if (s.V[r][c]) io.println("  V "+r+" "+c+" by "+(s.Vowner[r][c]==0?'-':s.Vowner[r][c]));
    }

    private void showRules() {
        io.println("\nRules:");
        io.println("- Players take turns claiming edges.");
        io.println("- Completing a box scores 1 and grants an extra turn.");
        io.println("- Game ends when all boxes are claimed. Highest score wins.");
        io.println("\nInput options:");
        io.println("  -> Box + side (recommended):  r c side   sides that are T,B,L,R or top/bottom/left/right");
        io.println("  -> Matrix:                    H r c   or   V r c");
        io.println("  -> Commands: 'avail' (list), 'edges' (claimed), 'rules' (help), 'q' (end round)");
    }

    private void showHighScores() {
        io.println("\nSession Scores:");
        io.println("Wins A=" + stats.winsA + ", Wins B=" + stats.winsB + ", Ties=" + stats.ties);
    }

    // -------------------- small utils --------------------

    private int askInt(String prompt, int def, int min, int max) {
        io.print(prompt);
        String s = io.nextLine().trim();
        if (s.isEmpty()) return def;
        try { int v = Integer.parseInt(s); if (v<min) v=min; if (v>max) v=max; return v; }
        catch(Exception e){ return def; }
    }
    private static boolean isInt(String s) {
        try { Integer.parseInt(s); return true; } catch (Exception e) { return false; }
    }
    private static String orDefault(String s, String def){ return s.isEmpty()? def : s; }
}
