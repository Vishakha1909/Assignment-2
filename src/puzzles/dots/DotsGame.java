/**
 * Project: Game Hub
 * File: DotsGame.java
 * Purpose: Terminal loop for Dots & Boxes.
 */
package puzzles.dots;

import game.core.ConsoleIO;
import game.core.Game;
import game.core.Renderer;

public final class DotsGame implements Game {
    private final ConsoleIO io;
    private final DotsRules rules = new DotsRules();
    //private final Renderer<DotsState> renderer = new DotsRenderer();
    private final Renderer<DotsState> renderer = new DotsRendererAnsi(
    true,   // color on (set false for monochrome shading)
    true    // tint edges too (set false to color only boxes)
);
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

    private void playOnce() {
        // ask for config NOW (after Play)
        int rows = askInt("Boxes rows (>=1) [3]: ", 3, 1, 50);
        int cols = askInt("Boxes cols (>=1) [3]: ", 3, 1, 50);
        io.print("Player 1 name [Player1]: ");
        String n1 = orDefault(io.nextLine().trim(), "Player1");
        io.print("Player 2 name [Player2]: ");
        String n2 = orDefault(io.nextLine().trim(), "Player2");
        PlayerInfo p1 = new PlayerInfo(n1,'A'), p2 = new PlayerInfo(n2,'B');

        DotsState state = new DotsState(rows, cols, p1, p2);
        io.println("Input: H r c  or  V r c   (q to end round; 'edges' to list claimed edges)");
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
            io.print(cur.name + " (" + cur.mark + ") edge [H/V] row col or q/edges: ");
            String line = io.nextLine().trim();
            if (line.equalsIgnoreCase("q")) break;
            if (line.equalsIgnoreCase("edges")) { listEdges(state); continue; }

            String[] tok = line.split("\\s+");
            if (tok.length != 3) { io.println("Format: H r c  or  V r c"); continue; }
            Orientation o = tok[0].equalsIgnoreCase("H") ? Orientation.H : Orientation.V;
            int r, c; try { r = Integer.parseInt(tok[1]); c = Integer.parseInt(tok[2]); }
            catch (Exception e) { io.println("Use integers for row and col."); continue; }

            ClaimEdge a = new ClaimEdge(new EdgePos(o,r,c));
            if (!rules.isValid(state, a)) { io.println("Invalid: " + rules.validationError(state, a)); continue; }
            state = rules.apply(state, a);
        }
    }

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
        io.println("- If you complete a box, you score 1 and take another turn.");
        io.println("- Game ends when all boxes are claimed. Highest score wins.");
        io.println("- Input: H r c  or  V r c. Type 'edges' to list claimed edges.");
    }

    private void showHighScores() {
        io.println("\nSession Scores:");
        io.println("Wins A=" + stats.winsA + ", Wins B=" + stats.winsB + ", Ties=" + stats.ties);
    }

    private int askInt(String prompt, int def, int min, int max) {
        io.print(prompt);
        String s = io.nextLine().trim();
        if (s.isEmpty()) return def;
        try { int v = Integer.parseInt(s); if (v<min) v=min; if (v>max) v=max; return v; }
        catch(Exception e){ return def; }
    }
    private static String orDefault(String s, String def){ return s.isEmpty()? def : s; }
}
