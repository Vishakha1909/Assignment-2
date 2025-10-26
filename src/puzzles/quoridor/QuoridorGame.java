package puzzles.quoridor;

import game.core.ConsoleIO;
import game.core.Game;
import game.core.Position;

/** Quoridor with a submenu (Play / Rules / Stats / Back) and session stats. */
public final class QuoridorGame implements Game {
    private final ConsoleIO io;
    private final QuoridorRules rules = new QuoridorRules();
    private QuoridorState state;                   // current board (set at Play)
    private QuoridorRenderer renderer;             // keep one renderer instance

    public QuoridorGame(ConsoleIO io) {
        this.io = io;
        this.renderer = new QuoridorRenderer(/*unicode*/true, /*color*/true, /*tintWalls*/true);
        this.state = new QuoridorState(9, 9);      // default size kept between rounds
    }

    @Override public void run() {
        while (true) {
            switch (submenu()) {
                case 1: playRound();  break;
                case 2: showRules();  break;
                case 3: showStats();  break;
                case 4: return;       // back to main menu
            }
        }
    }

    // ===== Submenu =====
    private int submenu() {
        io.println("\n=== Quoridor ===");
        io.println("1) Play");
        io.println("2) Rules");
        io.println("3) Stats");
        io.println("4) Back");
        while (true) {
            io.print("Choice [1-4]: ");
            String s = safeRead();
            if (s.length() == 1 && s.charAt(0) >= '1' && s.charAt(0) <= '4') return s.charAt(0) - '0';
        }
    }

    private void showRules() {
        io.println("\nRules (quick):");
        io.println("- Board n×m cells (default 9×9).");
        io.println("- A starts top center; B starts bottom center.");
        io.println("- On your turn enter one of:");
        io.println("    move r c");
        io.println("    wall h r c     (horizontal wall spans two cells)");
        io.println("    wall v r c     (vertical   wall spans two cells)");
        io.println("    size n m       (resize mid-round)");
        io.println("- You may not place a wall that blocks all paths.");
        io.println("- A wins by reaching last row; B wins by reaching row 0.");
    }

    private void showStats() {
        io.println("\n=== Quoridor — Session Stats ===");
        io.println(QuoridorStats.get().summary());
    }

    // ===== Round =====
    private void playRound() {
        // ask size once per Play, default = current state's size
        int n = state.rows, m = state.cols;
        io.print("Enter board size 'n m' (default " + n + " " + m + "): ");
        String sizeLine = safeRead();
        try {
            String[] parts = sizeLine.trim().split("\\s+");
            if (parts.length == 1 && !parts[0].isEmpty()) {
                int s = Integer.parseInt(parts[0]); if (s >= 5) { n = s; m = s; }
            } else if (parts.length >= 2) {
                n = Integer.parseInt(parts[0]); m = Integer.parseInt(parts[1]);
            }
        } catch (Exception ignore) {}
        state = new QuoridorState(n, m);

        int movesThisGame = 0;
        long t0 = System.currentTimeMillis();
        QuoridorStats.get().onGameStart();

        io.println(renderer.render(state));

        while (true) {
            if (isTerminal(state)) {
                boolean aWon = (state.p1.r == state.rows - 1);
                endGame(aWon, movesThisGame, t0);
                return;
            }

            io.print("P" + state.turn + "> ");
            String line = safeRead().trim();
            if (line.equalsIgnoreCase("q") || line.equalsIgnoreCase("quit")) return;
            if (line.equalsIgnoreCase("help")) { showRules(); continue; }

            try {
                String[] t = line.split("\\s+");
                String cmd = t[0].toLowerCase();

                if ("move".equals(cmd)) {
                    int r = Integer.parseInt(t[1]), c = Integer.parseInt(t[2]);
                    QuoridorAction a = QuoridorAction.move(state.currentPawn(), new Position(r, c));
                    String err = rules.validationError(state, a);
                    if (err != null) {
                        io.println("Invalid: " + err);
                    } else {
                        state = rules.apply(state, a);
                        movesThisGame++;
                        io.println(renderer.render(state));
                    }
                } else if ("wall".equals(cmd)) {
                    char hv = Character.toLowerCase(t[1].charAt(0));
                    int r = Integer.parseInt(t[2]), c = Integer.parseInt(t[3]);
                    QuoridorAction a = (hv == 'h') ? QuoridorAction.wallH(r, c) : QuoridorAction.wallV(r, c);
                    String err = rules.validationError(state, a);
                    if (err != null) {
                        io.println("Invalid: " + err);
                    } else {
                        state = rules.apply(state, a);
                        QuoridorStats.get().onWallPlaced();
                        io.println(renderer.render(state));
                    }
                } else if ("size".equals(cmd)) {
                    int rn = Integer.parseInt(t[1]), rm = Integer.parseInt(t[2]);
                    state = new QuoridorState(rn, rm);
                    io.println("Resized to " + rn + "x" + rm + ".");
                    io.println(renderer.render(state));
                } else {
                    io.println("Unknown command. Try: move r c | wall h r c | wall v r c | size n m | q");
                }
            } catch (Exception ex) {
                io.println("Parse error. Try: move r c | wall h r c | wall v r c | size n m | q");
            }
        }
    }

    private boolean isTerminal(QuoridorState s) {
        return (s.p1.r == s.rows - 1) || (s.p2.r == 0);
    }

    private void endGame(boolean aWon, int movesThisGame, long t0) {
        long dt = System.currentTimeMillis() - t0;
        QuoridorStats.get().onGameEnd(aWon, movesThisGame, dt);
        io.println(renderer.render(state));
        io.println(aWon ? "Winner: A" : "Winner: B");
        io.println("Moves: " + movesThisGame + "   Time: " + String.format("%.2fs", dt/1000.0));
    }

    private String safeRead() { String s = io.nextLine(); return (s == null) ? "" : s; }
}
