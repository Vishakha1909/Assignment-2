package puzzles.dots;

import game.core.ConsoleIO;
import game.core.Game;
import game.core.Renderer;

/**
 * Dots & Boxes (strict input: only "H r c" or "V r c").
 *
 * Commands during play:
 *   • avail [all|critical|N] [row k|col k]  → list unclaimed edges
 *   • edges                                 → list claimed edges
 *   • rules                                 → show rules/help
 *   • q                                     → end round
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
        int rows = askInt("Boxes rows (>=1) [3]: ", 3, 1, 50);
        int cols = askInt("Boxes cols (>=1) [3]: ", 3, 1, 50);
        io.print("Player 1 name [Player1]: ");
        String n1 = orDefault(io.nextLine().trim(), "Player1");
        io.print("Player 2 name [Player2]: ");
        String n2 = orDefault(io.nextLine().trim(), "Player2");
        PlayerInfo p1 = new PlayerInfo(n1,'A'), p2 = new PlayerInfo(n2,'B');

        DotsState state = new DotsState(rows, cols, p1, p2);

        io.println("");
        io.println("INPUT (strict):");
        io.println("  H r c   → horizontal edge at dot-row r, between cols c and c+1");
        io.println("  V r c   → vertical edge at dot-col c, between rows r and r+1");
        io.println("Ranges:  H r∈[0.." + rows + "], c∈[0.." + (cols-1) + "]   |   V r∈[0.." + (rows-1) + "], c∈[0.." + cols + "]");
        io.println("Commands: avail | edges | rules | q");
        io.println("");

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
            io.print(cur.name + " (" + cur.mark + ") move [H r c | V r c | avail | edges | rules | q]: ");
            String line = io.nextLine().trim();
            String lower = line.toLowerCase();

            // Commands
            if (lower.equals("q")) break;
            if (lower.equals("edges")) { listEdges(state); continue; }
            if (lower.equals("rules")) { showRules(); continue; }
            if (lower.equals("avail") || lower.startsWith("avail ")) { handleAvail(line, state); continue; }

            // Strict H/V parsing only
            ClaimEdge a = parseHV(line);
            if (a == null) {
                io.println("Invalid input. Use: H r c  or  V r c.  Example:  H 1 0");
                continue;
            }
            if (!rules.isValid(state, a)) {
                io.println("Invalid: " + rules.validationError(state, a));
                continue;
            }
            state = rules.apply(state, a);
        }
    }

    // -------------------- Parsing (H/V only) --------------------

    /** Accepts only "H r c" or "V r c". */
    private ClaimEdge parseHV(String s) {
        String[] t = s.trim().split("\\s+");
        if (t.length != 3) return null;
        String hv = t[0];
        if (!hv.equalsIgnoreCase("H") && !hv.equalsIgnoreCase("V")) return null;
        if (!isInt(t[1]) || !isInt(t[2])) return null;
        int r = Integer.parseInt(t[1]);
        int c = Integer.parseInt(t[2]);
        Orientation o = hv.equalsIgnoreCase("H") ? Orientation.H : Orientation.V;
        return new ClaimEdge(new EdgePos(o, r, c));
    }

    // -------------------- 'avail' listing --------------------

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
    io.println("  H/V move  | Note");
    io.println("  --------- | ----");
    int shown = 0;
    for (EdgeUtils.EdgeRow e : rows) {
        if (shown++ >= limit) break;
        String note = e.critical ? "closes a box" : "";
        io.println(String.format("  %-9s | %s", e.hv, note));
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
        io.println("\nInput (strict):");
        io.println("  • H r c   or   V r c");
        io.println("    Ranges:  H r∈[0..rows], c∈[0..cols-1]   |   V r∈[0..rows-1], c∈[0..cols]");
        io.println("  • Commands: 'avail' (list), 'edges' (claimed), 'rules' (help), 'q' (end round)");
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
